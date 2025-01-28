package service;

record ClearResult(String message){}

public class ClearService {
    ClearResult clear(){
        //Call clear on all the Daos, AuthDao.clear(), GameDao.clear(), UserDao.clear()
        //Everything is deleted from database
        //Return ClearResult
        throw new RuntimeException("Not implemented.");
    }
}
