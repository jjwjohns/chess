package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import ui.DrawBoard;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import java.util.Collection;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import static ui.EscapeSequences.*;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {
    ChessGame game;
    String color;
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
            game = notification.getGame();
            System.out.print("\n" + RESET_TEXT_COLOR);
            if (Objects.equals(color, "white") || Objects.equals(color, "none")) {
                DrawBoard.drawWhite(game.getBoard(), null);
            }
            else {
                DrawBoard.drawBlack(game.getBoard(), null);
            }
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

    public void join(String auth, int id, String color) throws Exception {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, auth, id);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
        this.color = color;
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

    public void redraw() {
        if (Objects.equals(color, "white") || Objects.equals(color, "none")) {
            DrawBoard.drawWhite(game.getBoard(), null);
        }
        else {
            DrawBoard.drawBlack(game.getBoard(), null);
        }
    }

    public void highlight(ChessPosition pos) {
        if (game.getBoard().getPiece(pos) == null){
            System.out.println("must choose a position with a piece");
        }
        Collection<ChessMove> validMoves = game.validMoves(pos);
        Collection<ChessPosition> validPositions= new java.util.ArrayList<>(List.of());
        for (ChessMove move : validMoves){
            validPositions.add(move.getEndPosition());
        }
        validPositions.add(pos);
        if (Objects.equals(color, "white") || Objects.equals(color, "none")) {
            DrawBoard.drawWhite(game.getBoard(), validPositions);
        }
        else {
            DrawBoard.drawBlack(game.getBoard(), validPositions);
        }
    }
}

