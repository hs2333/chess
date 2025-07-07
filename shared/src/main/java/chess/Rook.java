package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Rook implements MoveCalculator{
    @Override
    public Collection<ChessMove> Move(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMove = new ArrayList<>();

        //chess row and column
        int row = position.getRow();
        int col = position.getColumn();
        ChessPiece oldPiece = board.getPiece(position);

        //chess moving direction
        int[] colD = {0,-1,1,0};
        int[] rowD = {-1,0,0,1};

        //loop over possible moves
        for (int i=0; i<4; i++) {
            int newRow = row +rowD[i];
            int newCol = col +colD[i];

            //check inboard
            while ((newRow >=1 && newRow <=8) && (newCol >=1 && newCol <=8)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece newPiece = board.getPiece(newPosition);
                //check occupancy and team
                if (newPiece == null) {
                    possibleMove.add(new ChessMove(position, newPosition, null));}
                else if (newPiece.getTeamColor() != oldPiece.getTeamColor()) {
                    possibleMove.add(new ChessMove(position, newPosition, null));
                    break;
                } else {break;}
                newRow += rowD[i];
                newCol += colD[i];
            }
        }

        return possibleMove;
    }
}
