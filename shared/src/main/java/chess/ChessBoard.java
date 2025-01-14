package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {}

    public static ChessBoard copyBoard(ChessBoard theBoard) {
        ChessBoard copy = new ChessBoard();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                copy.addPiece(new ChessPosition(i,j),theBoard.getPiece(new ChessPosition(i,j)));
            }
        }
        return copy;
    }

    public ChessPiece[][] getBoard() {return board;}

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) { board[position.getRow()-1][position.getColumn()-1] = piece; }

    public void removePiece(ChessPosition position) {board[position.getRow()-1][position.getColumn()-1] = null;}

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) { return board[position.getRow()-1][position.getColumn()-1]; }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //Place all pieces in their starting positions. (Use add piece())
        this.addPiece(new ChessPosition(1,1),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(1,2),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(1,3),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(1,4),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.QUEEN));
        this.addPiece(new ChessPosition(1,5),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.KING));
        this.addPiece(new ChessPosition(1,6),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(1,7),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(1,8),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.ROOK));
        for(int i=1;i<=8;i++){
            this.addPiece(new ChessPosition(2,i),new ChessPiece(ChessGame.TeamColor.WHITE,ChessPiece.PieceType.PAWN));
        }
        for(int i=1;i<=8;i++){
            this.addPiece(new ChessPosition(7,i),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.PAWN));
        }
        this.addPiece(new ChessPosition(8,1),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.ROOK));
        this.addPiece(new ChessPosition(8,2),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(8,3),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(8,4),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.QUEEN));
        this.addPiece(new ChessPosition(8,5),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.KING));
        this.addPiece(new ChessPosition(8,6),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.BISHOP));
        this.addPiece(new ChessPosition(8,7),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.KNIGHT));
        this.addPiece(new ChessPosition(8,8),new ChessPiece(ChessGame.TeamColor.BLACK,ChessPiece.PieceType.ROOK));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessBoard that)) {
            return false;
        }
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "board=" + Arrays.deepToString(board);
    }
}
