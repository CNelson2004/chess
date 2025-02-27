package dataaccess;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SQLAuthDaoTests {
    SQLAuthDao dao;
    @BeforeEach
    public void setUp() throws DataAccessException {
        dao = new SQLAuthDao();
    }

    @AfterEach
    public void setDown() throws DataAccessException {
        dao.clear();
    }

    @Test
    public void testCreateAuthPass() throws DataAccessException {
        UserData user = new UserData("Catsi","cat@gmail.com","Catty");
        AuthData r = dao.createAuth(user);
        assertEquals("Catsi",r.username());
    }

    @Test
    public void testCreateAuthIntFail(){
        UserData user = new UserData(null,"cat@gmail.com","Catty");
        assertThrows(DataAccessException.class,() -> dao.createAuth(user));
    }

    @Test
    public void testGetAuthPass() throws DataAccessException {
        //create the AuthData and put it into database
        UserData user = new UserData("Catsi","cat@gmail.com","Catty");
        AuthData data = dao.createAuth(user);
        //then get it
        String token = data.authToken();
        AuthData r = dao.getAuth(token);
        assertEquals(data,r);
    }

    @Test
    public void testGetAuthFailInvalidAuth(){
        assertThrows(DataAccessException.class,() -> dao.getAuth("12345"));
    }

    @Test
    public void testGetAuthFailNullAuth(){
        assertThrows(DataAccessException.class,() -> dao.getAuth(null));
    }

    @Test
    public void testGetAllAuthsOneEntryPass() throws DataAccessException {
        //checking that's it empty
        assertEquals(0,dao.getAllAuths().size());
        //create the AuthData and put it into database
        UserData user = new UserData("Catsi","cat@gmail.com","Catty");
        dao.createAuth(user);
        //Check database size
        assertEquals(1,dao.getAllAuths().size());
    }

    @Test
    public void testGetAllAuthsTwoEntriesPass() throws DataAccessException {
        //checking that's it empty
        assertEquals(0,dao.getAllAuths().size());
        //create the AuthData and put it into database
        UserData user = new UserData("Catsi","cat@gmail.com","Catty");
        UserData user2 = new UserData("Dogsi","dog@gmail.com","Doggi");
        dao.createAuth(user);
        dao.createAuth(user2);
        //Check database size
        assertEquals(2,dao.getAllAuths().size());
    }

    @Test
    public void testGetAllAuthsTwoEntriesDeleteOnePass() throws DataAccessException {
        //checking that's it empty
        assertEquals(0,dao.getAllAuths().size());
        //create the AuthData and put it into database
        UserData user = new UserData("Catsi","cat@gmail.com","Catty");
        UserData user2 = new UserData("Dogsi","dog@gmail.com","Doggi");
        AuthData data = dao.createAuth(user);
        dao.createAuth(user2);
        dao.deleteAuth(data);
        //Check database size
        assertEquals(1,dao.getAllAuths().size());
    }

    //@Test
    //public void testGetAllAuthsFail() throws DataAccessException {}
    //I don't know how to make this fail

    @Test
    public void testDeleteAuthPass() throws DataAccessException {
        //create the AuthData and put it into database
        UserData user = new UserData("Catsi","cat@gmail.com","Catty");
        AuthData data = dao.createAuth(user);
        //then delete it
        dao.deleteAuth(data);
        assertEquals(0,dao.getAllAuths().size());
    }

    @Test
    public void testDeleteAuthFail(){
        //Don't know how to throw DataAccess exception
        assertThrows(NullPointerException.class,() -> dao.deleteAuth(null));
    }

    @Test
    public void testClearPass() throws DataAccessException {
        //create the AuthData and put it into database
        UserData user = new UserData("Catsi","cat@gmail.com","Catty");
        UserData user2 = new UserData("Dogsi","dog@gmail.com","Doggi");
        dao.createAuth(user);
        dao.createAuth(user2);
        //clearing it
        dao.clear();
        assertEquals(0,dao.getAllAuths().size());
    }
}
