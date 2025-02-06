package passoff.service;

import Requests.*;
import Results.*;
import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    MemoryAuthDao aDao;
    MemoryGameDao gDao;
    GameService g;
    String token;
    @BeforeEach
    void setUp() throws DataAccessException {
        aDao = new MemoryAuthDao();
        gDao = new MemoryGameDao();
        g = new GameService();
        //Creating a user to make the game
        MemoryUserDao uDao = new MemoryUserDao();
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
            assertEquals("Success",result.message());
            assertEquals("first",gDao.getGame(result.gameID()).gameName());
        }
        @Test
        void testCreateFailGameNameNull() throws DataAccessException {
            CreateRequest r = new CreateRequest(null,token);
            CreateResult result = g.create(r,aDao,gDao);

            assertEquals("gameName is null",result.message());
        }
        @Test
        void testCreateFailAuthDaoNull() throws DataAccessException {
            CreateRequest r = new CreateRequest("first",token);
            CreateResult result = g.create(r,null,gDao);

            assertEquals("AuthDao is null",result.message());
        }
        @Test
        void testCreateFailGameDaoNull() throws DataAccessException {
            CreateRequest r = new CreateRequest("first",token);
            CreateResult result = g.create(r,aDao,null);

            assertEquals("GameDao is null",result.message());
        }
        @Test
        void testCreateFailBadAuthToken() throws DataAccessException {
            CreateRequest r = new CreateRequest("first","12345");
            CreateResult result = g.create(r,aDao,gDao);

            assertEquals("Invalid authToken",result.message());
        }
        @Test
        void testCreateFailSameGameName() throws DataAccessException {
            CreateRequest r = new CreateRequest("first",token);
            g.create(r,aDao,gDao);
            CreateResult result = g.create(r,aDao,gDao);

            assertEquals("Game Name is already taken",result.message());
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

            assertEquals("Success",result.message());
            assertEquals(1,gDao.getAllGames().size());
            assertEquals(new GameData("Catsi",null,"first",gameID,gDao.getGame("first").game()),gDao.getGame(gameID));
        }

        @Test
        void testJoinFailInvalidGameID() throws DataAccessException {
            JoinRequest r = new JoinRequest("WHITE",12345,token);
            JoinResult result = g.join(r,aDao,gDao);

            assertEquals("Invalid Input",result.message());
        }

        @Test
        void testJoinFailInvalidColor() throws DataAccessException {
            JoinRequest r = new JoinRequest("GREEN",gameID,token);
            JoinResult result = g.join(r,aDao,gDao);

            assertEquals("Invalid Input",result.message());
        }

        @Test
        void testJoinFailAuthDaoNull() throws DataAccessException {
            JoinRequest r = new JoinRequest("WHITE",gameID,token);
            JoinResult result = g.join(r,null,gDao);

            assertEquals("AuthDao is null",result.message());
        }

        @Test
        void testJoinFailGameDaoNull() throws DataAccessException {
            JoinRequest r = new JoinRequest("WHITE",gameID,token);
            JoinResult result = g.join(r,aDao,null);

            assertEquals("GameDao is null",result.message());
        }

        @Test
        void testJoinFailBadAuthToken() throws DataAccessException {
            JoinRequest r = new JoinRequest("WHITE",gameID,"12345");
            JoinResult result = g.join(r,aDao,gDao);

            assertEquals("Invalid authToken",result.message());
        }

        @Test
        void testJoinFailColorTaken() throws DataAccessException {
            JoinRequest r = new JoinRequest("WHITE",gameID,token);
            g.join(r,aDao,gDao);
            JoinResult result = g.join(r,aDao,gDao);

            assertEquals("That color is already taken",result.message());
        }
    }

    @Nested
    class ListTests{
        @Test
        void testListPassZeroGames(){
            ListRequest r = new ListRequest(token);
            ListResult result = g.list(r,aDao,gDao);

            assertEquals("Success",result.message());
            assertEquals(0,result.allGames().size());
        }

        @Test
        void testListPassOneGame() throws DataAccessException {
            CreateRequest cr = new CreateRequest("first",token);
            g.create(cr,aDao,gDao);

            ListRequest r = new ListRequest(token);
            ListResult result = g.list(r,aDao,gDao);

            assertEquals("Success",result.message());
            assertEquals(1,result.allGames().size());
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

            assertEquals("Success",result.message());
            assertEquals(2,result.allGames().size());
            assertNotNull(gDao.getGame("first"));
            assertNotNull(gDao.getGame("second"));
        }

        @Test
        void testListFailAuthDaoNull(){
            ListRequest r = new ListRequest(token);
            ListResult result = g.list(r,null,gDao);

            assertEquals("AuthDao is null",result.message());
        }

        @Test
        void testListFailGameDaoNull(){
            ListRequest r = new ListRequest(token);
            ListResult result = g.list(r,aDao,null);

            assertEquals("GameDao is null",result.message());
        }

        @Test
        void testListFailBadAuthToken(){
            ListRequest r = new ListRequest("12345");
            ListResult result = g.list(r,aDao,gDao);

            assertEquals("Invalid authToken",result.message());
        }
    }

}
