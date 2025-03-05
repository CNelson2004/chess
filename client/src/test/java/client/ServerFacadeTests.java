package client;

import org.junit.jupiter.api.*;
import requests.*;
import results.*;
import server.Server;
import ui.ResponseException;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    //Remember to start server in Main.main when doing tests
    //clear database in between each test

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void testRegisterPass() throws ResponseException {
        RegisterResult r = facade.register(new RegisterRequest("Catsi","cat","cat@mail.com"));
        assertNotNull(r.authToken());
        assertEquals("Catsi",r.username());
        assertNull(r.message());
    }

    @Test
    void testRegisterFailEmailNull() throws ResponseException {
        assertThrows(ResponseException.class, () -> facade.register(new RegisterRequest("Catsi","cat",null)));
    }

    @Test
    void testRegisterFailUsernameNull() throws ResponseException {
        assertThrows(ResponseException.class, () -> facade.register(new RegisterRequest(null,"cat","cat@mail.com")));
    }

    @Test
    void testRegisterFailPasswordNull() throws ResponseException {
        assertThrows(ResponseException.class, () -> facade.register(new RegisterRequest("Catsi",null,"cat@mail.com")));
    }

    @Test
    void testRegisterFailUsernameTaken() throws ResponseException {
        facade.register(new RegisterRequest("Catsi","cat","cat@mail.com"));
        assertThrows(ResponseException.class, () -> facade.register(new RegisterRequest("Catsi","dog","dog@mail.com")));
    }

    @Test
    void testLoginPass() throws ResponseException {}

    @Test
    void testLoginFail() throws ResponseException {}

    @Test
    void testLogoutPass() throws ResponseException {}

    @Test
    void testLogoutFail() throws ResponseException {}

    @Test
    void testCreatePass() throws ResponseException {}

    @Test
    void testCreateFail() throws ResponseException {}

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
