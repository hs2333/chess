package service;

import dataaccess.*;

public class ClearService {
    private final UserDAO userDAO = DataAccessFactory.getUserDAO();
    private final AuthDAO authDAO = DataAccessFactory.getAuthDAO();
    private final GameDAO gameDAO = DataAccessFactory.getGameDAO();

    //just clear
    public void clear() throws DataAccessException {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
        } catch (Exception e) {
            throw new DataAccessException("Error: failed to clear database", e);
        }
    }
}
