package chess;

import java.util.ArrayList;
import java.util.Collection;


public class Rook implements MoveCalculator{
    @Override
    public Collection<ChessMove> Move(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int chessRow = position.getRow();
        int chessCol = position.getColumn();
        int[] rowDir = {-1,0,0,1};
        int[] colDir = {0,1,-1,0};
            
        for (int i = 0; i<=3; i++) {
            int newRow = chessRow + rowDir[i];
            int newCol = chessCol + colDir[i];

            
            while (true) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece oldPiece = board.getPiece(position);
                ChessPiece newPiece = board.getPiece(newPosition);
                if ((newRow >= 1 && newRow <=8) && (newCol >= 1 && newCol <=8)) {
                    if  ((newPiece==null) || (newPiece.getTeamColor()!=oldPiece.getTeamColor())) {
                        possibleMoves.add(new ChessMove(position,newPosition,null));
                    }
                   else {break;}
            }
                newRow += rowDir[i];
                newCol += colDir[i];

            }
        }
        return possibleMoves;
    }
}