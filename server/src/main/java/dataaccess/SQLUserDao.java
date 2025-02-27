package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.Collection;

import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserDao implements UserDao{
//    public SQLUserDao() throws DataAccessException {
//        configureDatabase();
//    }

    public void clear(){}

//    void storeUserPassword(String username, String clearTextPassword) {
//        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
//
//        // write the hashed password in database along with the user's other information
//        writeHashedPasswordToDatabase(username, hashedPassword);  //<- store in hashed column of user table in sql
//    }
//
//    boolean verifyUser(String username, String providedClearTextPassword) {
//        // read the previously hashed password from the database
//        var hashedPassword = readHashedPasswordFromDatabase(username);
//
//        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
//    }

    public UserData createUser(String username, String password, String email){return null;}

    //Helper function to getAuth which reads the data from the datatable
//    private AuthData readUser(ResultSet rs) throws SQLException {
//        var username = rs.getString("username");
//        var authToken = rs.getString("authToken");
//        return new AuthData(username,authToken);
//    }

    public UserData getUser(String username){return null;}

    public Collection<UserData> getAllUsers(){return null;}

//    private void executeUpdate(String statement, String... params) throws DataAccessException {
//        try (var conn = DatabaseManager.getConnection()) {
//            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
//                for (var i = 0; i < params.length; i++) {
//                    var param = params[i];
//                    //takes a statement and performs it. (ps = preparedStatement),
//                    ps.setString(i + 1, param); //fills in the question mark of the statement with the current param
//                }
//                ps.executeUpdate();
//
//                var rs = ps.getGeneratedKeys();
//                if (rs.next()) {
//                    rs.getInt(1);
//                }
//            }
//        } catch (SQLException e) {
//            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
//        }
//    }
//
//    private final String[] createStatements = {
//            """
//            CREATE TABLE IF NOT EXISTS auth (
//            authToken varchar(255) NOT NULL,
//            username varchar(255) NOT NULL,
//            PRIMARY KEY (authToken)
//            );
//            """
//    };
//
//    private void configureDatabase() throws DataAccessException {
//        DatabaseManager.createDatabase();
//        try (var conn = DatabaseManager.getConnection()) { //try to get a connection wtih the database
//            for (var statement : createStatements) {
//                try (var preparedStatement = conn.prepareStatement(statement)) {
//                    preparedStatement.executeUpdate(); //<- SQL executeUpdate function
//                }
//            }
//        } catch (SQLException ex) {
//            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
//        }
//    }

}
