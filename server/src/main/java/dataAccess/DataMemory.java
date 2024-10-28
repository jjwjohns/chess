package dataAccess;

import model.*;

import java.util.HashMap;
import java.util.UUID;

public class DataMemory implements DataAccess {
    private HashMap<String, User> users = new HashMap<>();
    private HashMap<String, Authtoken> authdata = new HashMap<>();
    private HashMap<String, Game> games = new HashMap<>();

    public void createUser(User user) throws DataAccessException{
        users.put(user.username(), user);
    }

    public User getUser(String user) throws DataAccessException{
        return users.get(user);
    }

    public Authtoken createAuth (String username) throws DataAccessException{
        String token = UUID.randomUUID().toString();
        Authtoken auth = new Authtoken(token, username);
        authdata.put(token, auth);
        return auth;
    }

    public Authtoken getAuth(String auth) throws DataAccessException{
        return authdata.get(auth);
    }



    public void deleteAuth(Authtoken auth) throws DataAccessException{
        authdata.remove(auth.authToken());
    }

    public void deleteAuths() throws DataAccessException {
        authdata.clear();
    }

    public void deleteUsers() throws DataAccessException {
        users.clear();
    }

    public void deleteGames() throws DataAccessException {
        games.clear();
    }
}
