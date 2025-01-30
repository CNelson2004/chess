package dataaccess;

import chess.ChessGame;
import model.UserData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public interface GameDao {
    void clear();
    GameData createGame(UserData user, String gameName, String color);
    GameData getGame(GameData game);
    Collection<GameData> getAllGames();
    GameData updateGame(GameData game, String color, UserData user);
    void deleteGame(GameData game);
}

