import chess.ChessGame;
import chess.ChessPiece;
import server.Server;
import dataaccess.*;
import service.ChessService;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws DataAccessException {

        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }
            var server = new Server().run(port);
            System.out.printf("Started server on port: %s%n", port);
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}