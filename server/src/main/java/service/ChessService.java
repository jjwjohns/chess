package service;

import dataAccess.DataMemory;
import model.Authtoken;
import model.User;
import org.eclipse.jetty.security.authentication.AuthorizationService;

public class ChessService {
    private final DataMemory dataAccess;

    public ChessService(DataMemory dataAccess){
        this.dataAccess = dataAccess;
    }
    public Authtoken register(User user) throws Exception{
        if (dataAccess.getUser(user) == null){
            dataAccess.createUser(user);
            Authtoken auth = dataAccess.createAuth(user.username());
            return auth;
        }
        throw new Exception("not implemented (service)");
    }

    public void clear() throws Exception{
        dataAccess.deleteAuths();
        dataAccess.deleteUsers();
        dataAccess.deleteGames();
    }
}