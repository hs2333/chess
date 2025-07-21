package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.UserService;
import model.RegisterRequest;
import dataaccess.DataAccessException;

import java.sql.SQLException;
import java.util.Map;

public class RegisterHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        try {
            var registerRequest = serializer.fromJson(req.body(), RegisterRequest.class);
            var service = new UserService();
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
            return serializer.toJson(Map.of("message", exception.getMessage() != null ? exception.getMessage() : "Server error"));
        }
    }
}
