package UI;

import model.Authtoken;

import java.util.Arrays;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final Repl repl;
    private State state = State.LOGGEDDOUT;
    private Authtoken auth;

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
            if (state == State.LOGGEDDOUT){
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

    public String help() {
        if (state == State.LOGGEDDOUT){
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
                - join <GAMEID> <WHITE|BLACK> - joins a game according to ID
                - observe <GAMEID> - observe active game
                - quit - exits
                - help - displays possible commands
                """;
    }
}
