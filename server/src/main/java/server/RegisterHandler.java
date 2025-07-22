package server;

import com.google.gson.Gson;
import dataaccess.*;
import spark.Request;
import spark.Response;
import spark.Route;
import service.UserService;
import model.RegisterRequest;

import java.sql.SQLException;
import java.util.Map;

public class RegisterHandler implements Route {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterHandler(UserDAO userDAO, AuthDAO authDAO) {
        this.authDAO = DataAccessFactory.getAuthDAO();
        this.userDAO = DataAccessFactory.getUserDAO();
    }

    @Override
    public Object handle(Request req, Response res) {
        var serializer = new Gson();
        try {
            var registerRequest = serializer.fromJson(req.body(), RegisterRequest.class);
            var service = new UserService(userDAO, authDAO);
            var result = service.register(registerRequest);
            res.status(200);
            return serializer.toJson(result);
        } catch (DataAccessException exception) {
            return ServerHelp.handleDataAccessException(exception, res, serializer);
        }
    }
}
