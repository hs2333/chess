package chess;

import java.util.ArrayList;
import java.util.Collection;

public class MoveHelp {
    //queen and bishop
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

    //king
    public static Collection<ChessMove> getMovesInDirections(ChessBoard board, ChessPosition position,
                                                             int[] rowD, int[] colD, boolean multiStep) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);

        for (int i = 0; i < rowD.length; i++) {
            int newRow = position.getRow() + rowD[i];
            int newCol = position.getColumn() + colD[i];

            while (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(newPos);

                if (targetPiece == null) {
                    possibleMoves.add(new ChessMove(position, newPos, null));
                } else if (targetPiece.getTeamColor() != currentPiece.getTeamColor()) {
                    possibleMoves.add(new ChessMove(position, newPos, null));
                    break;
                } else {
                    break;
                }

                if (!multiStep) break; // Don't continue if it's a one-step piece

                newRow += rowD[i];
                newCol += colD[i];
            }
        }
        return possibleMoves;
    }

    //knight
    public static Collection<ChessMove> getJumpMoves(ChessBoard board, ChessPosition position, int[][] offsets) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        ChessPiece currentPiece = board.getPiece(position);

        for (int[] offset : offsets) {
            int newRow = position.getRow() + offset[0];
            int newCol = position.getColumn() + offset[1];

            if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece targetPiece = board.getPiece(newPos);

                if (targetPiece == null || targetPiece.getTeamColor() != currentPiece.getTeamColor()) {
                    possibleMoves.add(new ChessMove(position, newPos, null));
                }
            }
        }
        return possibleMoves;
    }

    //pawn
    public static void addMoveOrPromotions(Collection<ChessMove> moves, ChessPosition from, ChessPosition to, int promotionRow, int currentRow) {
        if (currentRow == promotionRow) {
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.QUEEN));
        } else {
            moves.add(new ChessMove(from, to, null));
        }
    }
}
