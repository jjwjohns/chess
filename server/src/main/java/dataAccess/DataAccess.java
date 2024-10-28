package dataaccess;
import model.*;


public interface DataAccess {
    boolean authorize(String token) throws DataAccessException;

    //    User DataAccess methods
    void createUser(User user) throws DataAccessException;

    User getUser(String user) throws DataAccessException;

    //    Auth methods
    Authtoken createAuth(String username) throws DataAccessException;

    Authtoken getAuth(String auth) throws DataAccessException;

    void deleteAuth(Authtoken token) throws DataAccessException;

    //    Game methods
    CreateResult addGame(String gameName) throws DataAccessException;

    Game getGame(int gameID) throws DataAccessException;

    ListResult listGames() throws DataAccessException;

    void deleteGame(Integer gameID) throws DataAccessException;

    void joinGame(String username, JoinRequest request) throws DataAccessException;

    //    clear methods
    void deleteAuths() throws DataAccessException;

    void deleteUsers() throws DataAccessException;

    void deleteGames() throws DataAccessException;
}
