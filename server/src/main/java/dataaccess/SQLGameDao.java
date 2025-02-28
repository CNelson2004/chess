package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.sql.*;
import java.util.Random;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDao implements GameDao{
    public SQLGameDao() throws DataAccessException {
        configureDatabase();
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    public GameData createGame(String gameName) throws DataAccessException{
        if(gameName==null){throw new DataAccessException("Game Name is null");}
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, gameID, game) VALUES (?, ?, ?, ?, ?)";
        Random rnd = new Random();
        int gameID = 100000 + rnd.nextInt(900000);
        ChessGame theGame = new ChessGame();
        var game = new Gson().toJson(theGame); //Changes game into a json string
        executeUpdate(statement,null,null,gameName,gameID,game);
        return new GameData(null,null,gameName,gameID,theGame);
    }

    public GameData readGame(ResultSet rs) throws SQLException{
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        var gameID = rs.getInt("gameID");
        String json = rs.getString("game");
        ChessGame game = new Gson().fromJson(json, ChessGame.class); //changes game from json back into ChessGame
        return new GameData(whiteUsername,blackUsername,gameName,gameID,game);
    }

    public GameData getGame(Integer gameID) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername, blackUsername, gameName, gameID, game FROM game WHERE gameID=?"; //Query statement
            try (var ps = conn.prepareStatement(statement)) { //Prepares the statement
                ps.setInt(1,gameID);  //Sets the questionmark (index of ? starting at 1, value to set ? to)
                try (var rs = ps.executeQuery()) {   //execute updates changes database without returning, query just returns
                    if (rs.next()) {
                        return readGame(rs);
                    }else{
                        throw new DataAccessException("Invalid Authorization token");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public GameData getGame(String name) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername, blackUsername, gameName, gameID, game FROM game WHERE gameName=?"; //Query statement
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1,name);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }else{
                        throw new DataAccessException("Invalid Authorization token");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public Collection<GameData> getAllGames() throws DataAccessException{
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername, blackUsername, gameName, gameID, game FROM game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    public GameData updateGame(GameData game, String color, String username) throws DataAccessException{
        deleteGame(game);
        GameData updatedGame = new GameData(game.whiteUsername(),username,game.gameName(),game.gameID(),game.game());
        if(color.equals("WHITE")){updatedGame = new GameData(username,game.blackUsername(),game.gameName(),game.gameID(),game.game());}
        //adding updatedGame into the database
        var statement = "INSERT INTO game (whiteUsername, blackUsername, gameName, gameID, game) VALUES (?, ?, ?, ?, ?)";
        var theGame = new Gson().toJson(updatedGame); //updated Game turned into json before being inserted.
        executeUpdate(statement,updatedGame.whiteUsername(),updatedGame.blackUsername(),game.gameName(),game.gameID(),theGame);
        return updatedGame;
    }

    public void deleteGame(GameData game) throws DataAccessException{
        var statement = "DELETE FROM game WHERE gameID=?";
        executeUpdate(statement, game.gameID());
    }

    static void executeUpdate(String statement, Object... params) throws DataAccessException { //... could be switched for []
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p){ps.setString(i + 1, p);}
                    else if (param instanceof Integer p){ps.setInt(i + 1, p);}
                    else if (param instanceof ChessGame p){ps.setString(i + 1, p.toString());}
                    else if (param == null){ps.setNull(i + 1, NULL);}
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    //'json' TEXT DEFAULT NULL, only used when you have more information
    private final String[] createStatements = { //Chessgame will be turned into a json, which will be put into the database
            """
            CREATE TABLE IF NOT EXISTS game (
            whiteUsername varchar(255),
            blackUsername varchar(255),
            gameName varchar(255) NOT NULL,
            gameID INTEGER NOT NULL,
            game TEXT DEFAULT NULL,
            PRIMARY KEY (gameID)
            );
            """
    }; //game is a json, which is a string     ^Should black and white usernames be null or empty strings.

    private void configureDatabase() throws DataAccessException {
        SQLAuthDao.configureDatabase(createStatements);
    }
}
