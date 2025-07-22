package server;

import com.google.gson.Gson;
import dataaccess.*;
import spark.Request;
import spark.Response;
import spark.Route;
import service.UserService;

import java.sql.SQLException;
import java.util.Map;

public class LogoutHandler implements Route {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LogoutHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.authDAO = DataAccessFactory.getAuthDAO();
        this.userDAO = DataAccessFactory.getUserDAO();
    }

    @Override
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        try {
            String token = req.headers("Authorization");
            var service = new UserService(userDAO, authDAO);
            service.logout(token);
            res.status(200);
            Map<String, String> responseBody = Map.of("message", "Logout successful");
            return serializer.toJson(responseBody);} catch (DataAccessException exception) {
            return ServerHelp.handleDataAccessException(exception, res, serializer);
        }
    }
}
