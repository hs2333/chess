package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    private final MemoryGameDAO gameDAO;
    private final MemoryAuthDAO authDAO;

    public CreateGameHandler(MemoryGameDAO gameDAO, MemoryAuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        var serializer = new Gson();
        try {
            String token = req.headers("Authorization");
            var body = serializer.fromJson(req.body(), CreateGameRequest.class);
            var service = new GameService(gameDAO, authDAO);
            var result = service.createGame(token, body);
            res.status(200);
            return serializer.toJson(result);
        } catch (DataAccessException exception) {
            return ServerHelp.handleError(res, exception);
        }
    }
}
