package dataaccess;

import chess.ChessGame;
import model.UserData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public interface GameDao {
    void clear() throws DataAccessException;
    GameData createGame(String gameName) throws DataAccessException;
    GameData getGame(Integer gameID) throws DataAccessException;
    GameData getGame(String gameName) throws DataAccessException;
    Collection<GameData> getAllGames() throws DataAccessException;
    GameData updateGame(GameData game, String color, String username) throws DataAccessException;
    void deleteGame(GameData game) throws DataAccessException;
}

