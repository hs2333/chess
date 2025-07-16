package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King implements MoveCalculator{
    @Override
    public Collection<ChessMove> moveMove(ChessBoard board, ChessPosition position) {

        //chess moving direction
        int[] rowD = {1, 1, 1, 0, 0, -1, -1, -1};
        int[] colD = {-1, 0, 1, -1, 1, -1, 0, 1};

        return MoveHelp.getMovesInDirections(board, position, rowD, colD, false);
    }
}
