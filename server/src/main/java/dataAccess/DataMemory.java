package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DataMemory implements DataAccess {
    private HashMap<String, User> users = new HashMap<>();
    private HashMap<String, Authtoken> authdata = new HashMap<>();
    private HashMap<Integer, Game> games = new HashMap<>();
    private int nextId = 1;

    public boolean authorize(String token) throws DataAccessException{
        Authtoken auth = this.getAuth(token);
        if (auth == null){
            return false;
        }
        return true;
    }

    //    User methods
    public void createUser(User user) throws DataAccessException{
        users.put(user.username(), user);
    }

    public User getUser(String user) throws DataAccessException{
        return users.get(user);
    }

    //    Auth methods
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

    //    Game methods
    public CreateResult addGame(String gameName) throws DataAccessException {
        games.put(nextId, new Game(nextId, null, null, gameName, new ChessGame()));
        nextId++;
        return new CreateResult((nextId - 1));
    }

    public Game getGame(int gameID) throws DataAccessException{
        return games.get(gameID);
    }

    public void deleteGame(Integer gameID) throws DataAccessException{
        games.remove(gameID);
    }

    public ListResult listGames() throws DataAccessException{
        return new ListResult(new ArrayList<>(games.values()));
    }

    public void joinGame(String username, JoinRequest request) throws DataAccessException {
        Game game = getGame(request.gameID());
        ChessGame.TeamColor color = request.playerColor();
        Game newGame;

        if (color == ChessGame.TeamColor.WHITE){
            newGame = new Game(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        }
        else {
            newGame = new Game(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        }
        deleteGame(game.gameID());
        games.put(newGame.gameID(), newGame);
    }

    //    clear methods
    public void deleteAuths() throws DataAccessException {
        authdata.clear();
    }

    public void deleteUsers() throws DataAccessException {
        users.clear();
    }

    public void deleteGames() throws DataAccessException {
        games.clear();
        this.nextId = 1;
    }
}
