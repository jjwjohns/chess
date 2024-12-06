package websocket;

import com.google.gson.Gson;
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
        System.out.println("WebSocketFacade (client)");
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
            System.out.println(SET_TEXT_COLOR_GREEN + notification.getGame());
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
        System.out.println("onOpen (client)");
    }

    public void join(String auth, int id) throws Exception {
        System.out.println("join (client)");
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, auth, id);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }
}

