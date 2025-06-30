package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */

public class ChessBoard {
    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8];

        //add chess
        //queen
        addPiece(new ChessPosition(1,4), ChessPiece.PieceType.QUEEN("white"));
        addPiece(new ChessPosition(8,4), ChessPiece.PieceType.QUEEN("black")); //???

        //king
        addPiece(new ChessPosition(1,5), ChessPiece.PieceType.KING("white"));
        addPiece(new ChessPosition(8,5), ChessPiece.PieceType.KING("black"));

        //pawn
        for (int i = 1; i<=8; i++) {
            addPiece(new ChessPosition(2,i), new Pawn("white"));
            addPiece(new ChessPosition(7,i), new Pawn("black"));
        }

        //rock
        addPiece(new ChessPosition(1,1), new Rock("white"));
        addPiece(new ChessPosition(1,8), new Rock("white"));
        addPiece(new ChessPosition(8,1), new Rock("black"));
        addPiece(new ChessPosition(8,8), new Rock("black"));

        //knight
        addPiece(new ChessPosition(1,2), new Knight("white"));
        addPiece(new ChessPosition(1,7), new Knight("white"));
        addPiece(new ChessPosition(8,2), new Knight("black"));
        addPiece(new ChessPosition(8,7), new Knight("black"));

        //bishop
        addPiece(new ChessPosition(1,3), new Bishop("white"));
        addPiece(new ChessPosition(1,6), new Bishop("white"));
        addPiece(new ChessPosition(8,3), new Bishop("black"));
        addPiece(new ChessPosition(8,6), new Bishop("black"));
    }
}
