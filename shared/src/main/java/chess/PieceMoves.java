package chess;

import java.util.ArrayList;
import java.util.Collection;

interface PieceMoves {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);

    //checks if the piece can move to that location
    default boolean canMove(ChessBoard board, int row, int col, ChessGame.TeamColor color){
        if (row>0 && row<9 && col>0 && col<9){
            ChessPiece theSpot = board.getPiece(new ChessPosition(row,col));
            if(theSpot == null){return true;}
            if(theSpot.getTeamColor() != color){return true;}
        }
        return false;
    }

    //creates movement list for pieces
    default Collection<ChessMove> createMoveList(ChessPosition startingPosition, Collection<ChessPosition> moves){
        ArrayList<ChessMove> theMoves = new ArrayList<>();
        for (ChessPosition pos : moves) {
            theMoves.add(new ChessMove(startingPosition, pos, null));
        }
        return theMoves;
    }

    //creates end positions for rook and bishop
    default Collection<ChessPosition> directionalMoves(ChessBoard board, ChessPosition position, int[] rowLocations, int[] colLocations){
        ArrayList<ChessPosition> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        for(int i=0;i<4;i++){
            int tempRow = position.getRow();
            int tempCol = position.getColumn();

            while(true){
                //update info
                tempRow += rowLocations[i];
                tempCol += colLocations[i];
                //sliding along in the direction until you can't
                if (!canMove(board, tempRow, tempCol, color)){break;}
                moves.add(new ChessPosition(tempRow, tempCol));
                //stopping when we have encountered an enemy piece
                if (board.getPiece(new ChessPosition(tempRow, tempCol)) != null){break;}
            }
        }
        return moves;
    }
}

class PawnMoves implements PieceMoves {
    private boolean pawnMove(ChessBoard board, int row, int col){
        if (row>0 && row<9 && col>0 && col<9){
            return board.getPiece(new ChessPosition(row, col)) == null;
        }
        return false;
    }

    private void captureMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, int direction, ArrayList<ChessPosition> moves){
        ChessGame.TeamColor enemyColor = ChessGame.TeamColor.BLACK;
        if(color == ChessGame.TeamColor.BLACK){enemyColor = ChessGame.TeamColor.WHITE;}

        if(canMove(board,position.getRow()+direction,position.getColumn()-1,color)){
            ChessPosition enemyPosition = new ChessPosition(position.getRow()+direction,position.getColumn()-1);
            if(board.getPiece(enemyPosition) != null && board.getPiece(enemyPosition).getTeamColor() == enemyColor){
                moves.add(new ChessPosition(position.getRow()+direction,position.getColumn()-1));}
        }
        if(canMove(board,position.getRow()+direction,position.getColumn()+1,color)){
            ChessPosition enemyPosition = new ChessPosition(position.getRow()+direction,position.getColumn()+1);
            if(board.getPiece(enemyPosition) != null && board.getPiece(enemyPosition).getTeamColor() == enemyColor){
                moves.add(new ChessPosition(position.getRow()+direction,position.getColumn()+1));}
        }
    }

    private Collection<ChessMove> handlePromotion(ArrayList<ChessPosition> moves, ChessPosition position){
        ArrayList<ChessMove> promotedMoves = new ArrayList<>();
        for (ChessPosition pos : moves) {
            if (pos.getRow() == 1 || pos.getRow() == 8) {
                promotedMoves.add(new ChessMove(position, pos, ChessPiece.PieceType.QUEEN));
                promotedMoves.add(new ChessMove(position, pos, ChessPiece.PieceType.BISHOP));
                promotedMoves.add(new ChessMove(position, pos, ChessPiece.PieceType.ROOK));
                promotedMoves.add(new ChessMove(position, pos, ChessPiece.PieceType.KNIGHT));
            } else {
                promotedMoves.add(new ChessMove(position, pos, null));
            }
        }
        return promotedMoves;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ArrayList<ChessPosition> moves = new ArrayList<>();
        //getting information
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        int direction = 1;
        if (color == ChessGame.TeamColor.BLACK) {
            direction = -1;
        }
        int startRow = 2;
        if (color == ChessGame.TeamColor.BLACK) {
            startRow = 7;
        }

        //basic movement
        if (pawnMove(board, position.getRow() + direction, position.getColumn())) {
            moves.add(new ChessPosition(position.getRow() + direction, position.getColumn()));
            //checking first double movement
            if (position.getRow() == startRow && pawnMove(board, position.getRow() + 2 * direction, position.getColumn())) {
                moves.add(new ChessPosition(position.getRow() + 2 * direction, position.getColumn()));
            }
        }
        //capture movement
        captureMoves(board, position, color, direction, moves);
        //check promotions
        return handlePromotion(moves, position);
    }
}

class KingMoves implements PieceMoves {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        ArrayList<ChessPosition> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        for(int k=-1;k<2;k++){
            for(int m=-1;m<2;m++){
                if(canMove(board,position.getRow()+k,position.getColumn()+m,color)){
                    moves.add(new ChessPosition(position.getRow()+k,position.getColumn()+m));
                }
            }
        }
        return createMoveList(position,moves);
    }
}

class KnightMoves implements PieceMoves {


    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        ArrayList<ChessPosition> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        //moves: (-2,-1)(-2,1)(-1,-2)(-1,2)(1,-2)(1,2)(2,-1)(2,1)
        int[] rows = {-2,-1,1,2};
        int[] cols1 = {-1,1};
        int[] cols2 = {-2,2};
        for(int row:rows){
            int[] currentCols = cols2;
            if(row%2==0){currentCols = cols1;}
            for(int col:currentCols){
                if(canMove(board,position.getRow()+row,position.getColumn()+col,color)){moves.add(new ChessPosition(position.getRow()+row,position.getColumn()+col));}
            }
        }
        return createMoveList(position,moves);
    }
}

class RookMoves implements PieceMoves {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        Collection<ChessPosition> moves = directionalMoves(board, position, new int[]{0,0,1,-1},new int[]{1,-1,0,0});
        return createMoveList(position,moves);
    }
}

class BishopMoves implements PieceMoves {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        Collection<ChessPosition> moves = directionalMoves(board, position, new int[]{1,1,-1,-1},new int[]{1,-1,1,-1});
        return createMoveList(position,moves);
    }
}

class QueenMoves implements PieceMoves {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        ArrayList<ChessMove> queenMoves = new ArrayList<>();
        queenMoves.addAll(new RookMoves().pieceMoves(board,position));
        queenMoves.addAll(new BishopMoves().pieceMoves(board,position));
        return queenMoves;
    }
}
