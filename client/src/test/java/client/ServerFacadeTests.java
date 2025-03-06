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
    String token;

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
        @BeforeEach
        void setUp() throws ResponseException {
            RegisterResult res = facade.register(new RegisterRequest("Catsi", "cat", "cat@mail.com"));
            token = res.authToken();
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
        void testLogoutPass() throws ResponseException {
            LogoutResult r = facade.logout(new LogoutRequest(token)); //For some reason headers is null when passing to logout in server
            assertNull(r.message());
        }

        @Test
        void testLogoutFailNullToken() throws ResponseException {
            assertThrows(ResponseException.class, () -> facade.logout(new LogoutRequest(null)));
        }

        @Test
        void testLogoutFailInvalidToken() throws ResponseException {
            assertThrows(ResponseException.class, () -> facade.logout(new LogoutRequest("12345")));
        }

    }

    @Nested
    class CreateTests{
        @BeforeEach
        void setUp() throws ResponseException {
            RegisterResult res = facade.register(new RegisterRequest("Catsi", "cat", "cat@mail.com"));
            token = res.authToken();
        }

        @Test
        void testCreatePass() throws ResponseException {
            CreateResult r = facade.create(new CreateRequest("theName",token));
            assertNull(r.message());
            assertNotNull(r.gameID());
        }

        @Test
        void testCreateFailNullGameName() throws ResponseException {
            assertThrows(ResponseException.class, () -> facade.create(new CreateRequest(null,token)));
        }

        @Test
        void testCreateFailNullAuthToken() throws ResponseException {
            assertThrows(ResponseException.class, () -> facade.create(new CreateRequest("name",null)));
        }

        @Test
        void testCreateFailBadAuthToken() throws ResponseException {
            assertThrows(ResponseException.class, () -> facade.create(new CreateRequest("name","12345")));
        }

        @Test
        void testCreateFailSameGameName() throws ResponseException {
            facade.create(new CreateRequest("theName",token));
            assertThrows(ResponseException.class, () -> facade.create(new CreateRequest("theName",token)));
        }

    }

    @Nested
    class ListJoinClearTests{
        int id;

        @BeforeEach
        void setUp() throws ResponseException {
            RegisterResult res = facade.register(new RegisterRequest("Catsi", "cat", "cat@mail.com"));
            token = res.authToken();
            CreateResult cres = facade.create(new CreateRequest("theName",token));
            id = cres.gameID();
        }

        @Test
        void testListPassOneGame() throws ResponseException {
            ListResult r = facade.list(new ListRequest(token));
            assertNull(r.message());
            assertEquals(1,r.games().size());
        }

        @Test
        void testListPassTwoGames() throws ResponseException {
            facade.create(new CreateRequest("secondName",token));
            ListResult r = facade.list(new ListRequest(token));
            assertNull(r.message());
            assertEquals(2,r.games().size());
        }

        @Test
        void testListFail() throws ResponseException {
            assertThrows(ResponseException.class, () -> facade.list(new ListRequest(null)));
        }

        @Test
        void testJoinPassWhite() throws ResponseException {
            JoinResult r = facade.join(new JoinRequest("WHITE",id,token));
            assertNull(r.message());
        }

        @Test
        void testJoinPassBlack() throws ResponseException {
            JoinResult r = facade.join(new JoinRequest("BLACK",id,token));
            assertNull(r.message());
        }

        @Test
        void testJoinFailNullColor() throws ResponseException {
            assertThrows(ResponseException.class, () -> facade.join(new JoinRequest(null,id,token)));
        }

        @Test
        void testJoinFailNullAuthToken() throws ResponseException {
            assertThrows(ResponseException.class, () -> facade.join(new JoinRequest("WHITE",id,null)));
        }

        @Test
        void testJoinFailBadColor() throws ResponseException {
            assertThrows(ResponseException.class, () -> facade.join(new JoinRequest("BLUE",id,token)));
        }

        @Test
        void testJoinFailLowerCaseColor() throws ResponseException {
            assertThrows(ResponseException.class, () -> facade.join(new JoinRequest("white",id,token)));
        }

        @Test
        void testJoinFailCapitalizedColor() throws ResponseException {
            assertThrows(ResponseException.class, () -> facade.join(new JoinRequest("White",id,token)));
        }

        @Test
        void testJoinFailBadGameID() throws ResponseException {
            assertThrows(ResponseException.class, () -> facade.join(new JoinRequest("WHITE",54321,token)));
        }

        @Test
        void testJoinFailBadAuthToken() throws ResponseException {
            assertThrows(ResponseException.class, () -> facade.join(new JoinRequest("WHITE",id,"12345")));
        }

        @Test
        void testClearPass() throws ResponseException {
        }

    }
}
