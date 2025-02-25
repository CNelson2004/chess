package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.Collection;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthDao implements AuthDao {
    public void clear() {
    }

    public AuthData createAuth(UserData user) throws DataAccessException {
        //will serialize an AuthData object into a JSON string and put it into the sql database
        return null;
    }

    public AuthData getAuth(String authToken) {
        //Will deserialize a JSON string from the database andturn it into AuthData
        return null;
    }

    public void deleteAuth(AuthData auth) throws DataAccessException {
    }

    public Collection<AuthData> getAllAuths() {
        return null;
    }

    //Create statement here?

    private final String[] createStatements = {
            """
            
            """
    };

    //private void configureDatabase(){
    // create the databse using DatabaseManager.createDatabase()
    //then create the tables
    //the create statement will have to be changed to make sense with the chess project
    //}

    //Example code:
//    private void configureDatabase() throws ResponseException {
//        DatabaseManager.createDatabase();
//        try (var conn = DatabaseManager.getConnection()) {
//            for (var statement : createStatements) {
//                try (var preparedStatement = conn.prepareStatement(statement)) {
//                    preparedStatement.executeUpdate();
//                }
//            }
//        } catch (SQLException ex) {
//            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
//        }
//    }
}
