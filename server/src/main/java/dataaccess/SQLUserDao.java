package dataaccess;

import model.UserData;

import java.util.Collection;

import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserDao implements UserDao{
    public void clear(){}

//    void storeUserPassword(String username, String clearTextPassword) {
//        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
//
//        // write the hashed password in database along with the user's other information
//        writeHashedPasswordToDatabase(username, hashedPassword);  <- store in hashed column of user table in sql
//    }
//
//    boolean verifyUser(String username, String providedClearTextPassword) {
//        // read the previously hashed password from the database
//        var hashedPassword = readHashedPasswordFromDatabase(username);
//
//        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
//    }

    public UserData createUser(String username, String password, String email){return null;}

    public UserData getUser(String username){return null;}

    public Collection<UserData> getAllUsers(){return null;}
}
