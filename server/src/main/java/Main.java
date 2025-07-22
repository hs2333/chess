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
        try {
            DatabaseInitializer.initialize();  // ← Ensure tables exist
        } catch (DataAccessException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            return;
        }

        Server server = new Server();
        server.run(8080);}
}