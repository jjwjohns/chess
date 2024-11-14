package UI;

import chess.ChessGame;
import model.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final Repl repl;
    private State state = State.LOGGEDOUT;
    private Authtoken auth;
    private List<Game> list;

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

    public String register (String... params) throws Exception {
        if (params.length >=3){
            this.auth = server.register(params);
            state = State.LOGGEDIN;
            return "successfully registered";
        }
        throw new Exception("register failed");
    }

    public String login(String... params) throws Exception {
        if (params.length >=2){
            this.auth = server.login(params);
            state = State.LOGGEDIN;
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

            int index = Integer.parseInt(params[0])-1;
            int ID = list.get(index).gameID();
            server.play(auth, ID, color);
            return "Joined game successfully";
        }
        throw new Exception("play failed");
    }

    private String observe(String... params) throws Exception{
        if (params.length >= 1){
            int index = Integer.parseInt(params[0])-1;
            int ID = list.get(index).gameID();
            return server.observe(ID);
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
        return """
                - logout - exits when you are done
                - create <GAMENAME> - creates a game
                - list - lists all active games
                - play <GAMEID> <WHITE|BLACK> - joins a game according to ID
                - observe <GAMEID> - observe active game
                - quit - exits
                - help - displays possible commands
                """;
    }
}
