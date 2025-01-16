package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final PieceType pieceType;
    private final ChessGame.TeamColor teamColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        teamColor = pieceColor;
        pieceType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() { return teamColor; }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() { return pieceType; }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        switch(piece.getPieceType()){
            case PAWN:
                PieceMoves pawnMovement = new PawnMoves();
                return pawnMovement.pieceMoves(board,myPosition);
            case KING:
                PieceMoves kingMovement = new KingMoves();
                return kingMovement.pieceMoves(board,myPosition);
            case KNIGHT:
                PieceMoves knightMovement = new KnightMoves();
                return knightMovement.pieceMoves(board,myPosition);
            case ROOK:
                PieceMoves rookMovement = new RookMoves();
                return rookMovement.pieceMoves(board,myPosition);
            case BISHOP:
                PieceMoves bishopMovement = new BishopMoves();
                return bishopMovement.pieceMoves(board,myPosition);
            case QUEEN:
                PieceMoves queenMovement = new QueenMoves();
                return queenMovement.pieceMoves(board,myPosition);
            default:
                throw new RuntimeException("Unknown piece type: " + piece.getPieceType());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return pieceType == that.pieceType && teamColor == that.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceType, teamColor);
    }

    @Override
    public String toString() {
        return "Type:" + pieceType + ",Color:" + teamColor;
    }
}


