package dataaccess;

import chess.ChessGame;
import model.*;

import java.sql.SQLException;


public class MySqlDataAccess {
    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public boolean authorize(String token) throws DataAccessException{
        throw new DataAccessException("not implemented");
    }

    //    User methods
    public void createUser(User user) throws DataAccessException{
        throw new DataAccessException("not implemented");
    }

    public User getUser(String user) throws DataAccessException{
        throw new DataAccessException("not implemented");
    }

    //    Auth methods
    public Authtoken createAuth (String username) throws DataAccessException{
//        String token = UUID.randomUUID().toString();
//        Authtoken auth = new Authtoken(token, username);
        throw new DataAccessException("not implemented");
    }

    public Authtoken getAuth(String auth) throws DataAccessException{
        throw new DataAccessException("not implemented");
    }

    public void deleteAuth(Authtoken auth) throws DataAccessException{
        throw new DataAccessException("not implemented");
    }

    //    Game methods
    public CreateResult addGame(String gameName) throws DataAccessException {
        throw new DataAccessException("not implemented");
    }

    public Game getGame(int gameID) throws DataAccessException{
        throw new DataAccessException("not implemented");
    }

    public void deleteGame(Integer gameID) throws DataAccessException{
        throw new DataAccessException("not implemented");
    }

    public ListResult listGames() throws DataAccessException{
        throw new DataAccessException("not implemented");
    }

    public void joinGame(String username, JoinRequest request) throws DataAccessException {
        throw new DataAccessException("not implemented");
    }

    //    clear methods
    public void deleteAuths() throws DataAccessException {
        throw new DataAccessException("not implemented");
    }

    public void deleteUsers() throws DataAccessException {
        throw new DataAccessException("not implemented");
    }

    public void deleteGames() throws DataAccessException {
        throw new DataAccessException("not implemented");
    }





    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  games (
              `id` int NOT NULL AUTO_INCREMENT,
              `whiteusername` int NOT NULL,
              `blackusername` int NOT NULL,
              `gamename` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              FOREIGN KEY (`whiteusername`) REFERENCES users(`id`),
              FOREIGN KEY (`blackusername`) REFERENCES users(`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `id` int NOT NULL AUTO_INCREMENT,
              `authtoken` varchar(256) NOT NULL,
              `username` int NOT NULL,
              PRIMARY KEY (`id`),
              FOREIGN KEY (`username`) REFERENCES users(`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
