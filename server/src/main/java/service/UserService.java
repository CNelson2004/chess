package service;

record RegisterRequest(String username, String password, String email){}
record RegisterResult(String username, String authToken, String message){}
record LoginRequest(String username, String password){}
record LoginResult(String username, String authToken, String message){}
record LogoutRequest(String authToken){}
record LogoutResult (String message){}

public class UserService {
    RegisterResult register(RegisterRequest r) {
        //1.Verify input
        //[1.5. Validate the passed in authToken if needed and see what user is connected to it]
        //2. Check username isn't already taken: getUser(username) should return null
        //3. Create new User model object: User u = new UserData(...);
        //4. Insert new user into the database: UserDao.createUser(u);
        //5. Login the user, (create new AuthToken model object and insert it into the database)
        //6. Create RegisterResult and return it
        throw new RuntimeException("Not implemented.");
    }

    LoginResult login(LoginRequest r){
        //Verify input
        //Retrieve user from dataaccess/database w/ username [getUser(username)]
        //Check given password against UserData password
        //Create new AuthToken [createAuth()]
        //[Dataaccess: Insert Authtoken into the database- addAuth(authData)]
        //return LoginResult
        throw new RuntimeException("Not implemented.");
    }
    LogoutResult logout(LogoutRequest r){
        //Verify input
        //Verify authToken [getAuth(authToken)]
        //Get username associated w/ authToken?
        //Delete authData object [deleteAuth()]
        //Return LogoutResult
        throw new RuntimeException("Not implemented.");
    }
}
