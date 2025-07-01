package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Knight extends ChessPiece {

    public Knight(ChessGame.TeamColor color) {
        super(color, PieceType.KNIGHT);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int[][] offsets = {
                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int[] offset : offsets) {
            int r = row + offset[0];
            int c = col + offset[1];
            if (r >= 1 && r <= 8 && c >= 1 && c <= 8) {
                ChessPosition to = new ChessPosition(r, c);
                ChessPiece target = board.getPiece(to);
                if (target == null || target.getTeamColor() != this.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, to, null));
                }
            }
        }

        return moves;
    }
}
