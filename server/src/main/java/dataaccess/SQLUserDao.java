package dataaccess;

import model.UserData;

import java.util.Collection;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.sql.*;

public class SQLUserDao implements UserDao{
    public SQLUserDao() throws DataAccessException {
        configureDatabase();
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

    public UserData createUser(String username, String password, String email) throws DataAccessException {
        var statement = "INSERT INTO user (username, email, password) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        executeUpdate(statement, username, email, hashedPassword);
        return new UserData(username,email,hashedPassword); //<- Doesn't matter which password we return
    }

    public boolean checkPassword(UserData user, String password){
        return BCrypt.checkpw(password, user.password());
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        //Must read the hashed password and return the clear one?
        String username = rs.getString("username");
        String email = rs.getString("email");
        String hashedPassword = rs.getString("password");
        return new UserData(username,email,hashedPassword);
    }

    public UserData getUser(String username) throws DataAccessException{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username,email,password FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1,username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }else{
                        throw new DataAccessException("Invalid username");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    public Collection<UserData> getAllUsers() throws DataAccessException{
        var result = new ArrayList<UserData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username,email,password FROM user";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readUser(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    private void executeUpdate(String statement, String... params) throws DataAccessException {
        SQLAuthDao.executeUpdate(statement, params);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user (
            username varchar(255) NOT NULL,
            email varchar(255) NOT NULL,
            password varchar(255) NOT NULL,
            id INTEGER NOT NULL AUTO_INCREMENT,
            PRIMARY KEY (id)
            );
            """
    }; //The id may not be needed here due to never being used.

    private void configureDatabase() throws DataAccessException {
        SQLAuthDao.configureDatabase(createStatements);
    }

}
