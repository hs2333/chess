package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Queen implements MoveCalculator{
    @Override
    public Collection<ChessMove> moveMove(ChessBoard board, ChessPosition position) {

        //chess moving direction
        int[] rowD = {1,1,1, 0,0, -1,-1,-1};
        int[] colD = {-1,0,1, -1,1, -1,0,1};

        return MoveHelp.generateSlidingMoves(board, position, rowD, colD);
    };
}
