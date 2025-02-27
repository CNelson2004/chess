package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;

import java.sql.*;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLAuthDao implements AuthDao {
    public SQLAuthDao() throws DataAccessException {
        configureDatabase();
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    public AuthData createAuth(UserData user) throws DataAccessException {
        var statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
        String authToken = UUID.randomUUID().toString();
        executeUpdate(statement, user.name(), authToken);
        return new AuthData(user.name(), authToken);
    }

    public Collection<AuthData> getAllAuths() throws DataAccessException {
        var result = new ArrayList<AuthData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username,authToken FROM auth";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readAuth(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    //Helper function to getAuth which reads the data from the datatable
    private AuthData readAuth(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var authToken = rs.getString("authToken");
        return new AuthData(username,authToken);
    }

    public AuthData getAuth(String authToken) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, authToken FROM auth WHERE authToken=?"; //Query statement
            try (var ps = conn.prepareStatement(statement)) { //Prepares the statement
                ps.setString(1,authToken);  //Sets the questionmark (index of ? starting at 1, value to set ? to)
                try (var rs = ps.executeQuery()) {   //execute updates changes database without returning, query just returns
                    if (rs.next()) {
                        return readAuth(rs);
                    }else{
                        throw new DataAccessException("Invalid Authorization token");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        //return null;
    }

    public void deleteAuth(AuthData auth) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, auth.authToken());
    }

    //Overrides the method in SQL                {below} changed object to String since AuthData only has strings
    static void executeUpdate(String statement, String... params) throws DataAccessException { //... could be switched for []
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    //takes a statement and performs it. (ps = preparedStatement),
                    ps.setString(i + 1, param); //fills in the question mark of the statement with the current param
                }
                ps.executeUpdate();
                //Generated key stuff
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    //*may not need id, primary key just needs to be unique
    //'id' int NOT NULL AUTO_INCREMENT
    //'json' TEXT DEFAULT NULL, only used when you have more information
    //such as with game ChessGame in GameData
    //May not be needed: ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
            authToken varchar(255) NOT NULL,
            username varchar(255) NOT NULL,
            PRIMARY KEY (authToken)
            );
            """
    };

    //If the database already does exist, then will this just add the table to it?
    private void configureDatabase() throws DataAccessException {
        configureDatabase(createStatements);
    }

    static void configureDatabase(String[] createStatements) throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
