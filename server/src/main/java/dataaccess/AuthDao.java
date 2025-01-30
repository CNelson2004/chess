package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

//Do Dao's, and write tests for them, and make htem work, then tackle one endpoint at a time in the service section
public interface AuthDao {
    void clear();
    AuthData createAuth(UserData user);
    AuthData getAuth(AuthData auth);
    void deleteAuth(AuthData auth);
    Collection<AuthData> getAllAuths();
}

