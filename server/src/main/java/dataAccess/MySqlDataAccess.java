package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class MySqlDataAccess {
    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    //    User methods
    public void createUser(User user) throws DataAccessException{
        var statement = "INSERT INTO users (username, email, password, json) VALUES (?, ?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        User newUser = user.updatePassword(hashedPassword);
        var json = new Gson().toJson(newUser);
        executeUpdate(statement, user.username(), user.email(), hashedPassword, json);
    }

    public User getUser(String user) throws DataAccessException{

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readJson(rs, User.class);
                    }
//                    else {
//                        throw new DataAccessException("User not found: " + user);
//                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    //    Auth methods
    public boolean authorize(String token) throws DataAccessException{
        return getAuth(token) != null;
    }

    public Authtoken createAuth (String username) throws DataAccessException{
        String token = UUID.randomUUID().toString();
        Authtoken auth = new Authtoken(token, username);
        var statement = "INSERT INTO auths (authtoken, username, json) VALUES (?, ?, ?)";
        var json = new Gson().toJson(auth);
        try {
            executeUpdate(statement, token, username, json);
        } catch (DataAccessException e) {
            throw e;
        }
        return auth;
    }

    public Authtoken getAuth(String auth) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM auths WHERE authtoken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readJson(rs, Authtoken.class);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public void deleteAuth(Authtoken auth) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auths WHERE authtoken = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth.authToken());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    //    Game methods
    public CreateResult addGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO games (whiteusername, blackusername, gamename, json) VALUES (?, ?, ?, ?)";
        Game game = new Game(0, null, null, gameName, new ChessGame());
        var json = new Gson().toJson(game);
        var id = executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), json);

//        update game json
        Game game1 = new Game(id, null, null, gameName, new ChessGame());
        var json1 = new Gson().toJson(game1);
        var updateStatement = "UPDATE games SET json = ? WHERE id = ?";
        executeUpdate(updateStatement, json1, id);

        return new CreateResult(id);
    }

    public Game getGame(int gameID) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM games WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readJson(rs, Game.class);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    public ListResult listGames() throws DataAccessException{
        var result = new ArrayList<Game>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readJson(rs, Game.class));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        ListResult list = new ListResult(result);
        return list;
    }

    public void joinGame(String username, JoinRequest request) throws DataAccessException {
        String updateStatement;
        Game game = getGame(request.gameID());
        if (request.playerColor() == ChessGame.TeamColor.WHITE){
            updateStatement = "UPDATE games SET whiteusername = ? WHERE id = ?";
            game = game.updateWhiteUsername(username);
        }
        else {
            updateStatement = "UPDATE games SET blackusername = ? WHERE id = ?";
            game = game.updateBlackUsername(username);
        }
        executeUpdate(updateStatement, username, request.gameID());

//        update game json
        var json = new Gson().toJson(game);
        var jsonUpdateStatement = "UPDATE games SET json = ? WHERE id = ?";
        executeUpdate(jsonUpdateStatement, json, game.gameID());
    }

    //    clear methods
    public void clear() throws DataAccessException {
        deleteGames();
        deleteAuths();
        deleteUsers();
    }

    private void deleteAuths() throws DataAccessException {
        var statement = "DELETE from auths";
        executeUpdate(statement);
    }

    private void deleteUsers() throws DataAccessException {
        var statement = "DELETE from users";
        executeUpdate(statement);
    }

    private void deleteGames() throws DataAccessException {
        var statement = "DELETE from games";
        executeUpdate(statement);
        var statement2 = "ALTER TABLE games AUTO_INCREMENT = 1";
        executeUpdate(statement2);
    }


//    deserialization
    private <T> T readJson(ResultSet rs, Class<T> cla) throws DataAccessException {
        String json;
        try {
            json = rs.getString("json");
        } catch (SQLException e) {
            throw new DataAccessException("Error reading JSON from ResultSet");
        }
        return new Gson().fromJson(json, cla);
    }


//SQL
    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `json` json NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
                    """
            CREATE TABLE IF NOT EXISTS  games (
              `id` int NOT NULL AUTO_INCREMENT,
              `whiteusername` varchar(256),
              `blackusername` varchar(256),
              `gamename` varchar(256) NOT NULL,
              `json` json NOT NULL,
              PRIMARY KEY (`id`),
              FOREIGN KEY (`whiteusername`) REFERENCES users(`username`),
              FOREIGN KEY (`blackusername`) REFERENCES users(`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
                    """
            CREATE TABLE IF NOT EXISTS  auths (
              `authtoken` varchar(256) NOT NULL,
              `username` varchar(256),
              `json` json NOT NULL,
              PRIMARY KEY (`authtoken`),
              FOREIGN KEY (`username`) REFERENCES users(`username`)
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