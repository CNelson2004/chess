package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessGame.TeamColor teamTurn;
    ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param teamColor the team whose turn it is
     */
    public void setTeamTurn(TeamColor teamColor) {
        teamTurn = teamColor;
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
        //This takes into account moves that leave king exposed. Thus not valid.
        //Getting all possible moves
        ChessPiece thePiece = board.getPiece(startPosition);
        Collection<ChessMove> moves = thePiece.pieceMoves(board,startPosition);
        //make the theoretical move with a hypothetical board and check if the king would be in check
        ChessBoard boardState = ChessBoard.copyBoard(board);
        Collection<ChessMove> finalMoves = new ArrayList<>();
        for(ChessMove move: moves){
            board = ChessBoard.copyBoard(boardState);
            ChessPosition endPosition = move.getEndPosition();
            board.addPiece(endPosition,thePiece);
            board.removePiece(startPosition);
            if(!isInCheck(thePiece.getTeamColor())){finalMoves.add(move);}
        }
        board = ChessBoard.copyBoard(boardState);
        return finalMoves;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //If move is valid, do it, if not throw exception
        //checking if a piece actually exists
        if(board.getPiece(move.getStartPosition())==null){throw new InvalidMoveException("No piece exists.");}
        //checking if the team turn is correct
        TeamColor moveColor = board.getPiece(move.getStartPosition()).getTeamColor();
        if(teamTurn != moveColor){throw new InvalidMoveException("Not your team's turn.");}
        //getting info and moving stuff
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        Collection<ChessPosition> validPositions = new ArrayList<>();
        for(ChessMove validMove: validMoves){validPositions.add(validMove.getEndPosition());}
        if(validPositions.contains(move.getEndPosition())){
            board.addPiece(move.getEndPosition(),board.getPiece(move.getStartPosition()));
            board.removePiece(move.getStartPosition());
            //changing turn
            switch(moveColor){
                case WHITE:
                    setTeamTurn(TeamColor.BLACK);
                    break;
                case BLACK:
                    setTeamTurn(TeamColor.WHITE);
                    break;
            }
            //Dealing with pawn promotion
            if((board.getPiece(move.getEndPosition()).getPieceType()==ChessPiece.PieceType.PAWN)&&
                    ((move.getEndPosition().getRow()==8)||(move.getEndPosition().getRow()==1))){
                board.addPiece(move.getEndPosition(),new ChessPiece(moveColor,move.getPromotionPiece()));
            }
        }
        else{throw new InvalidMoveException("Not a valid move.");}
    }

    /**
     * Returns the location of all the pieces of a specific team on the board
     *
     * @param teamColor The team pieces we are looking for
     * @return all positions of thep pieces
     */
    public Collection<ChessPosition> getAllPieces(TeamColor teamColor) {
        ArrayList<ChessPosition> pieces = new ArrayList<>();
        for (int i=1;i<9;i++){
            for(int j=1;j<9;j++){
                try {
                    if (board.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor) {
                        pieces.add(new ChessPosition(i, j));
                    }
                }
                catch(Exception e){continue;}
            }
        }
        return pieces;
    }

    public Collection<Collection<ChessMove>> getPieceMoves(TeamColor teamColor) {
        Collection<ChessPosition> pieces = getAllPieces(teamColor);
        Collection<ChessMove> moves;
        Collection<Collection<ChessMove>> allMoves = new ArrayList<>();
        for(ChessPosition piecePosition: pieces){
            ChessPiece thePiece = new ChessPiece(teamColor,board.getPiece(piecePosition).getPieceType());
            moves = thePiece.pieceMoves(board, piecePosition);
            allMoves.add(moves);
        }
        return allMoves;
    }

    /**
     * @param teamColor team color we are checking
     * @return all the end positons of all the pieces of that team color
     */
    public Collection<Collection<ChessPosition>> getPieceEndPositions(TeamColor teamColor) {
        ArrayList<ChessPosition> endPositions = new ArrayList<>();
        Collection<Collection<ChessPosition>> allEndPositions = new ArrayList<>();
        Collection<Collection<ChessMove>> allMoves = getPieceMoves(teamColor);
        for(Collection<ChessMove> moves: allMoves){
            for(ChessMove move: moves){endPositions.add(move.getEndPosition());}
            allEndPositions.add(endPositions);
        }
        return allEndPositions;
    }

    /**
     * Finds the location of the king of a certain team on the board
     *
     * @param teamColor which team King we are looking for
     * @return Returns king's location on the board
     */
    public ChessPosition findKing(TeamColor teamColor) {
        for (int i=1;i<9;i++){
            for(int j=1;j<9;j++){
                try {
                    if ((board.getPiece(new ChessPosition(i, j)).getPieceType() == ChessPiece.PieceType.KING)
                            &&(board.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor)) {
                            return new ChessPosition(i, j);
                    }
                }
                catch (Exception e) {continue;}
            }
        }
        throw new RuntimeException("King not found");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //If the opponent can make a move to capture the king, then he is in check
        ChessPosition kingPosition = findKing(teamColor);
        //getting enemy end positions
        Collection<Collection<ChessPosition>> allEndPositions = getPieceEndPositions(TeamColor.WHITE);
        if(teamColor == TeamColor.WHITE){allEndPositions = getPieceEndPositions(TeamColor.BLACK);}
        //checking them
        for(Collection<ChessPosition> piecePositions: allEndPositions){
            if(piecePositions.contains(kingPosition)){return true;}
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
        //returns true for if no piece has any valid moves and king in check
         if(!isInCheck(teamColor)){return false;}
         Collection<ChessPosition> pieces = getAllPieces(teamColor);
         for(ChessPosition piecePosition: pieces){
         if(!validMoves(piecePosition).isEmpty()){return false;}
         }
         return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //returns true if no valid moves and king is not in check
        if(isInCheck(teamColor)){return false;}
        Collection<ChessPosition> pieces = getAllPieces(teamColor);
        for(ChessPosition piecePosition: pieces){
            if(!validMoves(piecePosition).isEmpty()){return false;}
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
