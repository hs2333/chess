package server;

import com.google.gson.Gson;
import dataaccess.UserDAO;
import spark.Request;
import spark.Response;
import spark.Route;
import service.ClearService;
import dataaccess.*;

import java.util.Map;

public class ClearHandler implements Route {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ClearHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = DataAccessFactory.getUserDAO();
        this.authDAO = DataAccessFactory.getAuthDAO();
        this.gameDAO = DataAccessFactory.getGameDAO();
    }

    @Override
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        try {
            var service = new ClearService(userDAO, authDAO, gameDAO);
            service.clear();
            res.status(200);
            return serializer.toJson(Map.of("message:", "Error clearing database"));
        } catch (DataAccessException exception) {
            return ServerHelp.handleDataAccessException(exception, res, serializer);
        }
    }
}

