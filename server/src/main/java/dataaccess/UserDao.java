package dataaccess;

import model.UserData;

import java.util.ArrayList;

//In phase 3 all of these Dao are going to store the info in memory directly, in lists, maps, etc.
public interface UserDao {
    void clear();
    UserData createUser(String username, String password, String email);
    UserData getUser(UserData user);
    void deleteUser(UserData user);
    //updateUser?
}

class MemoryUserDao implements UserDao{
    private final ArrayList<UserData> users = new ArrayList<>();

    public void clear(){
        users.clear();
    }

    public UserData createUser(String username, String password, String email) {
        //Creates a user and adds them to the database
        //Verify all data is not null, if it is, throw an error
        UserData user = new UserData(username, email, password);
        users.add(user);
        return user;
    }

    public UserData getUser(UserData user){
        for(UserData current: users){
            if(current.name().equals(user.name())){
                return current;
            }
        }
        return null;
        //Throw nullError saying that token does not exist in database
    }

    public void deleteUser(UserData user){
        //Check that auth exists first and throw error if it does not
        users.removeIf(current -> current.name().equals(user.name()));
        //^For-each loop w/ if statement inside
    }
}