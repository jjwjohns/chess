package service;

import dataAccess.DataMemory;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;

import javax.xml.crypto.Data;


public class ServiceTests {
    private static Server server = new Server();
    private static DataMemory access = server.dataAccess;
    private static ChessService service = server.service;
    private static User TestUser = new User("user", "password", "email");

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
//        #clearing an empty database
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
//        #clearing an empty database
        service.clear();
        Assertions.assertDoesNotThrow(() -> service.clear());
    }
}


