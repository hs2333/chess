package chess;

import javax.xml.validation.Validator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor color;
    private ChessBoard squares;

    public ChessGame() {
        setTeamTurn(TeamColor.WHITE);
        squares = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return color;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        color = team;
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
        //get possibleMove
        Collection<ChessMove> possibleMove = new ArrayList<>(squares.getPiece(startPosition).pieceMoves(squares, startPosition));
        Collection<ChessMove> validMove = new ArrayList<>();
        //if null
        if (piece == null) {return null;}
        //valid check at the end position
        for (ChessMove move : possibleMove) {
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece newPiece = squares.getPiece(endPosition);
            //first clear the piece at startPosition
            squares.addPiece(startPosition,null);
            squares.addPiece(endPosition,piece);
            if (!(isInCheck(piece.getTeamColor()))) {
                validMove.add(move);
            }
            //cancel the move
            squares.addPiece(endPosition,newPiece);
            squares.addPiece(startPosition,piece);
        }
        return validMove;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> validMove = validMoves(move.getStartPosition());
        if (validMove == null) {throw new InvalidMoveException("No valid moves.");}

        ChessPosition position = move.getStartPosition();
        ChessPiece piece = squares.getPiece(position);
        if (piece == null) {throw new InvalidMoveException("Piece doesn't exist.");}
        ChessGame.TeamColor color = piece.getTeamColor();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        if (promotionPiece != null) {
            piece = new ChessPiece(color,promotionPiece);
        }

        if ((validMove.contains(move)) && (getTeamTurn() == color)) {
            squares.addPiece(position,null);
            squares.addPiece(endPosition,piece);
            TeamColor nextTurn;
            if (getTeamTurn() == TeamColor.WHITE) {
                nextTurn = TeamColor.BLACK;
            } else {
                nextTurn = TeamColor.WHITE;
            }
            setTeamTurn(nextTurn);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition checkPosition = null;
        //loop through the board - find kind
        for (int row=1; row<=8; row++) {
            for (int col=1; col<=8; col++) {
                ChessPosition position = new ChessPosition(row,col);
                ChessPiece piece = squares.getPiece(position);
                //skip if no piece
                if (piece == null) {continue;}
                //check team and type
                if ((piece.getTeamColor() == color) && (piece.getPieceType() == ChessPiece.PieceType.KING)) {
                    checkPosition = position;
                }
            }
        }
        //loop through the board - find enemy
        for (int row=1; row<=8; row++) {
            for (int col=1; col<=8; col++) {
                ChessPosition position = new ChessPosition(row,col);
                ChessPiece piece = squares.getPiece(position);
                //skip if no piece
                if (piece == null) {continue;}
                ChessGame.TeamColor color = piece.getTeamColor();
                //check team
                if (color != teamColor) {
                    for (ChessMove move : piece.pieceMoves(squares, position)) {
                        if (move.getEndPosition().equals(checkPosition)) {
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
        return ((isInCheck(color)) && (isInStalemate(color)));
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        for (int row=1; row<=8; row++) {
            for (int col=1; col<=8; col++) {
                ChessPosition position = new ChessPosition(row,col);
                ChessPiece piece = squares.getPiece(position);
                //check team
                if (piece.getTeamColor() == color) {
                    if (!((validMoves(position)).isEmpty())) {
                        return false;
                    }

                }
            }
            }
        return true;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return color == chessGame.color && Objects.equals(squares, chessGame.squares);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, squares);
    }
}

