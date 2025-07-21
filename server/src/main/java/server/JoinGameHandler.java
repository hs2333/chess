package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.GameService;
import model.JoinGameRequest;
import dataaccess.DataAccessException;

import java.sql.SQLException;
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
            Map<String, String> responseBody = Map.of("message", "Error join game");
            return serializer.toJson(responseBody);
        } catch (DataAccessException exception) {
            if (exception.getCause() instanceof SQLException) {
                res.status(500);
            } else if (exception.getMessage().contains("unauthorized")) {
                res.status(401);
            } else {
                res.status(403);
            }
            return serializer.toJson(Map.of("message", exception.getMessage() != null ? exception.getMessage() : "Server error"));
        }
    }
}
