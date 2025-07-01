package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King extends ChessPiece {

    public King(ChessGame.TeamColor color) {
        super(color, PieceType.KING);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int r = row + dr;
                int c = col + dc;

                if (r >= 1 && r <= 8 && c >= 1 && c <= 8) {
                    ChessPosition to = new ChessPosition(r, c);
                    ChessPiece target = board.getPiece(to);
                    if (target == null || target.getTeamColor() != this.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, to, null));
                    }
                }
            }
        }

        return moves;
    }
}
