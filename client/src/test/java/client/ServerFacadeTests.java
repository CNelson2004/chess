package client;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;


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
    void testRegisterPass(){}

    @Test
    void testRegisterFail(){}

    @Test
    void testLoginPass(){}

    @Test
    void testLoginFail(){}

    @Test
    void testLogoutPass(){}

    @Test
    void testLogoutFail(){}

    @Test
    void testCreatePass(){}

    @Test
    void testCreateFail(){}

    @Test
    void testListPass(){}

    @Test
    void testListFail(){}

    @Test
    void testJoinPass(){}

    @Test
    void testJoinFail(){}

    @Test
    void testClearPass(){}

    //@Test
    //void testClearFail(){}   <- I don't know if this is required

    //Example:
//    @Test
//    void register() throws Exception {
//        var authData = facade.register("player1", "password", "p1@email.com");
//        assertTrue(authData.authToken().length() > 10);
//    }

}
