package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Pawn implements MoveCalculator{
    @Override
    public Collection<ChessMove> moveMove(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMove = new ArrayList<>();

        //chess row and column
        int row = position.getRow();
        int col = position.getColumn();
        ChessPiece oldPiece = board.getPiece(position);

        //check team
        boolean isTeamWhite = (oldPiece.getTeamColor() == ChessGame.TeamColor.WHITE);
        //chess start row
        int startRow = isTeamWhite? 2: 7;
        //chess promotion Row
        int promotionRow = isTeamWhite? 8: 1;
        //chess forward direction
        int direction = isTeamWhite? 1: -1;


        //loop over possible moves
        // 1 step forward
        int newRow = row + direction;
        if (newRow >= 1 && newRow <= 8) {
            ChessPosition forwardPos = new ChessPosition(newRow, col);
            ChessPiece forwardPiece = board.getPiece(forwardPos);
            if (forwardPiece == null) {
                MoveHelp.addMoveOrPromotions(possibleMove, position, forwardPos, promotionRow, newRow);
            }
        }

        // 2 steps forward
        int newRow2 = row + 2 * direction;
        if (row == startRow && newRow2 >= 1 && newRow2 <= 8) {
            ChessPosition midPos = new ChessPosition(row + direction, col);
            ChessPosition targetPos = new ChessPosition(newRow2, col);
            if (board.getPiece(midPos) == null && board.getPiece(targetPos) == null) {
                possibleMove.add(new ChessMove(position, targetPos, null));
            }
        }

        // diag left
        int newCol1 = col - 1;
        if (newCol1 >= 1) {
            ChessPosition diagLeft = new ChessPosition(newRow, newCol1);
            ChessPiece diagPiece = board.getPiece(diagLeft);
            if (diagPiece != null && diagPiece.getTeamColor() != oldPiece.getTeamColor()) {
                MoveHelp.addMoveOrPromotions(possibleMove, position, diagLeft, promotionRow, newRow);
            }
        }

        // diag right
        int newCol2 = col + 1;
        if (newCol2 <= 8) {
            ChessPosition diagRight = new ChessPosition(newRow, newCol2);
            ChessPiece diagPiece = board.getPiece(diagRight);
            if (diagPiece != null && diagPiece.getTeamColor() != oldPiece.getTeamColor()) {
                MoveHelp.addMoveOrPromotions(possibleMove, position, diagRight, promotionRow, newRow);
            }
        }

        return possibleMove;
    }
}
