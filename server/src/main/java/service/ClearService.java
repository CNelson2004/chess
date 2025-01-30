package service;
import dataaccess.*;

record ClearResult(String message){}

public class ClearService {
    public ClearResult clear(UserDao uDao, AuthDao aDao, GameDao gDao) {
        aDao.clear();
        gDao.clear();
        uDao.clear();
        return new ClearResult(null);
    }
}
