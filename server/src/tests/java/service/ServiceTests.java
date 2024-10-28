package service;

import dataAccess.DataMemory;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;

import javax.xml.crypto.Data;


public class ServiceTests {
    private static final Server server = new Server();
    private static final DataMemory access = server.dataAccess;
    private static final ChessService service = server.service;
    private static final User TestUser = new User("user", "password", "email");

    @AfterAll
    static void clearData() throws Exception {
        service.clear();
    }

    @Test
    public void testClear_Positive() throws Exception {

        access.createAuth("username");
        access.createUser(TestUser);

        service.clear();

        Assertions.assertNull(access.getUser("user"));
    }

    @Test
    public void testClear_Negative() throws Exception {
//        clearing an empty database
        service.clear();
        Assertions.assertDoesNotThrow(() -> service.clear());
    }

    @Test
    public void testRegister_Positive() throws Exception {

        service.register(TestUser);
        Assertions.assertSame(access.getUser("user"), TestUser);
    }

    @Test
    public void testRegister_Negative() throws Exception {
        service.register(TestUser);
        Assertions.assertNull(service.register(TestUser));
    }
}


