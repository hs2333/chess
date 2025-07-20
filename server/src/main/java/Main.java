import chess.*;
import dataaccess.DataAccessException;
import dataaccess.DatabaseInitializer;
import server.*;
import spark.Spark;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        //connnect to server
        //Server server = new Server();
        //server.run(8080);     }
        try {
            DatabaseInitializer.initialize();  // ← Ensure tables exist
        } catch (DataAccessException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            return;
        }

        Spark.port(8080);
        Spark.staticFiles.location("/web");

        Spark.delete("/db", new ClearHandler());
        Spark.post("/user", new RegisterHandler());
        Spark.post("/session", new LoginHandler());
        Spark.delete("/session", new LogoutHandler());
        Spark.post("/game", new CreateGameHandler());
        Spark.get("/game", new ListGamesHandler());
        Spark.put("/game", new JoinGameHandler());
    }
}