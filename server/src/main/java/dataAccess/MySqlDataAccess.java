package dataaccess;

import chess.ChessGame;
import model.*;


public class MySqlDataAccess {
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
}
