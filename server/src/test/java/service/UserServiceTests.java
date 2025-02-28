package service;

import requests.*;
import results.*;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    UserDao uDao;
    AuthDao aDao;
    UserService u;
    @BeforeEach
    void setUp(){ //UserDao and AuthDao can be changed for checking SQL
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
            assertNull(result.message());
            assertEquals(user, uDao.getUser("Catsi"));
        }

        @Test
        public void testRegisterFailEmailInvalid(){
            RegisterRequest r = new RegisterRequest("Catsi", "C@t", null);
            RegisterRequest finalR = r;
            assertThrows(InputException.class,() -> u.register(finalR, uDao, aDao));
        }

        @Test
        public void testRegisterFailUsernameTaken() throws DataAccessException{
            RegisterRequest r = new RegisterRequest("Catsi", "C@t", "Cassi@mail.com");
            u.register(r, uDao, aDao);
            r = new RegisterRequest("Catsi", "C@tti", "Cassi@mail.com");
            RegisterRequest finalR = r;
            assertThrows(DuplicateException.class,() -> u.register(finalR, uDao, aDao));
        }

        @Test
        public void testRegisterFailUsernameNull(){
            RegisterRequest r = new RegisterRequest(null, "C@t", "Cassi@mail.com");
            assertThrows(InputException.class,() -> u.register(r, uDao, aDao));
        }

        @Test
        public void testRegisterFailPasswordNull(){
            RegisterRequest r = new RegisterRequest("Catsi", null, "Cassi@mail.com");
            assertThrows(InputException.class,() -> u.register(r, uDao, aDao));
        }

        @Test
        public void testRegisterFailAuthDaoNull(){
            RegisterRequest r = new RegisterRequest("Catsi", "C@t", "Cassi@mail.com");
            assertThrows(DaoException.class,() -> u.register(r, uDao, null));
        }

        @Test
        public void testRegisterFailUserDaoNull(){
            RegisterRequest r = new RegisterRequest("Catsi", "C@t", "Cassi@mail.com");
            assertThrows(DaoException.class,() -> u.register(r, null, aDao));
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
            assertNull(result.message());
        }

        @Test
        public void testLoginFailUsernameDoesNotExist(){
            LoginRequest r = new LoginRequest("Dogsi","C@t");
            assertThrows(AuthorizationException.class,() -> u.login(r,uDao,aDao));
        }

        @Test
        public void testLoginFailBadPassword(){
            LoginRequest r = new LoginRequest("Catsi","Dog");
            assertThrows(AuthorizationException.class,() -> u.login(r,uDao,aDao));
        }

        @Test
        public void testLoginFailUsernameNull() throws DataAccessException {
            LoginRequest r = new LoginRequest(null,"C@t");
            assertThrows(InputException.class,() -> u.login(r,uDao,aDao));
        }

        @Test
        public void testLoginFailUserDaoNull() throws DataAccessException {
            LoginRequest r = new LoginRequest("Catsi","C@t");
            assertThrows(DaoException.class,() -> u.login(r,null,aDao));
        }

        @Test
        public void testLoginFailAuthDaoNull() throws DataAccessException {
            LoginRequest r = new LoginRequest("Catsi","C@t");
            assertThrows(DaoException.class,() -> u.login(r,uDao,null));
        }
    }

    @Nested
    class LogoutTests {
        String token;
        @BeforeEach
        void logoutSetUp() throws DataAccessException {
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
            assertNull(result.message());
        }

        @Test
        public void testLogoutFailInvalidToken(){
            LogoutRequest r = new LogoutRequest("123456789");
            assertThrows(AuthorizationException.class,() -> u.logout(r,aDao));
        }

        @Test
        public void testLogoutFailAuthDaoNull(){
            LogoutRequest r = new LogoutRequest(token);
            assertThrows(DaoException.class,() -> u.logout(r,null));
        }
    }
}
