package service;

import dataaccess.*;
import requests.*;
import results.*;
import dataaccess.DataAccessException;
import model.*;

import java.util.Collection;

public class GameService {
    boolean verifyJoinInput(String color, int gameID){
        if(color == null){return false;}
        if(!(color.equals("WHITE") || color.equals("BLACK"))){return false;}
        int digits = String.valueOf(gameID).length();
        return digits == 6;
    }

    boolean verifyDao(AuthDao aDao){
        return aDao != null;
        //return UserService.verifyDao(aDao); //<-Another option if you make verifyDao static in UserService
    }

    boolean verifyDao(GameDao gDao){
        return gDao != null;
    }

    boolean verifyInput(AuthDao aDao, GameDao gDao, String authToken) throws DataAccessException, DaoException, AuthorizationException {
        if(!verifyDao(aDao)){throw new DaoException("Error: Database is null");}
        if(!verifyDao(gDao)){throw new DaoException("Error: Database is null");}
        //Validate authToken
        UserService.authorizeToken(authToken,aDao);
        return true;
    }

    public CreateResult create(CreateRequest r,AuthDao aDao, GameDao gDao) throws DataAccessException {
        //Verify input
        if(r.gameName()==null){throw new InputException("Error: bad request");}
        verifyInput(aDao,gDao,r.authToken());
        //Check gameName isn't already taken (optional)
        if(gDao instanceof SQLGameDao){
            try{
                gDao.getGame(r.gameName());
                throw new DuplicateException("Error: already taken");
            }catch(DataAccessException e){
                //continue
            }
        }else{
            if(gDao.getGame(r.gameName()) != null){throw new DuplicateException("Error: already taken");}
        }

        //Create new game (automatically inserted into database)
        //(You don't automatically join game upon creation)
        GameData game = gDao.createGame(r.gameName());
        //Return CreateResult
        return new CreateResult(game.gameID(),null);
    }
    public JoinResult join(JoinRequest r, AuthDao aDao, GameDao gDao) throws DataAccessException {
        //Verify input
        if (!verifyJoinInput(r.playerColor(),r.gameID())){throw new InputException("Error: bad request");}
        verifyInput(aDao,gDao,r.authToken());
        //Get game based upon GameID [getGame(gameID)] (also make sure it exists)
        GameData game = gDao.getGame(r.gameID());
        //Check that requested color isn't taken using Game you got
        switch(r.playerColor()){
            case "WHITE":
                if(game.whiteUsername()!=null){throw new DuplicateException("Error: already taken");}
                else{break;}
            case "BLACK":
                if(game.blackUsername()!=null){throw new DuplicateException("Error: already taken");}
                else{break;}
            default:
                throw new InputException("Error: bad request");
        }
        //Get username by getting Authdata object with authToken [getAuth(authToken)]
        AuthData auth = aDao.getAuth(r.authToken());
        String username = auth.username();
        //update game with username being their color now (new game automatically added to database & old one deleted)
        gDao.updateGame(game,r.playerColor(),username);
        //Return JoinResult
        return new JoinResult(null);
    }
    public ListResult list(ListRequest r, AuthDao aDao, GameDao gDao) throws DataAccessException {
        //Validate Input
        verifyInput(aDao,gDao,r.authToken());
        //Get all the games [getAllGames()]
        Collection<GameData> allGames = gDao.getAllGames();
        //Return ListResult
        return new ListResult(allGames,null);
    }
}
