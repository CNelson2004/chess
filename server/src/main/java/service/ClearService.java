package service;
import Results.ClearResult;
import dataaccess.*;

public class ClearService {
    public ClearResult clear(UserDao uDao, AuthDao aDao, GameDao gDao) {
        aDao.clear();
        gDao.clear();
        uDao.clear();
        return new ClearResult(null);
    }
}
