package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Knight implements MoveCalculator{
    @Override
    public Collection<ChessMove> moveMove(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMove = new ArrayList<>();

        //chess row and column
        int row = position.getRow();
        int col = position.getColumn();
        ChessPiece oldPiece = board.getPiece(position);

        //chess moving direction
        int[] rowD = {-2,2, -1,1, -1,1, -2,2};
        int[] colD = {1,1, 2,2, -2,-2, -1,-1};

        //loop over possible moves
        for (int i=0; i<8; i++) {
            int newRow = row +rowD[i];
            int newCol = col +colD[i];

            //check inboard
            if ((newRow >=1 && newRow <=8) && (newCol >=1 && newCol <=8)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece newPiece = board.getPiece(newPosition);
                //check occupancy and team
                if ((newPiece == null) || (newPiece.getTeamColor() != oldPiece.getTeamColor())) {
                    possibleMove.add(new ChessMove(position, newPosition, null));
                }
            }
        }

        return possibleMove;
    }
}
