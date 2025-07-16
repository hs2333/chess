package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class RegisterHandler implements Route {
    private final MemoryUserDAO userDAO;
    private final MemoryAuthDAO authDAO;
    public RegisterHandler(MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
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
            res.status(exception.getMessage().contains("taken") ? 403 : 400);
            return serializer.toJson(Map.of("message", exception.getMessage()));
        }
    }
}

