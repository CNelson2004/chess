package service;

import Requests.CreateRequest;
import Requests.JoinRequest;
import Requests.ListRequest;
import Results.CreateResult;
import Results.JoinResult;
import Results.ListResult;


public class GameService {
    CreateResult create(CreateRequest r){
        //Verify input
        //Validate authToken [getAuth() must not return null]
        //Check gameName isn't already taken [getGame(gameName) must return null] (optional)
        //Create new Game model object: Game g = new Game(gameData) [which has a gameID]
        //Insert new game into the database: GameDao.createGame(gameData);
        //Return CreateResult
        //(Don't automatically join game upon creation)
        throw new RuntimeException("Not implemented.");
    }
    JoinResult join(JoinRequest r){
        //Verify input
        //Validate authToken [getAuth() must not return null]
        //Get game based upon GameID [getGame(gameID)] (also make sure it exists)
        //Check that requested color isn't taken using Game you got
        //Get username by getting Authdata object with authToken [getAuth(authToken)]
        //update game with username being their color now [updateGame(gameData)]
        //put updated game into database
        //Return JoinResult
        throw new RuntimeException("Not implemented.");
    }
    ListResult list(ListRequest r){
        //Verify input
        //Validate authToken [getAuth() must not return null]
        //Get all the games [getAllGames()]
        //Return ListResult
        throw new RuntimeException("Not implemented.");
    }
}
