package chess;

import java.util.Collection;

public interface MoveCalculator {
    Collection<ChessMove> Move(ChessBoard board, ChessPosition position);
}


