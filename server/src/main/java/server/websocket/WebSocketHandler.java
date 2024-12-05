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
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Authtoken is invalid");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }

      String user = token.username();

      connections.add(user, gameID, this.session);
      ChessGame game;
      try {
        game = Server.dataAccess.getGame(gameID).game();
      } catch (Exception e){
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Game ID is invalid");
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

    private void makeMove(UserGameCommand action) throws DataAccessException, IOException, InvalidMoveException {
      String auth = action.getAuthToken();
      Authtoken token = Server.dataAccess.getAuth(auth);
      String user = token.username();
      if (token == null){
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Authtoken is invalid");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }

      Integer gameID = action.getGameID();
      ChessMove move = action.getMove();
      Game game = Server.dataAccess.getGame(gameID);
      ChessGame chessGame = game.game();
      Collection<ChessMove> moves = chessGame.validMoves(move.getStartPosition());

      if (!moves.contains(move)){
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Move is invalid");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }

      String whiteUser = game.whiteUsername();
      String blackUser = game.blackUsername();
      ChessGame.TeamColor turn = chessGame.getTeamTurn();
      ServerMessage notification = null;
      if (turn == ChessGame.TeamColor.WHITE && !Objects.equals(whiteUser, user)){
        notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Please wait for your turn");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }
      else if (turn == ChessGame.TeamColor.BLACK && !Objects.equals(blackUser, user)){
        notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Please wait for your turn");
        session.getRemote().sendString(new Gson().toJson(notification));
        return;
      }

      chessGame.makeMove(move);

      Server.dataAccess.updateGame(gameID, chessGame);

      notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
      connections.broadcast(null, gameID, notification);

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