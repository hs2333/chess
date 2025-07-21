package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import service.GameService;
import model.CreateGameRequest;
import dataaccess.DataAccessException;

import java.sql.SQLException;
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
        } catch (DataAccessException exception) {
            if (exception.getCause() instanceof SQLException) {
                res.status(500);
            } else if (exception.getMessage().contains("unauthorized")) {
                res.status(401);
            } else {
                res.status(400);
            }
            return serializer.toJson(Map.of("Error message: ", (exception.getMessage() != null) ? exception.getMessage() : "Server error"));
        }
    }
}
