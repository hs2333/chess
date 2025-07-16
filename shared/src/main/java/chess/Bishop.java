package chess;

import java.util.ArrayList;
import java.util.Collection;

public class Bishop implements MoveCalculator{
    @Override
    public Collection<ChessMove> moveMove(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> possibleMove = new ArrayList<>();

        //chess moving direction
        int[] rowD = {-1, -1, 1, 1};
        int[] colD = {-1, 1, -1, 1};

        return MoveHelp.generateSlidingMoves(board, position, rowD, colD);
    }
}
