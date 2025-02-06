package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryUserDao implements UserDao{
    private final ArrayList<UserData> users = new ArrayList<>();

    public void clear(){
        getAllUsers().clear();
    }

    public Collection<UserData> getAllUsers(){return users;}

    public UserData createUser(String username, String password, String email) {
        //Creates a user and adds them to the database
        //Verify all data is not null, if it is, throw an error
        UserData user = new UserData(username, email, password);
        users.add(user);
        return user;
    }

    public UserData getUser(String username){
        for(UserData current: users){
            if(current.name().equals(username)){
                return current;
            }
        }
        return null;
        //Throw nullError saying that token does not exist in database
    }

    public void deleteUser(UserData user) throws DataAccessException {
        if(user==null){throw new DataAccessException("UserData is null");}
        users.removeIf(current -> current.name().equals(user.name()));
        //^For-each loop w/ if statement inside
    }
}
