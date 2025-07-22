package server;

import com.google.gson.Gson;
import dataaccess.*;
import spark.Request;
import spark.Response;
import spark.Route;
import service.UserService;
import model.RegisterRequest;

import java.sql.SQLException;
import java.util.Map;

public class RegisterHandler implements Route {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.authDAO = DataAccessFactory.getAuthDAO();
        this.userDAO = DataAccessFactory.getUserDAO();
    }

    @Override
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        try {
            var registerRequest = serializer.fromJson(req.body(), RegisterRequest.class);
            var service = new UserService(userDAO, authDAO);
            var result = service.register(registerRequest);
            res.status(200);
            return serializer.toJson(result);
        } catch (DataAccessException exception) {
            if (exception.getCause() instanceof SQLException) {
                res.status(500);
            } else if (exception.getMessage().contains("taken")) {
                res.status(403);
            } else {
                res.status(400);
            }
            String errorMessage = exception.getMessage() != null ? exception.getMessage() : "An unknown error occurred.";
            return serializer.toJson(Map.of("message", "Error: " + errorMessage));
        }
    }
}
