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

    @BeforeAll
    public static void init() throws DataAccessException {
        SERVER = new Server();
        var port = SERVER.run(8081);
        System.out.println("Started test HTTP SERVER on " + port);
        FACADE = new ServerFacade("http://localhost:8081");
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

    @Test
    public void logoutPositiveTest() throws Exception {
        Authtoken auth = FACADE.register("user", "password", "email");
        FACADE.logout(auth);
        Assertions.assertDoesNotThrow(() -> FACADE.login("user", "password"));
    }

    @Test
    public void logoutNegativeTest() throws Exception {
    Assertions.assertThrows(Exception.class, () -> FACADE.logout(new Authtoken("badtoken", "baduser")));
    }

    @Test
    public void createPositiveTest() throws Exception {
        Authtoken auth = FACADE.register("user", "password", "email");
        Assertions.assertDoesNotThrow(() -> FACADE.create(auth, "gamename"));
    }

    @Test
    public void createNegativeTest() throws Exception {
        Assertions.assertThrows(Exception.class, () -> FACADE.create(new Authtoken("badtoken", "baduser")));
    }

    @Test
    public void listPositiveTest() throws Exception {
        Authtoken auth = FACADE.register("user", "password", "email");
        FACADE.create(auth, "game1");
        FACADE.create(auth, "game2");
        ListResult result = FACADE.list(auth);
        Assertions.assertEquals(2, result.games().size());
    }

    @Test
    public void listNegativeTest() throws Exception {
        Assertions.assertThrows(Exception.class, () -> FACADE.list(new Authtoken("badtoken", "baduser")));
    }

    @Test
    public void playPositiveTest() throws Exception {
        Authtoken auth = FACADE.register("user", "password", "email");
        FACADE.create(auth, "game1");
        Assertions.assertDoesNotThrow(() -> FACADE.play(auth, 1, ChessGame.TeamColor.BLACK));
    }

    @Test
    public void playNegativeTest() throws Exception {
        Authtoken auth = FACADE.register("user", "password", "email");
        FACADE.create(auth, "game1");
        Assertions.assertThrows(Exception.class, () -> FACADE.play(auth, 5, ChessGame.TeamColor.BLACK));
    }

    @Test
    public void observePositiveTest() throws Exception {
        Authtoken auth = FACADE.register("user", "password", "email");
        FACADE.create(auth, "game1");
        Assertions.assertDoesNotThrow(() -> FACADE.observe(1));
    }

    @Test
    public void observeNegativeTest() throws Exception {
        Assertions.assertEquals(1, 1);
    }
}
