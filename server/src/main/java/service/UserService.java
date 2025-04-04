package service;

import dataaccess.*;
import requests.*;
import results.*;
import model.*;

public class UserService {
    boolean verifyInput(String item, String type){
        if(item == null){return false;}
        switch(type){
            case "username", "password", "email":
                return true;
            //case "email":
                //return item.contains("@")&&item.contains(".com");
            default:
                throw new IllegalArgumentException("type not recognized");
        }
    }

    boolean verifyDao(UserDao uDao){
        return uDao != null;
    }

    boolean verifyDao(AuthDao aDao){
        return aDao != null;
    }

    static void authorizeToken(String authToken, AuthDao aDao) throws DataAccessException, AuthorizationException {
        if(aDao instanceof SQLAuthDao){
            try{
                verifyAuth(authToken,aDao);
            }catch(DataAccessException e){
                throw new AuthorizationException("Error: unauthorized");
            }
        }else{
            if(!verifyAuth(authToken,aDao)){throw new AuthorizationException("Error: unauthorized");}
        }
    }

    static boolean verifyAuth(String authToken, AuthDao aDao) throws DataAccessException {
        if(authToken == null){return false;}
        if(aDao == null){return false;}
        AuthData aData = aDao.getAuth(authToken);
        return aData != null;
    }

    public RegisterResult register(RegisterRequest r, UserDao uDao, AuthDao aDao) throws DataAccessException {
        //verifying input
        if(!(verifyInput(r.username(),"username"))){throw new InputException("Error: bad request");}
            //return new RegisterResult(null,null,"Username is null");}
        if(!(verifyInput(r.password(),"password"))){throw new InputException("Error: bad request");}
            //return new RegisterResult(null,null,"Password is null");}
        if(!(verifyInput(r.email(),"email"))){throw new InputException("Error: bad request");}
            //return new RegisterResult(null,null,"Email is not valid");}
        if(!(verifyDao(uDao))){throw new DaoException("Error: Database is null");}
            //return new RegisterResult(null,null,"UserDao is null");}
        if(!(verifyDao(aDao))){throw new DaoException("Error: Database is null");}
            //return new RegisterResult(null,null, "AuthDao is null");}
        //Checking if username is taken (need to put in try block due to SQL compatability
        if(uDao instanceof SQLUserDao) {
            try {
                uDao.getUser(r.username());
                throw new DuplicateException("Error: already taken");
            } catch (DataAccessException e) {
                //continue
            }
        }else {
            if (uDao.getUser(r.username()) != null) {
                throw new DuplicateException("Error: already taken");
            }
        }
        //Creating a new User model object (User automatically added to database)
        UserData user = uDao.createUser(r.username(),r.password(),r.email());
        //Login the user (create new AuthToken model object and insert it into the database)
        AuthData token = aDao.createAuth(user);
        //Returning RegisterResult
        return new RegisterResult(r.username(),token.authToken(),null);
    }


    public LoginResult login(LoginRequest r, UserDao uDao, AuthDao aDao) throws DataAccessException {
        //Verifying input
        if(!(verifyInput(r.username(),"username"))){throw new InputException("Error: bad request");}
        if(!(verifyInput(r.username(),"password"))){throw new InputException("Error: bad request");}
        if(!(verifyDao(uDao))){throw new DaoException("Error: Database is null");}
        if(!(verifyDao(aDao))){throw new DaoException("Error: Database is null");}
        //Checking the password (and changing depending on Dao)
        UserData user;
        if(uDao instanceof SQLUserDao){
            //user.password is hashed if coming from SQL
            try {
                user = uDao.getUser(r.username());
                if (!(((SQLUserDao)uDao).checkPassword(user,r.password()))){throw new AuthorizationException("Error: unauthorized");}
            } catch(DataAccessException e){
                throw new AuthorizationException("Error: unauthorized");
            }
        }else{
            user = uDao.getUser(r.username());
            if(user==null){throw new AuthorizationException("Error: unauthorized");}
            if (!user.password().equals(r.password())){throw new AuthorizationException("Error: unauthorized");}
        }
        //Creating an authToken for the user (automatically added to database)
        AuthData token = aDao.createAuth(user);
        //returning LoginResult
        return new LoginResult(r.username(),token.authToken(),null);
    }
    public LogoutResult logout(LogoutRequest r, AuthDao aDao) throws DataAccessException {
        //Verify authToken
        if(!(verifyDao(aDao))){throw new DaoException("Error: Database is null");}
        authorizeToken(r.authToken(),aDao);
        //Deleting the authData object
        AuthData auth = aDao.getAuth(r.authToken());
        aDao.deleteAuth(auth);
        //Returning LogoutResult
        return new LogoutResult(null);
    }
}
