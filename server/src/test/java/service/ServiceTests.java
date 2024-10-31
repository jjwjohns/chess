package service;

import chess.ChessGame;
import dataaccess.MySqlDataAccess;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;

public class ServiceTests {
    private static final Server SERVER = new Server();
    private static final MySqlDataAccess ACCESS = SERVER.dataAccess;
    private static final ChessService SERVICE = SERVER.service;
    private static final User USER = new User("user", "password", "email");
    private static final LoginRequest LOGIN_REQUEST = new LoginRequest("user", "password");
    private static final CreateRequest CREATE_REQUEST = new CreateRequest("test");
    private static final JoinRequest JOIN_REQUEST = new JoinRequest(ChessGame.TeamColor.WHITE, 1);

    @AfterEach
    void clearData() throws Exception {
        SERVICE.clear();
    }

    @Test
    public void testClearPositive() throws Exception {
        ACCESS.createAuth("username");
        ACCESS.createUser(USER);
        SERVICE.clear();

        Assertions.assertNull(ACCESS.getUser("user"));
    }

    @Test
    public void testClearEmpty() throws Exception {
//        clearing an empty database
        SERVICE.clear();

        Assertions.assertDoesNotThrow(() -> SERVICE.clear());
    }

    @Test
    public void testRegisterPositive() throws Exception {
        SERVICE.register(USER);

        Assertions.assertSame(ACCESS.getUser("user"), USER);
    }

    @Test
    public void testRegisterNegative() throws Exception {
        SERVICE.register(USER);

        Assertions.assertNull(SERVICE.register(USER));
    }

    @Test
    public void testLoginPositive() throws Exception {
        SERVICE.register(USER);
        Authtoken auth = SERVICE.login(LOGIN_REQUEST);

        Assertions.assertSame(LOGIN_REQUEST.username(), auth.username());
    }

    @Test
    public void testLoginNegative() throws Exception {
        LoginRequest badlogin = new LoginRequest("baduser", "password");
        SERVICE.register(USER);

        Assertions.assertNull(SERVICE.login(badlogin));
    }

    @Test
    public void testLogoutPositive() throws Exception {
        SERVICE.register(USER);
        Authtoken auth = SERVICE.login(LOGIN_REQUEST);
        SERVICE.logout(auth.authToken());

        Assertions.assertNull(ACCESS.getAuth(auth.authToken()));
    }

    @Test
    public void testLogoutNegative() throws Exception {
        SERVICE.register(USER);
        Authtoken auth = SERVICE.login(LOGIN_REQUEST);

        Assertions.assertNull(ACCESS.getAuth("badtoken"));
        Assertions.assertNotNull(ACCESS.getAuth(auth.authToken()));
    }

    @Test
    public void testCreatePositive() throws Exception {
        CreateResult result = SERVICE.createGame(CREATE_REQUEST);
        Assertions.assertNotNull(result);
        Game game = ACCESS.getGame(result.gameID());

        Assertions.assertSame(game.gameName(), "test");
    }

    @Test
    public void testCreateNegative() throws Exception {
        CreateResult result1 = SERVICE.createGame(CREATE_REQUEST);
        CreateResult result2 = SERVICE.createGame(CREATE_REQUEST);
        Game game1 = ACCESS.getGame(result1.gameID());
        Game game2 = ACCESS.getGame(result2.gameID());

        Assertions.assertNotSame(game1.gameID(), game2.gameID());
    }

    @Test
    public void testListPositive() throws Exception {
        SERVICE.clear();
        SERVICE.createGame(CREATE_REQUEST);
        SERVICE.createGame(CREATE_REQUEST);
        ListResult listResult = SERVICE.listGames();

        Assertions.assertNotNull(listResult);
        Assertions.assertEquals(2, listResult.games().size());
    }

    @Test
    public void testListNegative() throws Exception {
        SERVICE.clear();
        ListResult listResult = SERVICE.listGames();

        Assertions.assertTrue(listResult.games().isEmpty());
    }

    @Test
    public void testJoinPositive() throws Exception {
        SERVICE.register(USER);
        Authtoken auth = SERVICE.login(LOGIN_REQUEST);
        CreateResult createResult = SERVICE.createGame(CREATE_REQUEST);
        SERVICE.joinGame(JOIN_REQUEST, auth.authToken());
        Game game = ACCESS.getGame(createResult.gameID());

        Assertions.assertSame("user", game.whiteUsername());
    }

    @Test
    public void testJoinNegative() throws Exception {
        SERVICE.register(USER);
        SERVICE.login(LOGIN_REQUEST);
        SERVICE.createGame(CREATE_REQUEST);

        Assertions.assertThrows(NullPointerException.class, () -> {
            SERVICE.joinGame(JOIN_REQUEST, "badtoken");
        });

    }
}


