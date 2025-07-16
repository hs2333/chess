package chess;

import java.util.Collection;

public interface MoveCalculator {
    Collection<ChessMove> moveMove(ChessBoard board, ChessPosition position);
}


