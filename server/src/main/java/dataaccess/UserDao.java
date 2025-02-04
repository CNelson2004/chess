package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

//In phase 3 all of these Dao are going to store the info in memory directly, in lists, maps, etc.
public interface UserDao {
    void clear();
    UserData createUser(String username, String password, String email);
    UserData getUser(String username);
    void deleteUser(UserData user);
    Collection<UserData> getAllUsers();
    //updateUser?
}

