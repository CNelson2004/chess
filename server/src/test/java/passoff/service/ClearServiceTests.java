package passoff.service;

import dataaccess.*;
import model.*;
import service.ClearService;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {
    //Simple Asserts: assertEquals(), assertTrue(), assertNotNull(), and assertThrows()
    //            assertEquals(200, 100 + 100);
//            assertTrue(100 == 2 * 50);
//            assertNotNull(new Object(), "Response did not return authentication String");
//            assertThrows(InvalidArgumentException.class, () -> {
//                throw new InvalidArgumentException();
//            });
    @Test
        public void testClear() throws DataAccessException {
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
            try {
                auth.createAuth(one);
                auth.createAuth(two);
            } catch (DataAccessException e) {
                throw new RuntimeException(e);
            }
        //Clearing the stuff
            ClearService clearService = new ClearService();
            clearService.clear(user,auth,game);
            assertTrue(user.getAllUsers().isEmpty());
            assertTrue(game.getAllGames().isEmpty());
            assertTrue(auth.getAllAuths().isEmpty());
        }

}
