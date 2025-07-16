package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import java.util.Map;

public class LoginHandler implements Route {
    private final MemoryUserDAO userDAO;
    private final MemoryAuthDAO authDAO;

    public LoginHandler(MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        var serializer = new Gson();
        try {
            var loginRequest = serializer.fromJson(req.body(), LoginRequest.class);
            var service = new UserService(userDAO, authDAO);
            var result = service.login(loginRequest);
            res.status(200);
            return serializer.toJson(result);
        } catch (DataAccessException exception) {
            return serverHelp.handleError(res, exception);
        }
    }
}
