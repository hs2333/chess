package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void insertAuth(AuthData token) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    boolean isValidToken(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;
}
