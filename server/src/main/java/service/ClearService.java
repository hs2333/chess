package service;

import dataaccess.*;

public class ClearService {
    private final MemoryUserDAO userDAO;
    private final MemoryAuthDAO authDAO;
    private final MemoryGameDAO gameDAO;

    public ClearService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO, MemoryGameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

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
