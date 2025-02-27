package dataaccess;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDaoTests {
    SQLUserDao dao;
    @BeforeEach
    public void setUp() throws DataAccessException {
        dao = new SQLUserDao();
    }

    @AfterEach
    public void setDown() throws DataAccessException {
        dao.clear();
    }

    @Test
    public void testCreateUserPass() throws DataAccessException {
        UserData r = dao.createUser("Catsi","Catty","cat@mail.com");
        assertEquals("Catsi",r.name());
        assertEquals("cat@mail.com",r.email());
        assertNotEquals("Catty",r.password());
    }

    @Test
    public void testCreateUserFail() throws DataAccessException {
        assertThrows(DataAccessException.class,() -> dao.createUser(null,null,null));
    }

    @Test
    public void testGetUserPass() throws DataAccessException {
        UserData data = dao.createUser("Catsi","Catty","cat@mail.com");
        UserData r = dao.getUser(data.name());
        assertEquals(data,r);
        assertNotEquals("Catty",r.password());
    }

    @Test
    public void testGetUserFail() throws DataAccessException {
        assertThrows(DataAccessException.class,() -> dao.getUser("Dogsi"));
    }

    @Test
    public void testGetAllUsersOnePass() throws DataAccessException {
        assertEquals(0,dao.getAllUsers().size());
        dao.createUser("Catsi","Catty","cat@mail.com");
        assertEquals(1,dao.getAllUsers().size());
    }

    @Test
    public void testGetAllUsersTwoPass() throws DataAccessException {
        assertEquals(0,dao.getAllUsers().size());
        dao.createUser("Catsi","Catty","cat@mail.com");
        dao.createUser("Dogsi","Doggy","dog@mail.com");
        assertEquals(2,dao.getAllUsers().size());
    }

    //@Test
    //public void testGetAllUsersFail() throws DataAccessException {}
    //I don't know how to make this fail

    @Test
    public void testCheckPasswordPass() throws DataAccessException {
        UserData user = dao.createUser("Catsi","Catty","cat@mail.com");
        assertTrue(dao.checkPassword(user, "Catty"));
    }

    @Test
    public void testCheckPasswordFail() throws DataAccessException {
        UserData user = dao.createUser("Catsi","Catty","cat@mail.com");
        assertFalse(dao.checkPassword(user, "Doggy"));
    }

    @Test
    public void testClearPass() throws DataAccessException {
        dao.createUser("Catsi","Catty","cat@mail.com");
        dao.createUser("Dogsi","Doggy","dog@mail.com");
        dao.clear();
        assertEquals(0,dao.getAllUsers().size());
    }
}
