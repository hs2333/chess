package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class ClearHandler implements Route {
    private final UserDAO userDAO;
    private final AuthTokenDAO authDAO;
    private final GameDAO gameDAO;


    public ClearHandler(UserDAO userDAO, AuthTokenDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public Object handle(Request req, Response res) {
        var service = new ClearService(userDAO, authDAO, gameDAO);
        try {
            service.clear();
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }
}
