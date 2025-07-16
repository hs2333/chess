package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.JoinGameRequest;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class JoinGameHandler implements Route {
    private final MemoryGameDAO gameDAO;
    private final MemoryAuthDAO authDAO;

    public JoinGameHandler(MemoryGameDAO gameDAO, MemoryAuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        var serializer = new Gson();
        try {
            String token = req.headers("Authorization");
            var body = serializer.fromJson(req.body(), JoinGameRequest.class);
            var service = new GameService(gameDAO, authDAO);
            service.joinGame(token, body);
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            if (e.getMessage().contains("unauthorized"))
            {res.status(401);}
            else if (e.getMessage().contains("taken"))
            {res.status(403);}
            else
            {res.status(400);}
            return serializer.toJson(Map.of("message", e.getMessage()));
        }
    }
}
