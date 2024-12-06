package ui;

import chess.ChessGame;
import model.*;
import websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final Repl repl;
    private WebSocketFacade ws;
    private State state = State.LOGGEDOUT;
    private Authtoken auth;
    private List<Game> list;
    private Integer gameNumber;


  public ChessClient(String serverUrl, Repl repl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.repl = repl;
  }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (state == State.LOGGEDOUT){
                return switch (cmd) {
                    case "quit" -> "quit";
                    case "register" -> register(params);
                    case "login" -> login(params);
                    default -> help();
                };
            }
            else if (state == State.JOINED){
                return switch (cmd) {
                    case "redraw" -> redraw();
                    case "leave" -> leave();
                    case "move" -> move();
                    case "resign" -> resign();
                    case "highlight" -> highlight();
                    default -> help();
                };
            }
            else {
                return switch (cmd) {
                    case "quit" -> "quit";
                    case "logout" -> logout();
                    case "create" -> create(params);
                    case "list" -> list();
                    case "play" -> play(params);
                    case "observe" -> observe(params);
                    default -> help();
                };
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private String highlight() {
      return "Have not implemented highlight";
    }

    private String resign() throws Exception {
      Scanner scanner = new Scanner(System.in);
      System.out.println("Are you sure you want to Resign? (Y/N)");
      String response = scanner.nextLine();
      while (true){
        if (Objects.equals(response, "Y")){
          ws.resign(auth.authToken(), gameNumber);
          return "Thanks for playing!";
        }
        else if (Objects.equals(response, "N")){
          return "You have not resigned. Keep on playing!";
        }
        else {
          System.out.println("Response must be Y or N");
          response = scanner.nextLine();
        }
      }
    }

    private String move() {
      return "Have not implemented move";
    }

    private String leave() throws Exception {
      ws.leave(auth.authToken(), gameNumber);
      state = State.LOGGEDIN;
      return "You have left";
    }

    private String redraw() {
      return "Have not implemented redraw";
    }

    public String register (String... params) throws Exception {
        if (params.length >=3){
            try {
                this.auth = server.register(params);
                state = State.LOGGEDIN;
            }
            catch (Exception e){
                throw new Exception("Invalid Register Request");
            }
            return "successfully registered";
        }
        throw new Exception("register failed");
    }

    public String login(String... params) throws Exception {
        if (params.length >=2){
            try {
                this.auth = server.login(params);
                state = State.LOGGEDIN;
            } catch (Exception e) {
                throw new Exception("Invalid Login Request");
            }
            return "successfully logged in";
        }
        throw new Exception("register failed");
    }

    private String logout() throws Exception{
        server.logout(auth);
        state = State.LOGGEDOUT;
        return "successfully logged out";
    }

    private String create(String... params) throws Exception{
        if (params.length >= 1){
            server.create(auth, params);
            return "game created";
        }
        throw new Exception("create failed");
    }

    private String list() throws Exception {
        ListResult result = server.list(auth);
        list = result.games();
        StringBuilder compiled = new StringBuilder();
        for (int i = 1; i <= list.size(); i++){
            compiled.append(i);
            compiled.append(": Game Name: ");
            compiled.append(list.get(i-1).gameName());
            compiled.append(", White Player: ");
            compiled.append(list.get(i-1).whiteUsername());
            compiled.append(", Black Player: ");
            compiled.append(list.get(i-1).blackUsername());
            compiled.append("\n");
        }
        return compiled.toString();
    }

    private String play(String... params) throws Exception{
        if (params.length >= 2){
            ChessGame.TeamColor color;
            if (params[1].toLowerCase().contains("white")){
                color = ChessGame.TeamColor.WHITE;
            }
            else if (params[1].toLowerCase().contains("black")){
                color = ChessGame.TeamColor.BLACK;
            }
            else {
                throw new Exception("color must be BLACK or WHITE");
            }

            try {
                int index = Integer.parseInt(params[0])-1;
                if (index < 0 || index >= list.size()) {
                    return "Invalid Game Number";
                }
                int id = list.get(index).gameID();
                server.play(auth, id, color);
            }
            catch (Exception e) {
                throw new Exception("Invalid Play Request");
            }

            int index = Integer.parseInt(params[0])-1;
            int id = list.get(index).gameID();

            if (color == ChessGame.TeamColor.WHITE){
//                DrawBoard.drawWhite();
                state = State.JOINED;
                this.ws = new WebSocketFacade(serverUrl);
                ws.join(auth.authToken(), id);
                gameNumber = id;
                return "\nJoined game successfully";
            }
//            DrawBoard.drawBlack();
            state = State.JOINED;

            this.ws = new WebSocketFacade(serverUrl);
            ws.join(auth.authToken(), id);
            gameNumber = id;
            return "\nJoined game successfully";
        }
        throw new Exception("play failed");
    }

    private String observe(String... params) throws Exception{
        if (params.length >= 1){
            try {
                int index = Integer.parseInt(params[0]) - 1;
                if (index <= list.size()) {
                    int id = list.get(index).gameID();
//                    DrawBoard.drawWhite();
                    state = State.JOINED;

                    this.ws = new WebSocketFacade(serverUrl);
                    ws.join(auth.authToken(), id);
                    gameNumber = id;

                    return "\n" + server.observe(id);
                }
                throw new Exception("Invalid Game Number");
            } catch (Exception e) {
                throw new Exception ("Invalid Game Number");
            }
        }
        throw new Exception("observe failed");
    }

    public String help() {
        if (state == State.LOGGEDOUT){
            return """
                - register <USERNAME> <PASSWORD> <EMAIL> - creates account
                - login <USERNAME> <PASSWORD> - logs in
                - quit - exits
                - help - displays possible commands
                """;
        }
        else if (state == State.JOINED){
            return """
                    - redraw - redraws the chess board to be more easily viewable
                    - leave - leaves the game without resigning
                    - move <PIECE> <LOCATION> <MOVE LOCATION> - moves a piece (example: move P b2 b4)
                    - resign - forfeits the game
                    - highlight <PIECE> <LOCATION> - highlights the possible moves of a piece (example: highlight P b2)
                    - help - displays possible commands
                    """;
        }
        return """
                - logout - exits when you are done
                - create <GAME NAME> - creates a game
                - list - lists all active games
                - play <GAME NUMBER> <WHITE|BLACK> - joins a game according to ID
                - observe <GAME NUMBER> - observe active game
                - quit - exits
                - help - displays possible commands
                """;
    }
}
