package service;

import dataaccess.*;
import model.*;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        //user data
        this.userDAO = DataAccessFactory.getUserDAO();
        //authentication token
        this.authDAO = DataAccessFactory.getAuthDAO();
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException {
        //check missing fields
        if (req.username() == null || req.password() == null || req.email() == null) {
            throw new DataAccessException("Error: bad request");
        } if (userDAO.getUser(req.username()) != null) {
            throw new DataAccessException("Error: already taken");
        }

        String hashed = BCrypt.hashpw(req.password(), BCrypt.gensalt());
        var user = new UserData(req.username(), hashed, req.email());
        userDAO.insertUser(user);

        String token = UUID.randomUUID().toString();
        authDAO.insertAuth(new AuthData(token, req.username()));

        return new RegisterResult(req.username(), token);
    }

    public LoginResult login(LoginRequest req) throws DataAccessException {
        if (req.username() == null || req.password() == null) {
            throw new DataAccessException("Error: bad request");
        }

        boolean valid = userDAO.validateUser(req.username(), req.password());
        if (!valid) {
            throw new DataAccessException("Error: unauthorized");
        }
        //new auth token
        String token = UUID.randomUUID().toString();
        authDAO.insertAuth(new AuthData(token, req.username()));
        return new LoginResult(req.username(), token);
    }

    public void logout(String token) throws DataAccessException {
        if (token == null || !authDAO.isValidToken(token)) {
            throw new DataAccessException("Error: unauthorized");
        }
        authDAO.deleteAuth(token);
    }
}
