package chess;

import org.junit.platform.commons.util.BlacklistedExceptions;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard squares;
    public ChessGame() {
        squares = new ChessBoard();
        squares.resetBoard();
        turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = squares.getPiece(startPosition);
        if (piece == null || piece.getTeamColor() != turn) {
            return null;
        } Collection<ChessMove> allMoves = piece.pieceMoves(squares, startPosition);
        return new ArrayList<>(allMoves);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = squares.getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != turn) {
            throw new InvalidMoveException();
        } Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());
        if (legalMoves == null || !legalMoves.contains(move)) {
            throw new InvalidMoveException();
        }

        ChessPiece newPiece = applyPromotionIfNeeded(piece, move);
        squares.addPiece(move.getEndPosition(), newPiece);
        squares.addPiece(move.getStartPosition(), null);

        turn = (turn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    private ChessPiece applyPromotionIfNeeded(ChessPiece original, ChessMove move) {
        ChessPiece.PieceType promotion = move.getPromotionPiece();
        if (promotion == null) return original;

        return switch (promotion) {
            case QUEEN -> new Queen(original.getTeamColor());
            case ROOK -> new Rook(original.getTeamColor());
            case BISHOP -> new Bishop(original.getTeamColor());
            case KNIGHT -> new Knight(original.getTeamColor());
            default -> original;
        };
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        if (kingPosition == null) return false;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = squares.getPiece(pos);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(squares, pos);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) return false;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = squares.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(squares, pos);
                    for (ChessMove move : moves) {
                        ChessBoard testBoard = cloneBoard();
                        testBoard.addPiece(move.getEndPosition(), applyPromotionIfNeeded(piece, move));
                        testBoard.addPiece(move.getStartPosition(), null);
                        if (!wouldBeInCheck(teamColor, testBoard)) return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) return false;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = squares.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(squares, pos);
                    for (ChessMove move : moves) {
                        ChessBoard testBoard = cloneBoard();
                        testBoard.addPiece(move.getEndPosition(), applyPromotionIfNeeded(piece, move));
                        testBoard.addPiece(move.getStartPosition(), null);
                        if (!wouldBeInCheck(teamColor, testBoard)) return false;
                    }
                }
            }
        }
        return true;
    }
    private ChessPosition findKing(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = squares.getPiece(pos);
                if (piece != null &&
                        piece.getTeamColor() == teamColor &&
                        piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return pos;
                }
            }
        }
        return null;
    }

    private boolean wouldBeInCheck(TeamColor teamColor, ChessBoard testBoard) {
        ChessPosition kingPos = null;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = testBoard.getPiece(pos);
                if (piece != null &&
                        piece.getTeamColor() == teamColor &&
                        piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPos = pos;
                    break;
                }
            }
        }
        if (kingPos == null) return true;

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = testBoard.getPiece(pos);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    for (ChessMove move : piece.pieceMoves(testBoard, pos)) {
                        if (move.getEndPosition().equals(kingPos)) return true;
                    }
                }
            }
        }
        return false;
    }

    private ChessBoard cloneBoard() {
        ChessBoard newBoard = new ChessBoard();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = squares.getPiece(pos);
                if (piece != null) {
                    newBoard.addPiece(pos, piece); // Assuming pieces are immutable or reused
                }
            }
        }
        return newBoard;
    }
    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.squares = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return squares;
    }
}
