package service;

import dataaccess.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    private final MemoryUserDAO userDAO;
    private final MemoryAuthDAO authDAO;

    public UserService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
        //user data
        this.userDAO = userDAO;
        //authentication token
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException {
        //check missing fields
        if (req.username() == null || req.password() == null || req.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        //check username
        if (userDAO.getUser(req.username()) != null) {
            throw new DataAccessException("Error: already taken");
        }

        //Hash and BCrypt
        String hashedPassword = BCrypt.hashpw(req.password(), BCrypt.gensalt());

        //new user
        var user = new UserData(req.username(), req.password(), req.email());
        userDAO.insertUser(user);
        //new auth token
        String token = UUID.randomUUID().toString();
        authDAO.insertAuth(new AuthData(token, req.username()));



        return new RegisterResult(req.username(), token);
    }

    public LoginResult login(LoginRequest req) throws DataAccessException {
        //check missing fields
        if (req.username() == null || req.password() == null) {
            throw new DataAccessException("Error: bad request");
        }

        //check credential
        var user = userDAO.getUser(req.username());
        if (user == null || !BCrypt.checkpw(req.password(), user.password())) {
            throw new DataAccessException("Error: unauthorized");
        }

        //new auth token
        String token = UUID.randomUUID().toString();
        authDAO.insertAuth(new AuthData(token, user.username()));


        return new LoginResult(user.username(), token);
    }

    public void logout(String token) throws DataAccessException {
        //check token valid
        //delete and invalidate the token
        if (token == null || !authDAO.isValidToken(token)) {
            throw new DataAccessException("Error: unauthorized");
        }
        authDAO.deleteAuth(token);
    }
}
