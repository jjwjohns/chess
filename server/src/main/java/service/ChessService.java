package service;

import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class ChessService {
    private final MySqlDataAccess dataAccess;

    public ChessService(MySqlDataAccess dataAccess){
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
        dataAccess.deleteGames();
        dataAccess.deleteAuths();
        dataAccess.deleteUsers();
    }

    public Authtoken login(LoginRequest login) throws DataAccessException{
        User user = dataAccess.getUser(login.username());
        if (user == null || !BCrypt.checkpw(login.password(), user.password())){
            return null;
        }
        return dataAccess.createAuth(user.username());
    }

    public void logout(String token) throws DataAccessException{
        Authtoken auth = dataAccess.getAuth(token);
        dataAccess.deleteAuth(auth);
    }

    public CreateResult createGame(CreateRequest request) throws DataAccessException{
        return dataAccess.addGame(request.gameName());
    }

    public ListResult listGames() throws DataAccessException{
        return dataAccess.listGames();
    }

    public void joinGame(JoinRequest request, String token) throws DataAccessException{
        String username = dataAccess.getAuth(token).username();
        dataAccess.joinGame(username, request);
    }
}