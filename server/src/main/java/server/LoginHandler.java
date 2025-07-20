package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.UserService;
import model.LoginRequest;
import dataaccess.DataAccessException;

import java.util.Map;

public class LoginHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        try {
            var loginRequest = serializer.fromJson(req.body(), LoginRequest.class);
            var service = new UserService();
            var result = service.login(loginRequest);
            res.status(200);
            return serializer.toJson(result);
        } catch (DataAccessException exception) {
            res.status(401);
            return serializer.toJson(Map.of("message", exception.getMessage()));
        }
    }
}
