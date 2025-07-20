package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.UserService;
import dataaccess.DataAccessException;

import java.util.Map;

public class LogoutHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        try {
            String token = req.headers("Authorization");
            var service = new UserService();
            service.logout(token);
            res.status(200);
            return "{}";
        } catch (DataAccessException exception) {
            res.status(401);
            return serializer.toJson(Map.of("message", exception.getMessage()));
        }
    }
}
