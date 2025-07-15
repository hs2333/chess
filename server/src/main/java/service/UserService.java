package service;

import dataaccess.*;
import model.*;

import java.util.UUID;

public class UserService {
    private final MemoryUserDAO userDAO;
    private final MemoryAuthDAO authDAO;

    public UserService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException {
        if (req.username() == null || req.password() == null || req.email() == null) {
            throw new DataAccessException("Error: bad request");
        }

        if (userDAO.getUser(req.username()) != null) {
            throw new DataAccessException("Error: already taken");
        }

        var user = new UserData(req.username(), req.password(), req.email());
        userDAO.insertUser(user);

        String token = UUID.randomUUID().toString();
        authDAO.insertAuth(new AuthData(token, req.username()));

        return new RegisterResult(req.username(), token);
    }
}
