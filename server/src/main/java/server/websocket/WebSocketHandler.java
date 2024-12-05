package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.Authtoken;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;


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

    private void makeMove(UserGameCommand action) throws DataAccessException, IOException {
        ChessMove move = action.getMove();
        String auth = action.getAuthToken();
        Integer gameID = action.getGameID();
        Authtoken token = Server.dataAccess.getAuth(auth);
        if (token == null){
          ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Authtoken is invalid");
          session.getRemote().sendString(new Gson().toJson(notification));
          return;
        }

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