package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.GameService;
import dataaccess.DataAccessException;

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
        } catch (DataAccessException e) {
            res.status(401);
            return serializer.toJson(Map.of("message", e.getMessage()));
        }
    }
}
