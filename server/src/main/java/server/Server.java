package server;

import spark.*;
import dataaccess.*;
import spark.Spark;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        try {
            DatabaseInitializer.initialize();
        } catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        } //I know I have a problem here. I might not need to throw an error. But let me fix it later.

        // Register your endpoints and handle exceptions here.
        //register endpoints through INTERFACE!!!!!!
        var userDAO = DataAccessFactory.getUserDAO();
        var authDAO = DataAccessFactory.getAuthDAO();
        var gameDAO = DataAccessFactory.getGameDAO();


        //add handlers
        Spark.post("/user", new RegisterHandler(userDAO, authDAO));
        Spark.post("/session", new LoginHandler(userDAO, authDAO));
        Spark.delete("/session", new LogoutHandler(userDAO, authDAO));
        Spark.delete("/db", new ClearHandler(userDAO, authDAO, gameDAO));
        Spark.post("/game", new CreateGameHandler(gameDAO, authDAO));
        Spark.get("/game", new ListGamesHandler(gameDAO, authDAO));
        Spark.put("/game", new JoinGameHandler(gameDAO, authDAO));
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
