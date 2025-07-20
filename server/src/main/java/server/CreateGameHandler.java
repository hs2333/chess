package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.GameService;
import model.CreateGameRequest;
import dataaccess.DataAccessException;

import java.util.Map;

public class CreateGameHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        try {
            String token = req.headers("Authorization");
            var gameRequest = serializer.fromJson(req.body(), CreateGameRequest.class);
            var service = new GameService();
            var result = service.createGame(token, gameRequest);
            res.status(200);
            return serializer.toJson(result);
        } catch (DataAccessException e) {
            res.status(e.getMessage().contains("unauthorized") ? 401 : 400);
            return serializer.toJson(Map.of("message", e.getMessage()));
        }
    }
}
