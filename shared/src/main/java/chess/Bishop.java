package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Bishop extends ChessPiece {
    public Bishop(ChessGame.TeamColor color) {
        super(color, PieceType.BISHOP);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int[][] directions = {
                {1, 1},   // up-right
                {1, -1},  // up-left
                {-1, 1},  // down-right
                {-1, -1}  // down-left
        };

        for (int[] dir : directions) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true) {
                row += dir[0];
                col += dir[1];

                if (row < 1 || row > 8 || col < 1 || col > 8) break;

                ChessPosition newPos = new ChessPosition(row, col);
                ChessPiece occupying = board.getPiece(newPos);

                if (occupying == null) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                } else {
                    if (occupying.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    }
                    break; // Can't jump over any piece
                }
            }
        }

        return moves;
    }
}