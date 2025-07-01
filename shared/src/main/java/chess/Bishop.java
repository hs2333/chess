package chess;

import java.util.ArrayList;
import java.util.Collection;


public class Bishop implements MoveCalculator{
    @Override
    public Collection<ChessMove> Move(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int chessRow = position.getRow();
        int chessCol = position.getColumn();
        int[] rowDir = {-1,-1,1,1};
        int[] colDir = {-1,1,-1,1};
            
        for (int i = 1; i<=4; i++) {
            int newRow = chessRow + rowDir[i];
            int newCol = chessCol + colDir[i];

            
            while (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece oldPiece = board.getPiece(position);
                ChessPiece newPiece = board.getPiece(newPosition);
                    if  ((newPiece==null) || (newPiece.getTeamColor()!=oldPiece.getTeamColor())) {
                        possibleMoves.add(new ChessMove(position,newPosition,null));
                    }
                   else {break;}
                newRow += rowDir[i];
                newCol += colDir[i];
            }
        }
        return possibleMoves;
    }
}