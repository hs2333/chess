package chess;

import java.util.ArrayList;
import java.util.Collection;


public class Knight implements MoveCalculator{
    @Override
    public Collection<ChessMove> Move(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int chessRow = position.getRow();
        int chessCol = position.getColumn();
        int[] rowDir = {-1, 1, -2, 2, -2, 2, -1, 1};
        int[] colDir = {2, 2, 1, 1, -1, -1, -2, -2};
        for (int i = 0; i<8; i++) {
            int newRow = chessRow + rowDir[i];
            int newCol = chessCol + colDir[i];

            //check valid position+

            if ((newRow >= 1 && newRow <=8) && (newCol >= 1 && newCol <=8)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece oldPiece = board.getPiece(position);
                ChessPiece newPiece = board.getPiece(newPosition);
                if  ((newPiece==null) || (newPiece.getTeamColor()!=oldPiece.getTeamColor())) {
                    possibleMoves.add(new ChessMove(position,newPosition,null));
                }
            }
        }
        return possibleMoves;
    }
}