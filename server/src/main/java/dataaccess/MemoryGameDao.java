package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
//import java.util.UUID;

public class MemoryGameDao implements GameDao{
    private final ArrayList<GameData> games = new ArrayList<>();

    public void clear(){
        getAllGames().clear();
    }

    public Collection<GameData> getAllGames(){
        return games;
    }

    public GameData createGame(String gameName) throws DataAccessException {
        //(You don't automatically join game upon creation)
        //int gameID = UUID.randomUUID().hashCode();
        Random rnd = new Random();
        int gameID = 100000 + rnd.nextInt(900000);
        GameData theGame = new GameData(null, null, gameName, gameID, new ChessGame());
        //Making sure you don't get two games with the same ID.
        boolean flag;
        while (true) {
            flag = false;
            for (GameData game : games) {
                theGame = new GameData(null, null, gameName, gameID, new ChessGame());
                if (theGame.gameID() == game.gameID()) {flag = true;}
            }
            if (!flag) break;
        }
        if (theGame.gameID() == -1) {throw new DataAccessException("Couldn't create a game");}
            games.add(theGame);
            return theGame;
    }

    public GameData getGame(String gameName){
        for(GameData current: games){
            if(current.gameName().equals(gameName)){
                return current;
            }
        }
        return null;
        //Throw nullError saying that token does not exist in database
    }

    public GameData getGame(Integer gameID){
        for(GameData current: games){
            if(current.gameID()==gameID){
                return current;
            }
        }
        return null;
        //Throw nullError saying that token does not exist in database
    }

    public void deleteGame(GameData game) throws DataAccessException {
        if(game==null){throw new DataAccessException("GameData is null");}
        games.remove(game);
    }

    public GameData updateGame(GameData game, String color, String username) throws DataAccessException {
        //Creates a new GameData and deletes the old one
        if(game==null){throw new DataAccessException("GameData is null");}
        deleteGame(game);
        GameData updatedGame = new GameData(game.whiteUsername(),username,game.gameName(),game.gameID(),game.game());
        if(color.equals("WHITE")){updatedGame = new GameData(username,game.blackUsername(),game.gameName(),game.gameID(),game.game());}
        games.add(updatedGame);
        games.remove(game);
        return updatedGame;
    }
}
