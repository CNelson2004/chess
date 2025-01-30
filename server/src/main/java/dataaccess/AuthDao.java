package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.UUID;

//Do Dao's, and write tests for them, and make htem work, then tackle one endpoint at a time in the service section
interface AuthDao {
    void clear();
    AuthData createAuth(UserData user);
    AuthData getAuth(AuthData auth);
    void deleteAuth(AuthData auth);
}

class MemoryAuthDao implements AuthDao{
    private final ArrayList<AuthData> authTokens = new ArrayList<>();

    public void clear(){
        authTokens.clear();
    }

    public AuthData createAuth(UserData user) {
        //Creates an AuthData(Authtoken) and adds it to the database.
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token,user.name());
        authTokens.add(authData);
        return authData;
    }

    public AuthData getAuth(AuthData auth){
        for(AuthData token: authTokens){
            if(token.authToken().equals(auth.authToken())){
                return token;
            }
        }
        return null;
        //Throw nullError saying that token does not exist in database
    }

    public void deleteAuth(AuthData auth){
        //Check that auth exists first and throw error if it does not
        authTokens.removeIf(token -> token.authToken().equals(auth.authToken()));
        //^For-each loop w/ if statement inside
    }
}