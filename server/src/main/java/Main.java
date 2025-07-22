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
        Server server = new Server();
        try {
            server.run(8080);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}