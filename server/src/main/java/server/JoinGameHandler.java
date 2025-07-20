package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.GameService;
import model.JoinGameRequest;
import dataaccess.DataAccessException;

import java.util.Map;

public class JoinGameHandler implements Route {
    @Override
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        try {
            String token = req.headers("Authorization");
            var joinRequest = serializer.fromJson(req.body(), JoinGameRequest.class);
            var service = new GameService();
            service.joinGame(token, joinRequest);
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            res.status(e.getMessage().contains("unauthorized") ? 401 : 403);
            return serializer.toJson(Map.of("message", e.getMessage()));
        }
    }
}
