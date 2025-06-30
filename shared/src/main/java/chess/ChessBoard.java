package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board;
    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow() -1;
        int col = position.getColumn() -1;
        board[row][col] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow() -1;
        int col = position.getColumn() -1;
        return board[row][col];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];

        //add chess
        //queen
        addPiece(new ChessPosition(1,4), new QUEEN("white"));
        addPiece(new ChessPosition(8,4), new QUEEN("black")); //???

        //king
        addPiece(new ChessPosition(1,5), new King("white"));
        addPiece(new ChessPosition(8,5), new King("black"));

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
