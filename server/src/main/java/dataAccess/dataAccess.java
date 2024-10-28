package dataAccess;
import model.*;


public interface DataAccess {
    void createUser(User user) throws DataAccessException;

    User getUser(String user) throws DataAccessException;

    Authtoken createAuth(String username) throws DataAccessException;

    Authtoken getAuth(String auth) throws DataAccessException;

    void deleteAuths() throws DataAccessException;

    void deleteUsers() throws DataAccessException;

    void deleteGames() throws DataAccessException;
}