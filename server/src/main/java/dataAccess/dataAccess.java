package dataAccess;
import chess.ChessGame;
import model.*;


public interface DataAccess {
    boolean authorize(String token) throws DataAccessException;

    void createUser(User user) throws DataAccessException;

    User getUser(String user) throws DataAccessException;

    Authtoken createAuth(String username) throws DataAccessException;

    Authtoken getAuth(String auth) throws DataAccessException;

    void deleteAuth(Authtoken token) throws DataAccessException;

    CreateResult addGame(String gameName) throws DataAccessException;

    Game getGame(int gameID) throws DataAccessException;

    void deleteGame(Integer gameID) throws DataAccessException;

    void joinGame(String username, JoinRequest request) throws DataAccessException;

    void deleteAuths() throws DataAccessException;

    void deleteUsers() throws DataAccessException;

    void deleteGames() throws DataAccessException;
}