package client;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Nested;
import requests.*;
import results.*;
import server.Server;
import ui.ResponseException;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    @AfterEach
    void clearServer(){
        server.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
    //Note: Dao's are not cleared when server stops
    @Nested
    class RegisterTests {

        @Test
        void testRegisterPass() throws ResponseException {
            RegisterResult r = facade.register(new RegisterRequest("Catsi", "cat", "cat@mail.com"));
            assertNotNull(r.authToken());
            assertEquals("Catsi", r.username());
            assertNull(r.message());
        }

        @Test
        void testRegisterFailEmailNull() throws ResponseException {
            try {
                facade.register(new RegisterRequest("Catsi", "cat", null));
                fail();
            } catch (ResponseException e) {
                assertEquals(500, e.getStatusCode());
            }
        }

        @Test
        void testRegisterFailUsernameNull() throws ResponseException {
            try {
                facade.register(new RegisterRequest(null, "cat", "cat@mail.com"));
                fail();
            } catch (ResponseException e) {
                assertEquals(500, e.getStatusCode());
            }
        }

        @Test
        void testRegisterFailPasswordNull() throws ResponseException {
            try {
                facade.register(new RegisterRequest("Catsi", null, "cat@mail.com"));
                fail();
            } catch (ResponseException e) {
                assertEquals(500, e.getStatusCode());
            }
        }

        @Test
        void testRegisterFailUsernameTaken() throws ResponseException {
            facade.register(new RegisterRequest("Catsi", "cat", "cat@mail.com"));
            assertThrows(ResponseException.class, () -> facade.register(new RegisterRequest("Catsi", "dog", "dog@mail.com")));
        }
    }

    @Nested
    class LoginLogoutTests{
        RegisterResult res;
        String token;
        @BeforeEach
        void setUp() throws ResponseException {
            res = facade.register(new RegisterRequest("Catsi", "cat", "cat@mail.com"));
        }

        @Test
        void testLoginPass() throws ResponseException {
            LoginResult r = facade.login(new LoginRequest("Catsi", "cat"));
            assertEquals("Catsi",r.username());
            assertNull(r.message());
            assertNotNull(r.authToken());
        }

        @Test
        void testLoginFailUsernameNull() throws ResponseException {
            assertThrows(ResponseException.class, () -> facade.login(new LoginRequest(null,"cat")));
        }

        @Test
        void testLoginFailPasswordNull() throws ResponseException {
            assertThrows(ResponseException.class, () -> facade.login(new LoginRequest("Catsi","null")));
        }

        @Test
        void testLoginFailBadPassword() throws ResponseException {
            assertThrows(ResponseException.class, () -> facade.login(new LoginRequest("Catsi","dog")));
        }

        @Test
        void testLogoutPass() throws ResponseException {}

        @Test
        void testLogoutFail() throws ResponseException {}

    }

    @Nested
    class CreateTests{
        @BeforeEach
        void setUp() throws ResponseException {

        }

        @Test
        void testCreatePass() throws ResponseException {}

        @Test
        void testCreateFail() throws ResponseException {}

    }

    @Nested
    class ListJoinTests{
        @BeforeEach
        void setUp() throws ResponseException {

        }

        @Test
        void testListPass() throws ResponseException {}

        @Test
        void testListFail() throws ResponseException {}

        @Test
        void testJoinPass() throws ResponseException {}

        @Test
        void testJoinFail() throws ResponseException {}

        @Test
        void testClearPass() throws ResponseException {}

        //@Test
        //void testClearFail(){}   <- I don't know if this is required

    }
}
