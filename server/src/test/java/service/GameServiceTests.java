package service;

import requests.*;
import results.*;
import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    AuthDao aDao;
    GameDao gDao;
    GameService g;
    String token;
    @BeforeEach
    void setUp() throws DataAccessException { //AuthDao and GameDao can be changed for testing SQL
        aDao = new MemoryAuthDao();
        gDao = new MemoryGameDao();
        g = new GameService();
        //Creating a user to make the game
        UserDao uDao = new MemoryUserDao();
        UserService u = new UserService();
        RegisterResult temp = u.register(new RegisterRequest("Catsi","C@t","Cassi@mail.com"),uDao,aDao);
        token = temp.authToken();
    }

    @Nested
    class CreateTests{
        @Test
        void testCreatePass() throws DataAccessException {
            CreateRequest r = new CreateRequest("first",token);
            CreateResult result = g.create(r,aDao,gDao);

            assertEquals(1,gDao.getAllGames().size());
            assertNull(result.message());
            assertEquals("first",gDao.getGame(result.gameID()).gameName());
        }
        @Test
        void testCreateFailGameNameNull() throws DataAccessException {
            CreateRequest r = new CreateRequest(null,token);
            assertThrows(InputException.class,() -> g.create(r,aDao,gDao));
        }
        @Test
        void testCreateFailAuthDaoNull() throws DataAccessException {
            CreateRequest r = new CreateRequest("first",token);
            assertThrows(DaoException.class,() -> g.create(r,null,gDao));
        }
        @Test
        void testCreateFailGameDaoNull() throws DataAccessException {
            CreateRequest r = new CreateRequest("first",token);
            assertThrows(DaoException.class,() -> g.create(r,aDao,null));
        }
        @Test
        void testCreateFailBadAuthToken() throws DataAccessException {
            CreateRequest r = new CreateRequest("first","12345");
            assertThrows(AuthorizationException.class,() -> g.create(r,aDao,gDao));
        }
        @Test
        void testCreateFailSameGameName() throws DataAccessException {
            CreateRequest r = new CreateRequest("first",token);
            g.create(r,aDao,gDao);
            assertThrows(DuplicateException.class,() -> g.create(r,aDao,gDao));
        }
    }

    @Nested
    class JoinTests{
        int gameID;
        @BeforeEach
        void joinSetUp() throws DataAccessException {
            //Creating a game to join
            CreateRequest cr = new CreateRequest("first",token);
            CreateResult res = g.create(cr,aDao,gDao);
            gameID = res.gameID();
        }

        @Test
        void testJoinPass() throws DataAccessException {
            JoinRequest r = new JoinRequest("WHITE",gameID,token);
            JoinResult result = g.join(r,aDao,gDao);

            assertNull(result.message());
            assertEquals(1,gDao.getAllGames().size());
            assertEquals(new GameData("Catsi",null,"first",gameID,gDao.getGame("first").game()),gDao.getGame(gameID));
        }

        @Test
        void testJoinFailInvalidGameID() throws DataAccessException {
            JoinRequest r = new JoinRequest("WHITE",12345,token);
            assertThrows(InputException.class,() -> g.join(r,aDao,gDao));
        }

        @Test
        void testJoinFailInvalidColor() throws DataAccessException {
            JoinRequest r = new JoinRequest("GREEN",gameID,token);
            assertThrows(InputException.class,() -> g.join(r,aDao,gDao));
        }

        @Test
        void testJoinFailAuthDaoNull() throws DataAccessException {
            JoinRequest r = new JoinRequest("WHITE",gameID,token);
            assertThrows(DaoException.class,() -> g.join(r,null,gDao));
        }

        @Test
        void testJoinFailGameDaoNull() throws DataAccessException {
            JoinRequest r = new JoinRequest("WHITE",gameID,token);
            assertThrows(DaoException.class,() -> g.join(r,aDao,null));
        }

        @Test
        void testJoinFailBadAuthToken() throws DataAccessException {
            JoinRequest r = new JoinRequest("WHITE",gameID,"12345");
            assertThrows(AuthorizationException.class,() -> g.join(r,aDao,gDao));
        }

        @Test
        void testJoinFailColorTaken() throws DataAccessException {
            JoinRequest r = new JoinRequest("WHITE",gameID,token);
            g.join(r,aDao,gDao);
            assertThrows(DuplicateException.class,() -> g.join(r,aDao,gDao));
        }
    }

    @Nested
    class ListTests{
        @Test
        void testListPassZeroGames() throws DataAccessException {
            ListRequest r = new ListRequest(token);
            ListResult result = g.list(r,aDao,gDao);

            assertNull(result.message());
            assertEquals(0,result.games().size());
        }

        @Test
        void testListPassOneGame() throws DataAccessException {
            CreateRequest cr = new CreateRequest("first",token);
            g.create(cr,aDao,gDao);

            ListRequest r = new ListRequest(token);
            ListResult result = g.list(r,aDao,gDao);

            assertNull(result.message());
            assertEquals(1,result.games().size());
            assertNotNull(gDao.getGame("first"));
        }

        @Test
        void testListPassTwoGames() throws DataAccessException {
            CreateRequest cr = new CreateRequest("first",token);
            g.create(cr,aDao,gDao);
            CreateRequest cr2 = new CreateRequest("second",token);
            g.create(cr2,aDao,gDao);

            ListRequest r = new ListRequest(token);
            ListResult result = g.list(r,aDao,gDao);

            assertNull(result.message());
            assertEquals(2,result.games().size());
            assertNotNull(gDao.getGame("first"));
            assertNotNull(gDao.getGame("second"));
        }

        @Test
        void testListFailAuthDaoNull(){
            ListRequest r = new ListRequest(token);
            assertThrows(DaoException.class,() -> g.list(r,null,gDao));
        }

        @Test
        void testListFailGameDaoNull(){
            ListRequest r = new ListRequest(token);
            assertThrows(DaoException.class,() -> g.list(r,aDao,null));
        }

        @Test
        void testListFailBadAuthToken(){
            ListRequest r = new ListRequest("12345");
            assertThrows(AuthorizationException.class,() -> g.list(r,aDao,gDao));
        }
    }

}
