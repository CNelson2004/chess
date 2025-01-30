package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class MemoryGameDao implements GameDao{
    private static final ArrayList<GameData> games = new ArrayList<>();

    public void clear(){
        getAllGames().clear();
    }

    public Collection<GameData> getAllGames(){
        return games;
    }

    public GameData createGame(UserData user, String gameName, String color) {
        int gameID = UUID.randomUUID().hashCode();
        GameData game = new GameData(user.name(),null,gameName,gameID,new ChessGame());
        if(color.equals("BLACK")){game = new GameData(null,user.name(),gameName,gameID,new ChessGame());}
        return game;
    }

    public GameData getGame(GameData game){
        for(GameData current: games){
            if(current.gameID() == game.gameID()){
                return current;
            }
        }
        return null;
        //Throw nullError saying that token does not exist in database
    }

    public void deleteGame(GameData game){
        //Check that auth exists first and throw error if it does not
        games.removeIf(current -> current.gameID() == game.gameID());
    }

    public GameData updateGame(GameData game, String color, UserData user){
        //Creates a new GameData and deletes the old one
        deleteGame(game);
        GameData updatedGame = new GameData(game.whiteUsername(),user.name(),game.gameName(),game.gameID(),game.game());
        if(color.equals("WHITE")){updatedGame = new GameData(user.name(),game.blackUsername(),game.gameName(),game.gameID(),game.game());}
        games.add(updatedGame);
        return updatedGame;
    }
}
