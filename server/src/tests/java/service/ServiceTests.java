package service;

import dataAccess.DataMemory;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;

public class ServiceTests {
    private static final Server server = new Server();
    private static final DataMemory access = server.dataAccess;
    private static final ChessService service = server.service;
    private static final User testuser = new User("user", "password", "email");

    @AfterAll
    static void clearData() throws Exception {
        service.clear();
    }

    @Test
    public void testClear_Positive() throws Exception {
        access.createAuth("username");
        access.createUser(testuser);

        service.clear();

        Assertions.assertNull(access.getUser("user"));
    }

    @Test
    public void testClear_Empty() throws Exception {
//        clearing an empty database
        service.clear();
        Assertions.assertDoesNotThrow(() -> service.clear());
    }

    @Test
    public void testRegister_Positive() throws Exception {
        service.register(testuser);
        Assertions.assertSame(access.getUser("user"), testuser);
    }

    @Test
    public void testRegister_Negative() throws Exception {
        service.register(testuser);
        Assertions.assertNull(service.register(testuser));
    }

    @Test
    public void testLogin_Positive() throws Exception {
        LoginRequest login = new LoginRequest("user", "password");
        service.register(testuser);
        Authtoken auth = service.login(login);
        Assertions.assertSame(login.username(), auth.username());
    }

    @Test
    public void testLogin_Negative() throws Exception {
        LoginRequest badlogin = new LoginRequest("baduser", "password");
        service.register(testuser);
        Assertions.assertNull(service.login(badlogin));
    }

    @Test
    public void testLogout_Positive() throws Exception {
        LoginRequest login = new LoginRequest("user", "password");
        service.register(testuser);
        Authtoken auth = service.login(login);
        service.logout(auth.authToken());
        Assertions.assertNull(access.getAuth(auth.authToken()));
    }

    @Test
    public void testLogout_Negative() throws Exception {
        LoginRequest login = new LoginRequest("user", "password");
        service.register(testuser);
        Authtoken auth = service.login(login);
        Assertions.assertNull(access.getAuth("badtoken"));
        Assertions.assertNotNull(access.getAuth(auth.authToken()));
    }
}


