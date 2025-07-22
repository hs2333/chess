package server;

import com.google.gson.Gson;
import dataaccess.*;
import spark.Request;
import spark.Response;
import spark.Route;
import service.GameService;
import model.CreateGameRequest;

import java.sql.SQLException;
import java.util.Map;

public class CreateGameHandler implements Route {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public CreateGameHandler (GameDAO gameDAO, AuthDAO authDAO) {

        this.authDAO = DataAccessFactory.getAuthDAO();
        this.gameDAO = DataAccessFactory.getGameDAO();
    }

    @Override
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        try {
            String token = req.headers("Authorization");
            var gameRequest = serializer.fromJson(req.body(), CreateGameRequest.class);
            var service = new GameService(gameDAO, authDAO);
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
            String errorMessage = exception.getMessage() != null ? exception.getMessage() : "An unknown error occurred.";
            return serializer.toJson(Map.of("message", "Error: " + errorMessage));
        }
    }
}
