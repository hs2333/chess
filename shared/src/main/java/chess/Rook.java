package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Rook extends ChessPiece {

    public Rook(ChessGame.TeamColor color) {
        super(color, PieceType.ROOK);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Rook moves in 4 directions: ↑ ↓ ← →
        int[][] directions = {
                {-1, 0}, // up
                {1, 0},  // down
                {0, -1}, // left
                {0, 1}   // right
        };

        for (int[] dir : directions) {
            int r = row + dir[0];
            int c = col + dir[1];

            while (r >= 1 && r <= 8 && c >= 1 && c <= 8) {
                ChessPosition to = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(to);

                if (piece == null) {
                    moves.add(new ChessMove(myPosition, to, null));
                } else {
                    if (piece.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, to, null)); // capture
                    }
                    break; // blocked by any piece
                }

                r += dir[0];
                c += dir[1];
            }
        }

        return moves;
    }
}
