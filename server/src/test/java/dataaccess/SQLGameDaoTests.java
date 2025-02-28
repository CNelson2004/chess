package dataaccess;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SQLGameDaoTests {
    SQLGameDao dao;
    @BeforeEach
    public void setUp() throws DataAccessException {
        dao = new SQLGameDao();
    }

    @AfterEach
    public void setDown() throws DataAccessException {
        dao.clear();
    }

    @Test
    public void testCreateGamePass() throws DataAccessException {
        GameData r = dao.createGame("test");
        assertEquals("test",r.gameName());
    }

    @Test
    public void testCreateGameFail() throws DataAccessException {
        assertThrows(DataAccessException.class,() -> dao.createGame(null));
    }

    @Test
    public void testGetGamePass() throws DataAccessException {
        GameData data = dao.createGame("test");
        GameData r = dao.getGame(data.gameName());
        assertEquals(data.gameID(),r.gameID());
        r = dao.getGame(data.gameID());
        assertEquals(data.gameID(),r.gameID());
    }

    @Test
    public void testGetGameFail() throws DataAccessException {
        assertThrows(DataAccessException.class,() -> dao.getGame("theGame"));
    }

    @Test
    public void testGetAllGamesOnePass() throws DataAccessException {
        assertEquals(0,dao.getAllGames().size());
        dao.createGame("test");
        assertEquals(1,dao.getAllGames().size());
    }

    @Test
    public void testGetAllGamesTwoPass() throws DataAccessException {
        assertEquals(0,dao.getAllGames().size());
        dao.createGame("test");
        dao.createGame("test2");
        assertEquals(2,dao.getAllGames().size());
    }

    @Test
    public void testGetAllGamesTwoEntriesDeleteOnePass() throws DataAccessException {
        assertEquals(0,dao.getAllGames().size());
        GameData data = dao.createGame("test");
        dao.createGame("test2");
        dao.deleteGame(data);
        assertEquals(1,dao.getAllGames().size());
    }

    //@Test
    //public void testGetAllAuthsFail() throws DataAccessException {}
    //I don't know how to make this fail

    @Test
    public void testDeleteGamePass() throws DataAccessException {
        GameData data = dao.createGame("test");
        dao.deleteGame(data);
        assertEquals(0,dao.getAllGames().size());
    }

    @Test
    public void testDeleteGameFail() throws DataAccessException {
        assertThrows(NullPointerException.class,() -> dao.deleteGame(null));
    }

    @Test
    public void testUpdateGamePass() throws DataAccessException {
        GameData data = dao.createGame("test");
        dao.updateGame(data,"WHITE","Catsi");
        assertEquals(1,dao.getAllGames().size());
        GameData game = dao.getGame("test");
        assertEquals(data.gameID(),game.gameID());
        assertEquals("Catsi",game.whiteUsername());
        assertEquals(data.blackUsername(),game.blackUsername());
    }

    @Test
    public void testUpdateGameFail() throws DataAccessException {
        assertThrows(NullPointerException.class,() -> dao.updateGame(null,"WHITE","Catsi"));
    }

    @Test
    public void clearGamePass() throws DataAccessException {
        dao.createGame("test");
        dao.createGame("test2");
        dao.clear();
        assertEquals(0,dao.getAllGames().size());
    }
}
