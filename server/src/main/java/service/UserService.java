package service;

import Requests.LoginRequest;
import Requests.LogoutRequest;
import Requests.RegisterRequest;
import Results.LoginResult;
import Results.LogoutResult;
import Results.RegisterResult;
import dataaccess.*;
import model.AuthData;
import model.UserData;

public class UserService {
    boolean verifyInput(String item, String type){
        if(item == null) return false;
        switch(type){
            case "username", "password":
                return true;
            case "email":
                return item.contains("@")&&item.contains(".com");
            default:
                throw new IllegalArgumentException("type not recognized");
        }
    }

    boolean verifyDao(MemoryUserDao uDao){
        return uDao != null;
    }

    boolean verifyDao(MemoryAuthDao aDao){
        return aDao != null;
    }

    boolean verifyAuth(String authToken, MemoryAuthDao aDao){
        if(authToken == null) return false;
        if(aDao == null) return false;
        AuthData aData = aDao.getAuth(authToken);
        return aData != null;
    }

    public RegisterResult register(RegisterRequest r, MemoryUserDao uDao, MemoryAuthDao aDao) throws DataAccessException {
        //verifying input
        if(!(verifyInput(r.username(),"username"))){
            return new RegisterResult(null,null,"Username is null");}
        if(!(verifyInput(r.password(),"password"))){
            return new RegisterResult(null,null,"Password is null");}
        if(!(verifyInput(r.email(),"email"))){
            return new RegisterResult(null,null,"Email is not valid");}
        if(!(verifyDao(uDao))){
            return new RegisterResult(null,null,"UserDao is null");}
        if(!(verifyDao(aDao))){
            return new RegisterResult(null,null, "AuthDao is null");}
        //Checking if username is taken
        if(uDao.getUser(r.username()) != null){
            return new RegisterResult(null,null,"Username is already taken");}
        //Creating a new User model object (User automatically added to database)
        UserData user = uDao.createUser(r.username(),r.password(),r.email());
        //Login the user (create new AuthToken model object and insert it into the database)
        AuthData token = aDao.createAuth(user);
        //Returning RegisterResult
        return new RegisterResult(r.username(),token.authToken(),"Success");
    }

    public LoginResult login(LoginRequest r, MemoryUserDao uDao, MemoryAuthDao aDao) throws DataAccessException {
        //Verifying input
        if(!(verifyInput(r.username(),"username"))){
            return new LoginResult(null,null,"Username is null");}
        if(!(verifyInput(r.username(),"password"))){
            return new LoginResult(null,null,"Password is null");}
        if(!(verifyDao(uDao))){
            return new LoginResult(null,null,"UserDao is null");}
        if(!(verifyDao(aDao))){
            return new LoginResult(null,null, "AuthDao is null");}
        //Checking the password
        UserData user = uDao.getUser(r.username());
        if (user==null){
            return  new LoginResult(null,null,"Username doesn't exist");}
        if (!user.password().equals(r.password())){
            return  new LoginResult(null,null,"Incorrect password");}
        //Creating an authToken for the user (automatically added to database)
        AuthData token = aDao.createAuth(user);
        //returning LoginResult
        return new LoginResult(r.username(),token.authToken(),"Success");
    }
    public LogoutResult logout(LogoutRequest r, MemoryAuthDao aDao) throws DataAccessException {
        //Verify authToken
        if(!(verifyDao(aDao))){return new LogoutResult("AuthDao is null");}
        if(!verifyAuth(r.authToken(),aDao)){return new LogoutResult("Bad authentication token");}
        //Deleting the authData object
        AuthData auth = aDao.getAuth(r.authToken());
        aDao.deleteAuth(auth);
        //Returning LogoutResult
        return new LogoutResult("Success");
    }
}
