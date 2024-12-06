package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.Authtoken;
import model.Game;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;


@WebSocket
public class WebSocketHandler {
  private  Session session;

  private final ConnectionManager connections = new ConnectionManager();

  public WebSocketHandler() {}

  @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
      this.session = session;

     UserGameCommand action = new Gson().fromJson(message, UserGameCommand.class);
      switch (action.getCommandType()) {
          case CONNECT -> connect(action);
          case MAKE_MOVE -> makeMove(action);
          case LEAVE -> leave(action);
          case RESIGN -> resign(action);
      }
    }


    private void connect(UserGameCommand action) throws Exception {
      String auth = action.getAuthToken();
      Integer gameID = action.getGameID();
      Authtoken token = Server.dataAccess.getAuth(auth);
      if (token == null){
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Authtoken is invalid");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }
      String user = token.username();

      connections.add(user, gameID, this.session);
      ChessGame game;
      try {
        game = Server.dataAccess.getGame(gameID).game();
      } catch (Exception e){
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Game ID is invalid");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }

      var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
      session.getRemote().sendString(new Gson().toJson(notification));

      String message = "player " + user + " has joined game " + gameID;

      notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
      connections.broadcast(user, gameID, notification);
    }

    private void makeMove(UserGameCommand action) throws Exception {
      ServerMessage notification;
      String auth = action.getAuthToken();
      Authtoken token = Server.dataAccess.getAuth(auth);
      if (token == null){
        notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Invalid AuthToken");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }

      String user = token.username();
      Integer gameID = action.getGameID();
      ChessMove move = action.getMove();
      Game game = Server.dataAccess.getGame(gameID);

      if (game.gameOver() == 1){
        notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Game is Over");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }

      ChessGame chessGame = game.game();
      Collection<ChessMove> moves = chessGame.validMoves(move.getStartPosition());

      if (!moves.contains(move)){
        notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Move is invalid");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }

      String whiteUser = game.whiteUsername();
      String blackUser = game.blackUsername();
      ChessGame.TeamColor turn = chessGame.getTeamTurn();
      if (turn == ChessGame.TeamColor.WHITE && !Objects.equals(whiteUser, user)){
        notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Please wait for your turn");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }
      else if (turn == ChessGame.TeamColor.BLACK && !Objects.equals(blackUser, user)){
        notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Please wait for your turn");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }
      else if (chessGame.getBoard().getPiece(move.getStartPosition()).getTeamColor() != turn) {
        notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "You can only move your pieces!");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }
      else if (chessGame.isInStalemate(ChessGame.TeamColor.WHITE) || chessGame.isInStalemate(ChessGame.TeamColor.BLACK)){
        notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "The Game is already over! Stalemate");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }
      else if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) || chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)){
        notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "The Game is already over! Checkmate");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }
      chessGame.makeMove(move);
      Server.dataAccess.updateGame(gameID, game);
      notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
      connections.broadcast(null, gameID, notification);

      if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE)){
        notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, move + "White is in Checkmate!");
        connections.broadcast(user, gameID, notification);
        game = game.updateGameOver();
        Server.dataAccess.updateGame(gameID, game);
        return;
      }
      else if (chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)){
        notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, move + "Black is in Checkmate!");
        connections.broadcast(user, gameID, notification);
        game = game.updateGameOver();
        Server.dataAccess.updateGame(gameID, game);
        return;
      }
      else if (chessGame.isInCheck(ChessGame.TeamColor.BLACK)){
        notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, move + "Black is in check");
        connections.broadcast(user, gameID, notification);
        return;
      }
      else if (chessGame.isInCheck(ChessGame.TeamColor.WHITE)){
        notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, move + "White is in check");
        connections.broadcast(user, gameID, notification);
        return;
      }
      else if (chessGame.isInStalemate(ChessGame.TeamColor.WHITE) || chessGame.isInStalemate(ChessGame.TeamColor.BLACK)){
        notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, move + "Stalemate!");
        connections.broadcast(user, gameID, notification);
        game = game.updateGameOver();
        Server.dataAccess.updateGame(gameID, game);
        return;
      }
      notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, move.toString());
      connections.broadcast(user, gameID, notification);
    }

    private void leave(UserGameCommand action) throws DataAccessException, IOException {
      Integer gameID = action.getGameID();
      Authtoken token = Server.dataAccess.getAuth(action.getAuthToken());
      String user = token.username();

      Game game = Server.dataAccess.getGame(gameID);
      if (Objects.equals(game.blackUsername(), user)){
        game = game.updateBlackUsername(null);
      }
      else if (Objects.equals(game.whiteUsername(), user)){
        game = game.updateWhiteUsername(null);
      }

      Server.dataAccess.updateGame(gameID, game);

      ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, user + "has left the game");
      connections.broadcast(user, gameID, notification);

      connections.remove(user);
    }

    private void resign(UserGameCommand action) throws IOException, DataAccessException {
      ServerMessage notification;
      Integer gameID = action.getGameID();
      Authtoken token = Server.dataAccess.getAuth(action.getAuthToken());
      String user = token.username();
      Game game = Server.dataAccess.getGame(gameID);

      if (game.gameOver() == 1){
        notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Cannot resign. The Game is already over");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }

      if (!Objects.equals(game.whiteUsername(), user) && !Objects.equals(game.blackUsername(), user)){
        notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Cannot resign. You are an observer");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }

      if (Objects.equals(game.blackUsername(), user)){
        game = game.updateBlackUsername(null);
      }
      else if (Objects.equals(game.whiteUsername(), user)){
        game = game.updateWhiteUsername(null);
      }
      game = game.updateGameOver();
      Server.dataAccess.updateGame(gameID, game);

      notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, user + "has resigned the game");
      connections.broadcast(null, gameID, notification);
      connections.remove(user);
    }
}
