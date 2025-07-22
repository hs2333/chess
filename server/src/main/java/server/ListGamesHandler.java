package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessFactory;
import dataaccess.GameDAO;
import spark.Request;
import spark.Response;
import spark.Route;
import service.GameService;
import dataaccess.DataAccessException;

import java.sql.SQLException;
import java.util.Map;

public class ListGamesHandler implements Route {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ListGamesHandler(GameDAO gameDAO, AuthDAO authDAO) {
        this.authDAO = DataAccessFactory.getAuthDAO();
        this.gameDAO = DataAccessFactory.getGameDAO();
    }

    @Override
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        try {
            String token = req.headers("Authorization");
            var service = new GameService(gameDAO, authDAO);
            var result = service.listGames(token);
            res.status(200);
            return serializer.toJson(result);
        } catch (DataAccessException exception) {
            return ServerHelp.handleDataAccessException(exception, res, serializer);
        }
    }
}
