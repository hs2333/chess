package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.GameService;
import dataaccess.DataAccessException;

import java.sql.SQLException;
import java.util.Map;

public class ListGamesHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        try {
            String token = req.headers("Authorization");
            var service = new GameService();
            var result = service.listGames(token);
            res.status(200);
            return serializer.toJson(result);
        } catch (DataAccessException exception) {
            if (exception.getCause() instanceof SQLException) {
                res.status(500);
            } else {
                res.status(401);
            }
            String errorMessage = exception.getMessage() != null ? exception.getMessage() : "An unknown error occurred.";
            return serializer.toJson(Map.of("message", "Error: " + errorMessage));
        }
    }
}
