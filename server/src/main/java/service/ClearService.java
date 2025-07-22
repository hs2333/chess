package service;

import dataaccess.*;

public class ClearService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    //ClearService
    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = DataAccessFactory.getUserDAO();
        this.authDAO = DataAccessFactory.getAuthDAO();
        this.gameDAO = DataAccessFactory.getGameDAO();
    }

    //just clear
    public void clear() throws DataAccessException {
        try {
            if (DataAccessFactory.usingSQL()) {
                userDAO.clear();
                authDAO.clear();
                gameDAO.clear();
            } else {
                DataAccessFactory.resetMemoryDAOs();
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: failed to clear database", e);
        }
    }
}

