package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn implements MoveCalculator {
    @Override
    public Collection<ChessMove> Move(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        ChessPiece oldPiece = board.getPiece(position);

        boolean isWhite = (oldPiece.getTeamColor() == ChessGame.TeamColor.WHITE);
        //up or down
        int direction = isWhite ? 1 : -1;
        //start row
        int startRow = (isWhite) ? 2 : 7;
        //promotion row
        int promotionRow = (isWhite) ? 8 : 1;


        //forward 1
        int newRow = row + direction;
        if (newRow >= 1 && newRow <= 8) {
            ChessPosition newPosition = new ChessPosition(newRow, col);
            ChessPiece newPiece1 = board.getPiece(newPosition);
            if  (newPiece1==null) {
                if (newRow == promotionRow) {
                    possibleMoves.add(new ChessMove(position,newPosition, ChessPiece.PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(position,newPosition, ChessPiece.PieceType.ROOK));
                    possibleMoves.add(new ChessMove(position,newPosition, ChessPiece.PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(position,newPosition, ChessPiece.PieceType.KNIGHT));}
               else {possibleMoves.add(new ChessMove(position,newPosition,null));}
            }
        }
        //forward 2
        int newRow1 = row + direction;
        int newRow2 = row + direction + direction;
        if (newRow2 >= 1 && newRow2 <= 8 && row == startRow) {
            ChessPosition newPosition1 = new ChessPosition(newRow1, col);
            ChessPosition newPosition2 = new ChessPosition(newRow2, col);
            ChessPiece newPiece1 = board.getPiece(newPosition1);
            ChessPiece newPiece2 = board.getPiece(newPosition2);
            if  (newPiece1==null && newPiece2==null) {
                possibleMoves.add(new ChessMove(position,newPosition2,null));
            }
        }

        //diagonal
        int newCol_l = col -1;
        int newCol_r = col +1;
        if (newRow >= 1 && newRow <= 8 && newCol_l >= 1 && newCol_l <= 8) {
            ChessPosition newPosition = new ChessPosition(newRow, newCol_l);
            ChessPiece newPiece = board.getPiece(newPosition);
            if  ((newPiece != null && newPiece.getTeamColor()!=oldPiece.getTeamColor())) {
                if (newRow == promotionRow) {
                    possibleMoves.add(new ChessMove(position,newPosition, ChessPiece.PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(position,newPosition, ChessPiece.PieceType.ROOK));
                    possibleMoves.add(new ChessMove(position,newPosition, ChessPiece.PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(position,newPosition, ChessPiece.PieceType.KNIGHT));}
                else {possibleMoves.add(new ChessMove(position,newPosition,null));}
            }
        }
        if (newRow >= 1 && newRow <= 8 && newCol_r >= 1 && newCol_r <= 8) {
            ChessPosition newPosition = new ChessPosition(newRow, newCol_r);
            ChessPiece newPiece = board.getPiece(newPosition);
            if  ((newPiece != null && newPiece.getTeamColor()!=oldPiece.getTeamColor())) {
                if (newRow == promotionRow) {
                    possibleMoves.add(new ChessMove(position,newPosition, ChessPiece.PieceType.QUEEN));
                    possibleMoves.add(new ChessMove(position,newPosition, ChessPiece.PieceType.ROOK));
                    possibleMoves.add(new ChessMove(position,newPosition, ChessPiece.PieceType.BISHOP));
                    possibleMoves.add(new ChessMove(position,newPosition, ChessPiece.PieceType.KNIGHT));}
                else {possibleMoves.add(new ChessMove(position,newPosition,null));}
            }
        }

        return possibleMoves;
    }
}
