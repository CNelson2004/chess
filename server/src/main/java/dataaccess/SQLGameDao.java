package dataaccess;

import model.GameData;

import java.util.Collection;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDao implements GameDao{
    public void clear(){}

    public GameData createGame(String gameName) throws DataAccessException{return null;}

    public GameData getGame(String gameID){return null;}

    public Collection<GameData> getAllGames(){return null;}

    public GameData updateGame(GameData game, String color, String username) throws DataAccessException{return null;}

    public void deleteGame(GameData game) throws DataAccessException{}
}
