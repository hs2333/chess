package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn implements MoveCalculator {

    @Override
    public Collection<ChessMove> Move(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        ChessPiece thisPawn = board.getPiece(position);
        ChessGame.TeamColor color = thisPawn.getTeamColor();

        int row = position.getRow();
        int col = position.getColumn();
        int direction = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (color == ChessGame.TeamColor.WHITE) ? 2 : 7;

        // Forward one square
        ChessPosition oneStep = new ChessPosition(row + direction, col);
        if (inBounds(oneStep) && board.getPiece(oneStep) == null) {
            possibleMoves.add(new ChessMove(position, oneStep, null));

            // Forward two squares from starting row
            ChessPosition twoStep = new ChessPosition(row + 2 * direction, col);
            if (row == startRow && board.getPiece(twoStep) == null) {
                possibleMoves.add(new ChessMove(position, twoStep, null));
            }
        }

        // Capture diagonally
        int[] dCol = {-1, 1};
        for (int dc : dCol) {
            int newCol = col + dc;
            int newRow = row + direction;
            ChessPosition diagPos = new ChessPosition(newRow, newCol);
            if (inBounds(diagPos)) {
                ChessPiece target = board.getPiece(diagPos);
                if (target != null && target.getTeamColor() != color) {
                    possibleMoves.add(new ChessMove(position, diagPos, null));
                }
            }
        }

        return possibleMoves;
    }

    private boolean inBounds(ChessPosition pos) {
        int r = pos.getRow();
        int c = pos.getColumn();
        return r >= 1 && r <= 8 && c >= 1 && c <= 8;
    }
    private void addPromotions(Collection<ChessMove> moves, ChessPosition from, ChessPosition to) {
        List<ChessPiece.PieceType> promotions = List.of(
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT
        );
        for (ChessPiece.PieceType type : promotions) {
            moves.add(new ChessMove(from, to, type));
        }
    }
}
