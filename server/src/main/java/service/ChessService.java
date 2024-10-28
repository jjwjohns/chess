package service;

import dataAccess.DataAccessException;
import dataAccess.DataMemory;
import model.*;

import java.util.Objects;

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

    public Authtoken login(LoginRequest login) throws DataAccessException{
        User user = dataAccess.getUser(login.username());
        if (user == null || !Objects.equals(user.password(), login.password())){
            return null;
        }
        return dataAccess.createAuth(user.username());
    }

    public Integer logout(String token) throws DataAccessException{
        Authtoken auth = dataAccess.getAuth(token);
        dataAccess.deleteAuth(auth);
        return 200;
    }

    public CreateResult createGame(CreateRequest request) throws DataAccessException{
        return dataAccess.addGame(request.gameName());
    }
}