package dataAccess;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;
import service.ChessService;

public class DataAccessTests {
    private static final Server SERVER = new Server();
    private static final MySqlDataAccess ACCESS = SERVER.dataAccess;
    private static final ChessService SERVICE = SERVER.service;
    private static final User USER = new User("user", "password", "email");
    private static final User BADUSER = new User(null, null, null);
    private static final LoginRequest LOGIN_REQUEST = new LoginRequest("user", "password");
    private static final CreateRequest CREATE_REQUEST = new CreateRequest("test");
    private static final JoinRequest JOIN_REQUEST = new JoinRequest(ChessGame.TeamColor.WHITE, 1);

    @BeforeEach
    void clearData() throws Exception {
        SERVICE.clear();
    }

    @Test
    public void testClearPositive() throws Exception {
        ACCESS.createUser(USER);
        ACCESS.clear();

        Assertions.assertNull(ACCESS.getUser("user"));
    }


    @Test
    public void testCreateUserPositive() throws Exception {
        ACCESS.createUser(USER);

        Assertions.assertEquals(ACCESS.getUser("user").username(), USER.username());
    }

    @Test
    public void testCreateUserNegative() throws Exception {
        Assertions.assertThrows(DataAccessException.class, () -> {
            ACCESS.createUser(BADUSER);
        });
    }

    @Test
    public void testGetUserPositive() throws Exception {
        ACCESS.createUser(USER);
        User user = ACCESS.getUser("user");

        Assertions.assertEquals(user.username(), USER.username());
    }

    @Test
    public void testGetUserNegative() throws Exception {
        Assertions.assertNull(ACCESS.getUser("USER"));
    }

    @Test
    public void testAuthorizePositive() throws Exception {
        ACCESS.createUser(USER);
        Authtoken auth = SERVICE.login(LOGIN_REQUEST);
        Assertions.assertTrue(ACCESS.authorize(auth.authToken()));
    }

    @Test
    public void testAuthorizeNegative() throws Exception {
        Assertions.assertFalse(ACCESS.authorize("hello"));
    }

    @Test
    public void testCreateAuthPositive() throws Exception {
        ACCESS.createUser(USER);
        Authtoken auth = ACCESS.createAuth(USER.username());
        Assertions.assertTrue(ACCESS.authorize(auth.authToken()));
    }

    @Test
    public void testCreateAuthNegative() throws Exception {
        ACCESS.createUser(USER);
        Assertions.assertThrows(DataAccessException.class, () -> {
            ACCESS.createAuth(BADUSER.username());});
    }

    @Test
    public void testGetAuthPositive() throws Exception {
        ACCESS.createUser(USER);
        Authtoken auth = ACCESS.createAuth(USER.username());
        Authtoken auth1 = ACCESS.getAuth(auth.authToken());
        Assertions.assertEquals(auth, auth1);
    }

    @Test
    public void testGetAuthNegative() throws Exception {
        Assertions.assertNull(ACCESS.getAuth("hi"));
    }

    @Test
    public void testDeleteAuthPositive() throws Exception {
        ACCESS.createUser(USER);
        Authtoken auth = ACCESS.createAuth(USER.username());
        ACCESS.deleteAuth(auth);
        Assertions.assertNull(ACCESS.getAuth(auth.authToken()));
    }

    @Test
    public void testDeleteAuthNegative() throws Exception {
        Assertions.assertThrows(DataAccessException.class, () -> {
            ACCESS.deleteAuth(new Authtoken(null, null));
        });
    }

    @Test
    public void testAddGamePositive() throws Exception {
        CreateResult result  = ACCESS.addGame("game");
        Game game = ACCESS.getGame(result.gameID());

        Assertions.assertEquals(game.gameName(), "game");
    }

    @Test
    public void testAddGameNegative() throws Exception {
        Assertions.assertThrows(DataAccessException.class, () -> {
            ACCESS.addGame(null);
        });
    }

    @Test
    public void testGetGamePositive() throws Exception {
        CreateResult result  = ACCESS.addGame("game");
        Game game = ACCESS.getGame(result.gameID());

        Assertions.assertEquals(game.gameName(), "game");
    }

    @Test
    public void testGetGameNegative() throws Exception {
        Assertions.assertNull(ACCESS.getGame(0));
    }

    @Test
    public void testListGamesPositive() throws Exception {
        ACCESS.addGame("game0");
        ACCESS.addGame("game1");
        ACCESS.addGame("game2");
        ListResult list = ACCESS.listGames();

        Assertions.assertNotNull(list);
        Assertions.assertEquals(3, list.games().size());
    }

    @Test
    public void testListGamesNegative() throws Exception {
        ListResult listResult = SERVICE.listGames();

        Assertions.assertTrue(listResult.games().isEmpty());;
    }

    @Test
    public void testJoinPositive() throws Exception {
        ACCESS.createUser(USER);
        CreateResult result = ACCESS.addGame("game");
        ACCESS.joinGame(USER.username(), JOIN_REQUEST);
        Game game = ACCESS.getGame(result.gameID());

        Assertions.assertEquals("user", game.whiteUsername());
    }

    @Test
    public void testJoinNegative() throws Exception {
        Assertions.assertThrows(NullPointerException.class, () -> {
            ACCESS.joinGame(USER.username(), new JoinRequest(ChessGame.TeamColor.WHITE, 0));
        });
    }
}
