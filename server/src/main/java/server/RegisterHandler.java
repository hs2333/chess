package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.UserService;
import model.RegisterRequest;
import dataaccess.DataAccessException;

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
            res.status(exception.getMessage().contains("taken") ? 403 : 400);
            return serializer.toJson(Map.of("message", exception.getMessage()));
        }
    }
}
