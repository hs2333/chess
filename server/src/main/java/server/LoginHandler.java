package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;


    public LoginHandler(UserDAO userDAO, AuthTokenDAO authDAO) {
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
            return ServerHelp.handleError(res, exception);
        }
    }
}
