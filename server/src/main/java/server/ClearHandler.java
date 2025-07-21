package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.ClearService;
import dataaccess.DataAccessException;

import java.util.Map;

public class ClearHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        try {
            var service = new ClearService();
            service.clear();
            res.status(200);
            return serializer.toJson(Map.of("message:", "Error clearing database"));
        } catch (DataAccessException exception) {
            res.status(500);
            return serializer.toJson(Map.of("message", exception.getMessage() != null ? exception.getMessage() : "Server error"));
        }
    }
}

