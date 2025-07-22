package server;

import com.google.gson.Gson;
import dataaccess.DataAccessFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import service.UserService;
import model.*;
import dataaccess.*;

import java.sql.SQLException;
import java.util.Map;

public class LoginHandler implements Route {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.authDAO = DataAccessFactory.getAuthDAO();
        this.userDAO = DataAccessFactory.getUserDAO();
    }

    @Override
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        try {
            var loginRequest = serializer.fromJson(req.body(), LoginRequest.class);
            var service = new UserService(userDAO, authDAO);
            var result = service.login(loginRequest);
            res.status(200);
            return serializer.toJson(result);
        } catch (DataAccessException exception) {
            if (exception.getCause() instanceof SQLException) {
                res.status(500);
            } else if (exception.getMessage().toLowerCase().contains("unauthorized") ||
                    exception.getMessage().toLowerCase().contains("does not exist")) {
                res.status(401);}
            else
            {res.status(400);}
            String errorMessage = exception.getMessage() != null ? exception.getMessage() : "Error occurred.";
            return serializer.toJson(Map.of("message", "Error: " + errorMessage));
        }
    }
}
