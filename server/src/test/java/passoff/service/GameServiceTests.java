package passoff.service;

import Requests.*;
import Results.*;
import dataaccess.*;
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
        @BeforeEach
        void joinSetUp(){

        }

        void testJoinPass(){

        }

        void testJoinFail(){

        }
    }

    @Nested
    class ListTests{
        @BeforeEach
        void listSetUp(){

        }

        void testListPass(){

        }

        void testListFail(){

        }
    }

}
