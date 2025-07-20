package server;

import spark.*;
import dataaccess.*;
import spark.Spark;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //register endpoints
        var userDAO = new MemoryUserDAO();
        var authDAO = new MemoryAuthDAO();
        var gameDAO = new MemoryGameDAO();

        //add handlers
        Spark.post("/user", new RegisterHandler());
        Spark.post("/session", new LoginHandler());
        Spark.delete("/session", new LogoutHandler());
        Spark.delete("/db", new ClearHandler());
        Spark.post("/game", new CreateGameHandler());
        Spark.get("/game", new ListGamesHandler());
        Spark.put("/game", new JoinGameHandler());

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
