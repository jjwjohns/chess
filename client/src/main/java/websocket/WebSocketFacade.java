package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import ui.DrawBoard;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.*;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;

    public WebSocketFacade(String url) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String s) {
                    ServerMessage notification = new Gson().fromJson(s, ServerMessage.class);
                    handleMessage(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new RuntimeException();
        }
    }

    private void handleMessage(ServerMessage notification) {
        if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME){
            System.out.println(SET_TEXT_COLOR_GREEN + new Gson().toJson(notification.getGame()));
//            if (notification.toString().contains("White"))
            System.out.print("\n" + RESET_TEXT_COLOR);
            DrawBoard.drawWhite();
            System.out.print("\n" + RESET_TEXT_COLOR);
        }
        else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION){
            System.out.println(SET_TEXT_COLOR_GREEN + notification.getMessage());
            System.out.print("\n" + RESET_TEXT_COLOR);
        }
        else if (notification.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){
            System.out.println(SET_TEXT_COLOR_RED + notification.getErrorMessage());
            System.out.print("\n" + RESET_TEXT_COLOR);
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void join(String auth, int id) throws Exception {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, auth, id);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void leave(String auth, Integer gameNumber) throws Exception {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, gameNumber);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void resign(String auth, Integer gameNumber) throws Exception {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, auth, gameNumber);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public void move(String auth, Integer gameNumber, ChessMove move) throws Exception {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, auth, gameNumber, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }
}

