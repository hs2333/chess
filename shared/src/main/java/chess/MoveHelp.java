package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MoveHelp {
    public static Collection<ChessMove> generateSlidingMoves(
            ChessBoard board, ChessPosition position, int[] rowD, int[] colD) {

        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        ChessPiece oldPiece = board.getPiece(position);

        for (int i = 0; i < rowD.length; i++) {
            int newRow = row + rowD[i];
            int newCol = col + colD[i];

            while (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece newPiece = board.getPiece(newPosition);

                if (newPiece == null) {
                    possibleMoves.add(new ChessMove(position, newPosition, null));
                } else if (newPiece.getTeamColor() != oldPiece.getTeamColor()) {
                    possibleMoves.add(new ChessMove(position, newPosition, null));
                    break;
                } else {
                    break;
                }

                newRow += rowD[i];
                newCol += colD[i];
            }
        }

        return possibleMoves;
    }
}
