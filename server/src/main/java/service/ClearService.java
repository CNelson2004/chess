package service;
import Results.ClearResult;
import dataaccess.*;

public class ClearService {
    public ClearResult clear(UserDao uDao, AuthDao aDao, GameDao gDao) {
        if(uDao == null || aDao == null || gDao == null){
            throw new DaoException("Error: Database is null"); //500
        }
        aDao.clear();
        gDao.clear();
        uDao.clear();
        return new ClearResult("Success");
    }
}
