package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Knight implements MoveCalculator{
    @Override
    public Collection<ChessMove> moveMove(ChessBoard board, ChessPosition position) {

        //chess moving direction
        int[] rowD = {-2,2, -1,1, -1,1, -2,2};
        int[] colD = {1,1, 2,2, -2,-2, -1,-1};

        int[][] knightMoves = new int[rowD.length][2];
        for (int i = 0; i < rowD.length; i++) {
            knightMoves[i][0] = rowD[i];
            knightMoves[i][1] = colD[i];
        }

        return MoveHelp.getJumpMoves(board, position, knightMoves);
    }
}
