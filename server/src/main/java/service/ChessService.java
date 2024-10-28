package service;

import dataAccess.DataAccessException;
import dataAccess.DataMemory;
import model.Authtoken;
import model.User;
import spark.*;
import org.eclipse.jetty.security.authentication.AuthorizationService;

public class ChessService {
    private final DataMemory dataAccess;

    public ChessService(DataMemory dataAccess){
        this.dataAccess = dataAccess;
    }

    public Authtoken register(User user) throws DataAccessException {
        if (dataAccess.getUser(user.username()) == null){
            dataAccess.createUser(user);
            Authtoken auth = dataAccess.createAuth(user.username());
            return auth;
        }
        return null;
    }

    public void clear() throws DataAccessException{
        dataAccess.deleteAuths();
        dataAccess.deleteUsers();
        dataAccess.deleteGames();
    }
}