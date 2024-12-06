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
          case LEAVE -> leave();
          case RESIGN -> resign();
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


      System.out.println(action.getCommandType());

      var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
      session.getRemote().sendString(new Gson().toJson(notification));

      String message = "player " + user + " has joined game " + gameID;

      notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
      connections.broadcast(user, gameID, notification);
    }

    private void makeMove(UserGameCommand action) throws Exception {
      String auth = action.getAuthToken();
      Authtoken token = Server.dataAccess.getAuth(auth);
      ServerMessage notification = null;
      if (token == null){
        notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Game is Over");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }

      String user = token.username();
      Integer gameID = action.getGameID();
      ChessMove move = action.getMove();
      Game game = Server.dataAccess.getGame(gameID);
      ChessGame chessGame = game.game();
      if (chessGame == null){
        notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "bug to be fixed");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }
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
        return;
      }
      else if (chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)){
        notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, move + "Black is in Checkmate!");
        connections.broadcast(user, gameID, notification);
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
      else if (chessGame.isInStalemate(ChessGame.TeamColor.WHITE)){
        notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, move + "Stalemate!");
        connections.broadcast(user, gameID, notification);
        return;
      }
      else if (chessGame.isInStalemate(ChessGame.TeamColor.BLACK)){
        notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, move + "Stalemate!");
        connections.broadcast(user, gameID, notification);
        return;
      }

      notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, move.toString());
      connections.broadcast(user, gameID, notification);
    }

    private void leave(){
      connections.remove("user");
      System.out.println("leave not implemented");
    }

    private void resign(){
      System.out.println("resign not implemented");
    }
}

//    private void enter(String visitorName, Session session) throws IOException {
//        connections.add(visitorName, session);
//        var message = String.format("%s is in the shop", visitorName);
//        var notification = new Notification(Notification.Type.ARRIVAL, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
//}