package dataaccess;

import model.UserData;

//UserDAO interface
public interface UserDAO {
    void insertUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    boolean validateUser(String username, String password) throws DataAccessException;
    void clear() throws DataAccessException;
}
