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
        if(r.gameName()==null){throw new InputException("Error: bad request");}
        if(!verifyDao(aDao)){throw new DaoException("Error: Database is null");}
        if(!verifyDao(gDao)){throw new DaoException("Error: Database is null");}
        //Validate authToken
        if(!verifyAuth(r.authToken(),aDao)){throw new AuthorizationException("Error: unauthorized");}
        //Check gameName isn't already taken (optional)
        if(gDao.getGame(r.gameName()) != null){throw new DuplicateException("Error: already taken");}
        //Create new game (automatically inserted into database)
        //(You don't automatically join game upon creation)
        GameData game = gDao.createGame(r.gameName());
        //Return CreateResult
        return new CreateResult(game.gameID(),"Success");
    }
    public JoinResult join(JoinRequest r, MemoryAuthDao aDao, MemoryGameDao gDao) throws DataAccessException {
        //Verify input
        if (!verifyJoinInput(r.playerColor(),r.gameID())){throw new InputException("Error: bad request");}
        if(!verifyDao(aDao)){throw new DaoException("Error: Database is null");}
        if(!verifyDao(gDao)){throw new DaoException("Error: Database is null");}
        //Validate authToken [getAuth() must not return null]
        if (!verifyAuth(r.authToken(),aDao)){throw new AuthorizationException("Error: unauthorized");}
        //Get game based upon GameID [getGame(gameID)] (also make sure it exists)
        GameData game = gDao.getGame(r.gameID());
        //Check that requested color isn't taken using Game you got
        switch(r.playerColor()){
            case "WHITE":
                if(game.whiteUsername()!=null){throw new DuplicateException("Error: already taken");}
            case "BLACK":
                if(game.blackUsername()!=null){throw new DuplicateException("Error: already taken");}
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
        if(!verifyDao(aDao)){throw new DaoException("Error: Database is null");}
        if(!verifyDao(gDao)){throw new DaoException("Error: Database is null");}
        //Validate authToken [getAuth() must not return null]
        if(!verifyAuth(r.authToken(),aDao)){throw new AuthorizationException("Error: unauthorized");}
        //Get all the games [getAllGames()]
        Collection<GameData> allGames = gDao.getAllGames();
        //Return ListResult
        return new ListResult(allGames,"Success");
    }
}
