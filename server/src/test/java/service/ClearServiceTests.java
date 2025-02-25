package service;

import dataaccess.*;
import model.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {
    @Test
    public void testClearPass() throws DataAccessException {
        //Populating it with stuff
        UserDao user = new MemoryUserDao();
        GameDao game = new MemoryGameDao();
        AuthDao auth = new MemoryAuthDao();
        UserData one = new UserData("one","one@gmail.com","one1");
        UserData two = new UserData("two","two@gmail.com","two2");
        user.createUser("one","one1","one@gmail.com");
        user.createUser("two","two2","two2@gmail.com");
        game.createGame("test1");
        game.createGame("test2");
        auth.createAuth(one);
        auth.createAuth(two);
        //Clearing the stuff
        ClearService clearService = new ClearService();
        clearService.clear(user,auth,game);
        assertTrue(user.getAllUsers().isEmpty());
        assertTrue(game.getAllGames().isEmpty());
        assertTrue(auth.getAllAuths().isEmpty());
        }

        @Test
    public void testClearAuthDaoError() throws DataAccessException {
            //Populating it with stuff
            UserDao user = new MemoryUserDao();
            GameDao game = new MemoryGameDao();
            AuthDao auth = new MemoryAuthDao();
            UserData one = new UserData("one","one@gmail.com","one1");
            UserData two = new UserData("two","two@gmail.com","two2");
            user.createUser("one","one1","one@gmail.com");
            user.createUser("two","two2","two2@gmail.com");
            game.createGame("test1");
            game.createGame("test2");
            auth.createAuth(one);
            auth.createAuth(two);
            //Clearing the stuff
            ClearService clearService = new ClearService();
            assertThrows(DaoException.class,() -> clearService.clear(user,null,game));
        }

        @Test
    public void testClearUserDaoError() throws DataAccessException {
            //Populating it with stuff
            UserDao user = new MemoryUserDao();
            GameDao game = new MemoryGameDao();
            AuthDao auth = new MemoryAuthDao();
            UserData one = new UserData("one", "one@gmail.com", "one1");
            UserData two = new UserData("two", "two@gmail.com", "two2");
            user.createUser("one", "one1", "one@gmail.com");
            user.createUser("two", "two2", "two2@gmail.com");
            game.createGame("test1");
            game.createGame("test2");
            auth.createAuth(one);
            auth.createAuth(two);
            //Clearing the stuff
            ClearService clearService = new ClearService();
            assertThrows(DaoException.class, () -> clearService.clear(user, auth,null));
        }
}
