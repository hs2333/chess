package service;

import dataaccess.*;

public class ClearService {
    private final UserDAO  userDAO;
    private final AuthTokenDAO  authDAO;
    private final GameDAO  gameDAO;

    //ClearService
    public ClearService(UserDAO userDAO, AuthTokenDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    //clear
    public void clear() throws DataAccessException {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
        } catch (Exception e) {
            throw new DataAccessException("Error: failed to clear database");
        }
    }
}
