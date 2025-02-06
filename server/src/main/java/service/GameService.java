package service;

import Requests.CreateRequest;
import Requests.JoinRequest;
import Requests.ListRequest;
import Results.CreateResult;
import Results.JoinResult;
import Results.ListResult;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import model.AuthData;
import model.GameData;

import java.util.Collection;


public class GameService {
    boolean verifyJoinInput(String color, int gameID){
        if(!(color.equals("WHITE") || color.equals("BLACK"))){return false;}
        int digits = String.valueOf(gameID).length();
        return digits == 6;
    }

    boolean verifyDao(MemoryAuthDao aDao){
        return aDao != null;
    }

    boolean verifyDao(MemoryGameDao gDao){
        return gDao != null;
    }

    boolean verifyAuth(String authToken, MemoryAuthDao aDao){
        if(authToken == null) return false;
        if(aDao == null) return false;
        AuthData aData = aDao.getAuth(authToken);
        return aData != null;
    }

    public CreateResult create(CreateRequest r,MemoryAuthDao aDao, MemoryGameDao gDao) throws DataAccessException {
        //Verify input
        if(r.gameName()==null){return new CreateResult(null, "gameName is null");}
        if(!verifyDao(aDao)){return new CreateResult(null, "AuthDao is null");}
        if(!verifyDao(gDao)){return new CreateResult(null, "GameDao is null");}
        //Validate authToken
        if(!verifyAuth(r.authToken(),aDao)){return new CreateResult(null, "Invalid authToken");}
        //Check gameName isn't already taken (optional)
        if(gDao.getGame(r.gameName()) != null){
            return new CreateResult(null,"Game Name is already taken");}
        //Create new game (automatically inserted into database)
        //(You don't automatically join game upon creation)
        GameData game = gDao.createGame(r.gameName());
        //Return CreateResult
        return new CreateResult(game.gameID(),"Success");
    }
    public JoinResult join(JoinRequest r, MemoryAuthDao aDao, MemoryGameDao gDao) throws DataAccessException {
        //Verify input
        if (!verifyJoinInput(r.playerColor(),r.gameID())){return new JoinResult("Invalid Input");}
        if(!verifyDao(aDao)){return new JoinResult("AuthDao is null");}
        if(!verifyDao(gDao)){return new JoinResult("GameDao is null");}
        //Validate authToken [getAuth() must not return null]
        if (!verifyAuth(r.authToken(),aDao)){return new JoinResult("Invalid authToken");}
        //Get game based upon GameID [getGame(gameID)] (also make sure it exists)
        GameData game = gDao.getGame(r.gameID());
        //Check that requested color isn't taken using Game you got
        switch(r.playerColor()){
            case "WHITE":
                if(game.whiteUsername()!=null){return new JoinResult("That color is already taken");}
            case "BLACK":
                if(game.blackUsername()!=null){return new JoinResult("That color is already taken");}
        }
        //Get username by getting Authdata object with authToken [getAuth(authToken)]
        AuthData auth = aDao.getAuth(r.authToken());
        String username = auth.username();
        //update game with username being their color now (new game automatically added to database & old one deleted)
        GameData updatedGame = gDao.updateGame(game,r.playerColor(),username);
        //Return JoinResult
        return new JoinResult("Success");
    }
    public ListResult list(ListRequest r, MemoryAuthDao aDao, MemoryGameDao gDao){
        //Validate Input
        if(!verifyDao(aDao)){return new ListResult(null, "AuthDao is null");}
        if(!verifyDao(gDao)){return new ListResult(null, "GameDao is null");}
        //Validate authToken [getAuth() must not return null]
        if(!verifyAuth(r.authToken(),aDao)){return new ListResult(null,"Invalid authToken");}
        //Get all the games [getAllGames()]
        Collection<GameData> allGames = gDao.getAllGames();
        //Return ListResult
        return new ListResult(allGames,"Success");
    }
}
