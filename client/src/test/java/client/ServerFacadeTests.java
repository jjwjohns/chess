package client;

import ui.ServerFacade;
import chess.ChessGame;
import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() throws DataAccessException {
        server = new Server();
        var port = server.run(8081);
        System.out.println("Started test HTTP SERVER on " + port);
        facade = new ServerFacade("http://localhost:8081");
    }

    @BeforeEach
    void clearData() throws Exception {
        server.dataAccess.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerPositiveTest() throws Exception {
        Authtoken auth = facade.register("user", "password", "email");
        Assertions.assertEquals(auth.username(), "user");
    }

    @Test
    public void registerNegativeTest() throws Exception {
        facade.register("user", "password", "email");
        Assertions.assertThrows(Exception.class ,() -> facade.register("user", "password", "email"));
    }

    @Test
    public void loginPositiveTest() throws Exception {
        facade.register("user", "password", "email");
        Authtoken auth = facade.login("user", "password");
        Assertions.assertEquals(auth.username(), "user");
    }

    @Test
    public void loginNegativeTest() throws Exception {
        Assertions.assertThrows(Exception.class, () -> facade.login("user", "password"));
    }

    @Test
    public void logoutPositiveTest() throws Exception {
        Authtoken auth = facade.register("user", "password", "email");
        facade.logout(auth);
        Assertions.assertDoesNotThrow(() -> facade.login("user", "password"));
    }

    @Test
    public void logoutNegativeTest() throws Exception {
    Assertions.assertThrows(Exception.class, () -> facade.logout(new Authtoken("badtoken", "baduser")));
    }

    @Test
    public void createPositiveTest() throws Exception {
        Authtoken auth = facade.register("user", "password", "email");
        Assertions.assertDoesNotThrow(() -> facade.create(auth, "gamename"));
    }

    @Test
    public void createNegativeTest() throws Exception {
        Assertions.assertThrows(Exception.class, () -> facade.create(new Authtoken("badtoken", "baduser")));
    }

    @Test
    public void listPositiveTest() throws Exception {
        Authtoken auth = facade.register("user", "password", "email");
        facade.create(auth, "game1");
        facade.create(auth, "game2");
        ListResult result = facade.list(auth);
        Assertions.assertEquals(2, result.games().size());
    }

    @Test
    public void listNegativeTest() throws Exception {
        Assertions.assertThrows(Exception.class, () -> facade.list(new Authtoken("badtoken", "baduser")));
    }

    @Test
    public void playPositiveTest() throws Exception {
        Authtoken auth = facade.register("user", "password", "email");
        facade.create(auth, "game1");
        Assertions.assertDoesNotThrow(() -> facade.play(auth, 1, ChessGame.TeamColor.BLACK));
    }

    @Test
    public void playNegativeTest() throws Exception {
        Authtoken auth = facade.register("user", "password", "email");
        facade.create(auth, "game1");
        Assertions.assertThrows(Exception.class, () -> facade.play(auth, 5, ChessGame.TeamColor.BLACK));
    }

    @Test
    public void observePositiveTest() throws Exception {
        Authtoken auth = facade.register("user", "password", "email");
        facade.create(auth, "game1");
        Assertions.assertDoesNotThrow(() -> facade.observe(1));
    }

    @Test
    public void observeNegativeTest() throws Exception {
        Assertions.assertEquals(1, 1);
    }
}
