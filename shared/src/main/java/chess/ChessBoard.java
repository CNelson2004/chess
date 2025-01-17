package chess;

import java.util.Arrays;
import java.util.Objects;

import static chess.ChessPiece.PieceType.ROOK;
import static chess.ChessPiece.PieceType.KNIGHT;
import static chess.ChessPiece.PieceType.BISHOP;
import static chess.ChessPiece.PieceType.QUEEN;
import static chess.ChessPiece.PieceType.KING;
import static chess.ChessPiece.PieceType.PAWN;

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
        ChessPiece.PieceType[] lineup = {ROOK,KNIGHT,BISHOP,QUEEN,KING,BISHOP,KNIGHT, ROOK};
        for(int i=1;i<=8;i++){
            addPiece(new ChessPosition(1,i),new ChessPiece(ChessGame.TeamColor.WHITE,lineup[i-1]));
        }
        for(int i=1;i<=8;i++){
            addPiece(new ChessPosition(2,i),new ChessPiece(ChessGame.TeamColor.WHITE,PAWN));
        }
        for(int i=1;i<=8;i++){
            addPiece(new ChessPosition(7,i),new ChessPiece(ChessGame.TeamColor.BLACK,PAWN));
        }
        for(int i=1;i<=8;i++){
            addPiece(new ChessPosition(8,i),new ChessPiece(ChessGame.TeamColor.BLACK,lineup[i-1]));
        }
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
