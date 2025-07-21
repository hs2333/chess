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
            return "{}";
        } catch (DataAccessException e) {
            if (e.getCause() instanceof SQLException) {
                res.status(500);
            } else if (e.getMessage().contains("unauthorized")) {
                res.status(401);
            } else {
                res.status(403);
            }
            return serializer.toJson(Map.of("message", e.getMessage()));
        }
    }
}
