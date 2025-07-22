package server;

import com.google.gson.Gson;
import dataaccess.*;
import spark.Request;
import spark.Response;
import spark.Route;
import service.GameService;
import model.JoinGameRequest;
import dataaccess.DataAccessException;

import java.sql.SQLException;
import java.util.Map;

public class JoinGameHandler implements Route {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public JoinGameHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.authDAO = DataAccessFactory.getAuthDAO();
        this.gameDAO = DataAccessFactory.getGameDAO();
    }

    @Override
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        try {
            String token = req.headers("Authorization");
            var joinRequest = serializer.fromJson(req.body(), JoinGameRequest.class);
            var service = new GameService(gameDAO, authDAO);
            service.joinGame(token, joinRequest);
            res.status(200);
            Map<String, String> responseBody = Map.of("message", "Logout successful");
            return serializer.toJson(responseBody);
        } catch (DataAccessException exception) {
            if (exception.getCause() instanceof SQLException) {
                res.status(500);
            } else if (exception.getMessage().contains("unauthorized")) {
                res.status(401);
            } else if (exception.getMessage().contains("taken")) {
                res.status(403);
            } else {
                res.status(400);}
            String errorMessage = exception.getMessage() != null ? exception.getMessage() : "An unknown error occurred.";
            return serializer.toJson(Map.of("message", "Error: " + errorMessage));
        }
    }
}
