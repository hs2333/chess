package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Pawn implements MoveCalculator{
    @Override
    public Collection<ChessMove> Move(ChessBoard board, ChessPosition position) {
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
        //1 step forward
        int newRow = row + direction;
        if (newRow >=1 && newRow <=8) {
            ChessPosition newPosition = new ChessPosition(newRow, col);
            ChessPiece newPiece = board.getPiece(newPosition);
            if (newPiece == null) {
                //promotion
                if (newRow == promotionRow) {
                    possibleMove.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                    possibleMove.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                    possibleMove.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
                    possibleMove.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                }
                else {possibleMove.add(new ChessMove(position, newPosition, null));}
        }}

        //2 steps forward
        int newRow2 = row +direction +direction;
        if ((row==startRow) && (newRow2 >=1 && newRow2 <=8)) {
            ChessPosition newPosition = new ChessPosition(newRow, col);
            ChessPosition newPosition2 = new ChessPosition(newRow2, col);
            ChessPiece newPiece = board.getPiece(newPosition);
            ChessPiece newPiece2 = board.getPiece(newPosition2);
            if ((newPiece == null) && (newPiece2 == null)) {
                possibleMove.add(new ChessMove(position, newPosition2, null));
            }
        }

        //diag
        int newCol1 = col -1;
        int newCol2 = col +1;
        if ((newRow >=1 && newRow <=8) && (newCol1 >=1 && newCol1 <=8)) {
            ChessPosition newPosition = new ChessPosition(newRow, newCol1);
            ChessPiece newPiece = board.getPiece(newPosition);
            if ((newPiece != null) && (newPiece.getTeamColor() != oldPiece.getTeamColor())) {
                //promotion
                if (newRow == promotionRow) {
                    possibleMove.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                    possibleMove.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                    possibleMove.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
                    possibleMove.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                }
                else {possibleMove.add(new ChessMove(position, newPosition, null));}
            }
        }

        if ((newRow >=1 && newRow <=8) && (newCol2 >=1 && newCol2 <=8)) {
            ChessPosition newPosition = new ChessPosition(newRow, newCol2);
            ChessPiece newPiece = board.getPiece(newPosition);
            if ((newPiece != null) && (newPiece.getTeamColor() != oldPiece.getTeamColor())) {
                //promotion
                if (newRow == promotionRow) {
                    possibleMove.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
                    possibleMove.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
                    possibleMove.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
                    possibleMove.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
                }
                else {possibleMove.add(new ChessMove(position, newPosition, null));}
            }
        }

                return possibleMove;
    }
}
