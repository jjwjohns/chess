package client;

import UI.ServerFacade;
import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server SERVER;
    private static ServerFacade FACADE;
    private static final User USER = new User("user", "password", "email");
    private static final User BADUSER = new User(null, null, null);
    private static final LoginRequest LOGIN_REQUEST = new LoginRequest("user", "password");
    private static final CreateRequest CREATE_REQUEST = new CreateRequest("test");
    private static final JoinRequest JOIN_REQUEST = new JoinRequest(ChessGame.TeamColor.WHITE, 1);

    @BeforeAll
    public static void init() throws DataAccessException {
        SERVER = new Server();
        var port = SERVER.run(8080);
        System.out.println("Started test HTTP SERVER on " + port);
        FACADE = new ServerFacade("http://localhost:8080");
    }

    @BeforeEach
    void clearData() throws Exception {
        SERVER.dataAccess.clear();
    }

    @AfterAll
    static void stopServer() {
        SERVER.stop();
    }


    @Test
    public void registerPositiveTest() throws Exception {
        Authtoken auth = FACADE.register("user", "password", "email");
        Assertions.assertEquals(auth.username(), "user");
    }

    @Test
    public void registerNegativeTest() throws Exception {
        FACADE.register("user", "password", "email");
        Assertions.assertThrows(Exception.class ,() -> FACADE.register("user", "password", "email"));
    }

    @Test
    public void loginPositiveTest() throws Exception {
        FACADE.register("user", "password", "email");
        Authtoken auth = FACADE.login("user", "password");
        Assertions.assertEquals(auth.username(), "user");
    }

    @Test
    public void loginNegativeTest() throws Exception {
        Assertions.assertThrows(Exception.class, () -> FACADE.login("user", "password"));
    }

}
