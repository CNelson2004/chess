package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class MemoryAuthDao implements AuthDao{
    private final ArrayList<AuthData> authTokens = new ArrayList<>();

    public void clear(){
        getAllAuths().clear();
    }

    public Collection<AuthData> getAllAuths(){return authTokens;}

    public AuthData createAuth(UserData user) throws DataAccessException{
        if(user==null){throw new DataAccessException("UserData is null");}
        //Creates an AuthData(Authtoken) and adds it to the database.
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token,user.name());
        authTokens.add(authData);
        return authData;
    }

    public AuthData getAuth(String aToken){
        for(AuthData token: authTokens){
            if(token.authToken().equals(aToken)){
                return token;
            }
        }
        return null;
        //Throw nullError saying that token does not exist in database
    }

    public void deleteAuth(AuthData auth) throws DataAccessException{
        if(auth==null){throw new DataAccessException("AuthData is null");}
        //Check that auth exists first and throw error if it does not
        authTokens.removeIf(token -> token.authToken().equals(auth.authToken()));
        //^For-each loop w/ if statement inside
    }
}
