package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Queen extends ChessPiece {

    public Queen(ChessGame.TeamColor color) {
        super(color, PieceType.QUEEN);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}, // straight
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // diagonal
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
                        moves.add(new ChessMove(myPosition, to, null));
                    }
                    break;
                }

                r += dir[0];
                c += dir[1];
            }
        }

        return moves;
    }
}
