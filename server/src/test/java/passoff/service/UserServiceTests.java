package passoff.service;

import Requests.*;
import Results.*;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    MemoryUserDao uDao;
    MemoryAuthDao aDao;
    UserService u;
    @BeforeEach
    void setUp(){
        uDao = new MemoryUserDao();
        aDao = new MemoryAuthDao();
        u = new UserService();
    }

    @Nested
    class RegisterTests{

        @Test
        public void testRegisterPass() throws DataAccessException {
            RegisterRequest r = new RegisterRequest("Catsi", "C@t", "Cassi@mail.com");
            RegisterResult result = u.register(r, uDao, aDao);

            UserData user = new UserData("Catsi", "Cassi@mail.com", "C@t");
            assertEquals(1, uDao.getAllUsers().size());
            assertEquals("Catsi", result.username());
            assertEquals("Success", result.message());
            assertEquals(user, uDao.getUser("Catsi"));
        }

        @Test
        public void testRegisterFailEmailInvalid() throws DataAccessException {
            RegisterRequest r = new RegisterRequest("Catsi", "C@t", "Cassimail.com");
            RegisterResult result = u.register(r, uDao, aDao);
            assertEquals("Email is not valid", result.message());

            r = new RegisterRequest("Catsi2", "C@t", "Cassi@mailcom");
            result = u.register(r, uDao, aDao);
            assertEquals("Email is not valid", result.message());

            r = new RegisterRequest("Catsi3", "C@t", "Cassi@mail.org");
            result = u.register(r, uDao, aDao);
            assertEquals("Email is not valid", result.message());
        }

        @Test
        public void testRegisterFailUsernameTaken() throws DataAccessException {
            RegisterRequest r = new RegisterRequest("Catsi", "C@t", "Cassi@mail.com");
            u.register(r, uDao, aDao);

            r = new RegisterRequest("Catsi", "C@tti", "Cassi@mail.com");
            RegisterResult result = u.register(r, uDao, aDao);
            assertEquals("Username is already taken", result.message());
        }

        @Test
        public void testRegisterFailUsernameNull() throws DataAccessException {
            RegisterRequest r = new RegisterRequest(null, "C@t", "Cassi@mail.com");
            RegisterResult result = u.register(r, uDao, aDao);
            assertEquals("Username is null", result.message());
        }

        @Test
        public void testRegisterFailPasswordNull() throws DataAccessException {
            RegisterRequest r = new RegisterRequest("Catsi", null, "Cassi@mail.com");
            RegisterResult result = u.register(r, uDao, aDao);
            assertEquals("Password is null", result.message());
        }

        @Test
        public void testRegisterFailAuthDaoNull() throws DataAccessException {
            RegisterRequest r = new RegisterRequest("Catsi", "C@t", "Cassi@mail.com");
            RegisterResult result = u.register(r, uDao, null);
            assertEquals("AuthDao is null", result.message());
        }

        @Test
        public void testRegisterFailUserDaoNull() throws DataAccessException {
            RegisterRequest r = new RegisterRequest("Catsi", "C@t", "Cassi@mail.com");
            RegisterResult result = u.register(r, null, aDao);
            assertEquals("UserDao is null", result.message());
        }
    }


    @Nested
    class LoginTests{
        @BeforeEach
        void loginSetUp() throws DataAccessException {
            //Quickly registering user
            RegisterResult temp = u.register(new RegisterRequest("Catsi","C@t","Cassi@mail.com"),uDao,aDao);
            //And deleting their AuthData from the preemptive login
            AuthData auth = aDao.getAuth(temp.authToken());
            aDao.deleteAuth(auth);
        }

        @Test
        public void testLoginPass() throws DataAccessException {
            LoginRequest r = new LoginRequest("Catsi","C@t");
            LoginResult result = u.login(r,uDao,aDao);

            assertEquals(1,aDao.getAllAuths().size());
            assertEquals("Catsi",result.username());
            assertEquals("Success",result.message());
        }

        @Test
        public void testLoginFailUsernameDoesNotExist() throws DataAccessException {
            LoginRequest r = new LoginRequest("Dogsi","C@t");
            LoginResult result = u.login(r,uDao,aDao);
            assertEquals("Username doesn't exist",result.message());
        }

        @Test
        public void testLoginFailBadPassword() throws DataAccessException {
            LoginRequest r = new LoginRequest("Catsi","Dog");
            LoginResult result = u.login(r,uDao,aDao);
            assertEquals("Incorrect password",result.message());
        }

        @Test
        public void testLoginFailUsernameNull() throws DataAccessException {
            LoginRequest r = new LoginRequest(null,"C@t");
            LoginResult result = u.login(r,uDao,aDao);
            assertEquals("Username is null",result.message());
        }

        @Test
        public void testLoginFailUserDaoNull() throws DataAccessException {
            LoginRequest r = new LoginRequest("Catsi","C@t");
            LoginResult result = u.login(r,null,aDao);
            assertEquals("UserDao is null",result.message());
        }

        @Test
        public void testLoginFailAuthDaoNull() throws DataAccessException {
            LoginRequest r = new LoginRequest("Catsi","C@t");
            LoginResult result = u.login(r,uDao,null);
            assertEquals("AuthDao is null",result.message());
        }
    }

    @Nested
    class LogoutTests {
        String token;
        @BeforeEach
        void LogoutSetUp() throws DataAccessException {
            //Quickly registering user (which also logs them in)
            RegisterResult temp = u.register(new RegisterRequest("Catsi","C@t","Cassi@mail.com"),uDao,aDao);
            //Getting authToken
            AuthData auth = aDao.getAuth(temp.authToken());
            token = auth.authToken();
        }

        @Test
        public void testLogoutPass() throws DataAccessException {
            LogoutRequest r = new LogoutRequest(token);
            LogoutResult result = u.logout(r, aDao);

            assertTrue(aDao.getAllAuths().isEmpty());
            assertEquals("Success",result.message());
        }

        @Test
        public void testLogoutFailInvalidToken() throws DataAccessException {
            LogoutRequest r = new LogoutRequest("123456789");
            LogoutResult result = u.logout(r,aDao);
            assertEquals("Bad authentication token",result.message());
        }

        @Test
        public void testLogoutFailAuthDaoNull() throws DataAccessException {
            LogoutRequest r = new LogoutRequest(token);
            LogoutResult result = u.logout(r,null);
            assertEquals("AuthDao is null",result.message());
        }
    }
}
