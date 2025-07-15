package server;

import dataaccess.*;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

import com.google.gson.Gson;

public class LogoutHandler implements Route {
    private final MemoryUserDAO userDAO;
    private final MemoryAuthDAO authDAO;

    public LogoutHandler(MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    @Override
    public Object handle(Request req, Response res) {
        var token = req.headers("Authorization");
        var service = new UserService(userDAO, authDAO);
        try {
            service.logout(token);
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            res.status(401);
            return new Gson().toJson(Map.of("message", e.getMessage()));
        }
    }
}
