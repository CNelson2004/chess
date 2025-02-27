package dataaccess;

import chess.ChessGame;
import model.UserData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public interface GameDao {
    void clear();
    GameData createGame(String gameName) throws DataAccessException;
    GameData getGame(Integer gameID);
    GameData getGame(String gameName);
    Collection<GameData> getAllGames();
    GameData updateGame(GameData game, String color, String username) throws DataAccessException;
    void deleteGame(GameData game) throws DataAccessException;
}

