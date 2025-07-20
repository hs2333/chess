package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    boolean validateUser(String username, String password) throws DataAccessException;
    void clear() throws DataAccessException;
}
