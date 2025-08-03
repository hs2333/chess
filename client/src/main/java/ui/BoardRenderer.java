package ui;

import chess.*;
import static ui.EscapeSequences.*;

import java.util.Collection;

public class BoardRenderer {

    public static void render(ChessGame game, boolean whitePerspective) {
        renderInternal(game, whitePerspective, null, null);
    }

    public static void renderWithHighlights(ChessGame game, boolean whitePerspective,
                                            ChessPosition fromPos, Collection<ChessMove> moves) {
        renderInternal(game, whitePerspective, fromPos, moves);
    }

    private static void renderInternal(ChessGame game, boolean whitePerspective,
                                       ChessPosition highlightFrom, Collection<ChessMove> highlightMoves) {

        ChessBoard board = game.getBoard();
        int[] ranks = whitePerspective ? new int[]{8,7,6,5,4,3,2,1} : new int[]{1,2,3,4,5,6,7,8};
        char[] files = whitePerspective ? new char[]{'a','b','c','d','e','f','g','h'} : new char[]{'h','g','f','e','d','c','b','a'};

        System.out.println();
        for (int row : ranks) {
            System.out.print(" " + row + " ");
            for (char col : files) {
                ChessPosition pos = new ChessPosition(row, col - 'a' + 1);
                ChessPiece piece = board.getPiece(pos);

                boolean isFrom = highlightFrom != null && pos.equals(highlightFrom);
                boolean isTo = highlightMoves != null && highlightMoves.stream().anyMatch(m -> m.getEndPosition().equals(pos));

                String bg;
                if (isFrom) {
                    bg = SET_BG_COLOR_GREEN;
                } else if (isTo) {
                    bg = SET_BG_COLOR_YELLOW;
                } else {
                    boolean lightSquare = (row + (col - 'a' + 1)) % 2 == 0;
                    bg = lightSquare ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;
                }

                System.out.print(bg);
                if (piece != null) {
                    String symbol = getSymbol(piece);
                    String fg = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE;
                    System.out.print(fg + " " + symbol + " ");
                } else {
                    System.out.print("   ");
                }
                System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
            }
            System.out.println();
        }

        // file labels
        System.out.print("   ");
        for (char col : files) {
            System.out.print(" " + col + " ");
        }
        System.out.println(RESET_TEXT_COLOR + RESET_BG_COLOR + "\n");
    }

    private static String getSymbol(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case ROOK -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case PAWN -> "P";
        };
    }
}
