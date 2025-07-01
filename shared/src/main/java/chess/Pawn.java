package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends ChessPiece {

    public Pawn(ChessGame.TeamColor color) {
        super(color, PieceType.PAWN);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int direction = (getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int finalRow = (getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;

        // Single forward move
        ChessPosition oneForward = new ChessPosition(row + direction, col);
        if (isInBounds(oneForward) && board.getPiece(oneForward) == null) {
            addPawnMove(moves, myPosition, oneForward, finalRow);
            // Double forward move
            if (row == startRow) {
                ChessPosition twoForward = new ChessPosition(row + 2 * direction, col);
                if (board.getPiece(twoForward) == null) {
                    moves.add(new ChessMove(myPosition, twoForward, null));
                }
            }
        }

        // Diagonal captures
        for (int deltaCol : new int[]{-1, 1}) {
            ChessPosition diagonal = new ChessPosition(row + direction, col + deltaCol);
            if (isInBounds(diagonal)) {
                ChessPiece target = board.getPiece(diagonal);
                if (target != null && target.getTeamColor() != this.getTeamColor()) {
                    addPawnMove(moves, myPosition, diagonal, finalRow);
                }
            }
        }

        return moves;
    }

    private void addPawnMove(List<ChessMove> moves, ChessPosition from, ChessPosition to, int promotionRow) {
        if (to.getRow() == promotionRow) {
            // Add all promotion options
            moves.add(new ChessMove(from, to, PieceType.QUEEN));
            moves.add(new ChessMove(from, to, PieceType.ROOK));
            moves.add(new ChessMove(from, to, PieceType.BISHOP));
            moves.add(new ChessMove(from, to, PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(from, to, null));
        }
    }

    private boolean isInBounds(ChessPosition pos) {
        int r = pos.getRow();
        int c = pos.getColumn();
        return r >= 1 && r <= 8 && c >= 1 && c <= 8;
    }
}
