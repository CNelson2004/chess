package passoff.service;

import Requests.*;
import Results.*;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    MemoryUserDao uDao;
    MemoryAuthDao aDao;
    UserService u;
    @BeforeEach void setUp(){
        uDao = new MemoryUserDao();
        aDao = new MemoryAuthDao();
        u = new UserService();
    }

    @Test
    public void testRegisterPass(){
        RegisterRequest r = new RegisterRequest("Catsi","C@t","Cassi@mail.com");
        RegisterResult result = u.register(r,uDao,aDao);

        UserData user = new UserData("Catsi","Cassi@mail.com","C@t");
        assertEquals(1, uDao.getAllUsers().size());
        assertEquals("Catsi",result.username());
        assertEquals("Success",result.message());
        assertEquals(user,uDao.getUser("Catsi"));
    }

    @Test
    public void testRegisterFailEmailInvalid(){
        RegisterRequest r = new RegisterRequest("Catsi","C@t","Cassimail.com");
        RegisterResult result = u.register(r,uDao,aDao);
        assertEquals("Email is not valid",result.message());

        r = new RegisterRequest("Catsi2","C@t","Cassi@mailcom");
        result = u.register(r,uDao,aDao);
        assertEquals("Email is not valid",result.message());

        r = new RegisterRequest("Catsi3","C@t","Cassi@mail.org");
        result = u.register(r,uDao,aDao);
        assertEquals("Email is not valid",result.message());
    }

    @Test
    public void testRegisterFailUsernameTaken(){
        RegisterRequest r = new RegisterRequest("Catsi","C@t","Cassi@mail.com");
        u.register(r,uDao,aDao);

        r = new RegisterRequest("Catsi","C@tti","Cassi@mail.com");
        RegisterResult result = u.register(r,uDao,aDao);
        assertEquals("Username is already taken",result.message());
    }

    @Test
    public void testRegisterFailUsernameNull(){
        RegisterRequest r = new RegisterRequest(null,"C@t","Cassi@mail.com");
        RegisterResult result = u.register(r,uDao,aDao);
        assertEquals("Username is null",result.message());
    }

    @Test
    public void testRegisterFailPasswordNull(){
        RegisterRequest r = new RegisterRequest("Catsi",null,"Cassi@mail.com");
        RegisterResult result = u.register(r,uDao,aDao);
        assertEquals("Password is null",result.message());
    }

    @Test
    public void testRegisterFailAuthDaoNull(){
        RegisterRequest r = new RegisterRequest("Catsi","C@t","Cassi@mail.com");
        RegisterResult result = u.register(r,uDao,null);
        assertEquals("AuthDao is null",result.message());
    }

    @Test
    public void testRegisterFailUserDaoNull(){
        RegisterRequest r = new RegisterRequest("Catsi","C@t","Cassi@mail.com");
        RegisterResult result = u.register(r,null,aDao);
        assertEquals("UserDao is null",result.message());
    }

    @Test
    public void testLoginPass(){}

    @Test
    public void testLoginFail(){}

    @Test
    public void testLogoutPass(){}

    @Test
    public void testLogoutFail(){}

}
