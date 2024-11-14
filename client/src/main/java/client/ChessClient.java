package client;

import java.util.Arrays;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final Repl repl;
    private State state = State.LOGGEDDOUT;

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
            return switch (cmd) {
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register (String... params) throws Exception {
        if (params.length >=3){
            server.register(params);
            return "You successfully registered";
        }
        throw new Exception("register failed");
    }

    public String help() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL> - creates account
                - login <USERNAME> <PASSWORD> - logs in
                - quit - exits out
                - help - displays possible commands
                """;
    }
}
