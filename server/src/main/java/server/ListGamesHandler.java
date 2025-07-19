package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;
import java.util.Map;

public class ListGamesHandler implements Route {
    private final MemoryGameDAO gameDAO;
    private final MemoryAuthDAO authDAO;
    public ListGamesHandler(GameDAO gameDAO, AuthTokenDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        var serializer = new Gson();
        try {
            String token = req.headers("Authorization");
            var service = new GameService(gameDAO, authDAO);
            var result = service.listGames(token);
            res.status(200);
            return serializer.toJson(result);
        } catch (DataAccessException e) {
            res.status(401);
            return serializer.toJson(Map.of("message", e.getMessage()));
        }
    }
}
