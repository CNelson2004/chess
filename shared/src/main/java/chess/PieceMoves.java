package chess;

import java.util.ArrayList;
import java.util.Collection;

interface PieceMoves {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);

    default boolean canMove(ChessBoard board, int row, int col, ChessGame.TeamColor color){
        if (row>0 && row<9 && col>0 && col<9){
            ChessPiece theSpot = board.getPiece(new ChessPosition(row,col));
            if(theSpot == null){return true;}
            ChessGame.TeamColor enemyColor = theSpot.getTeamColor();
            switch(color){
                case WHITE:
                    if(enemyColor == ChessGame.TeamColor.BLACK){return true;}
                    break;
                case BLACK:
                    if(enemyColor == ChessGame.TeamColor.WHITE){return true;}
                    break;
            }
        }
        return false;
    }
}

class PawnMoves implements PieceMoves {
    /**
     * @param board: the current chess board
     * @param color: The color of the current pawn
     * @param row: the row we want to go to
     * @param col: the column we want to go to
     * Checks whether a spot is viable for a piece to move to, and why
     * Returns false if:
     * location is out of bounds or there is a piece of the same color there
     */
    @Override
    public boolean canMove(ChessBoard board, int row, int col, ChessGame.TeamColor color){
        if (row>0 && row<9 && col>0 && col<9){
            ChessPiece theSpot = board.getPiece(new ChessPosition(row,col));
            if(theSpot == null){return false;}
            ChessGame.TeamColor enemyColor = theSpot.getTeamColor();
            switch(color){
                case WHITE:
                    if(enemyColor == ChessGame.TeamColor.BLACK){return true;}
                    break;
                case BLACK:
                    if(enemyColor == ChessGame.TeamColor.WHITE){return true;}
                    break;
            }
        }
        return false;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
         ArrayList<ChessMove> theMoves = new ArrayList<>();
         ArrayList<ChessPosition> moves = new ArrayList<>();
         ChessGame.TeamColor color = board.getPiece(position).getTeamColor();

          switch(color){
             case WHITE:
                 //Checking normal movement conditions
                 if(position.getRow()==2){
                     if(board.getPiece(new ChessPosition(position.getRow()+1,position.getColumn())) == null){
                         moves.add(new ChessPosition(position.getRow()+1,position.getColumn()));
                             if(board.getPiece(new ChessPosition(position.getRow()+2,position.getColumn())) == null){moves.add(new ChessPosition(position.getRow()+2,position.getColumn()));}
                         }
                     }
                 else{
                     if(board.getPiece(new ChessPosition(position.getRow()+1,position.getColumn())) == null){moves.add(new ChessPosition(position.getRow()+1,position.getColumn()));}
                 }
                 //checking capture conditions (diagonal)
                 if(canMove(board, position.getRow()+1, position.getColumn()-1, ChessGame.TeamColor.WHITE)) {
                     moves.add(new ChessPosition(position.getRow()+1, position.getColumn()-1));}
                 if(canMove(board, position.getRow()+1, position.getColumn()+1, ChessGame.TeamColor.WHITE)) {
                     moves.add(new ChessPosition(position.getRow()+1, position.getColumn()+1));}
                break;
             case BLACK:
                 //checking normal conditions
                 if(position.getRow()==7){
                     if(board.getPiece(new ChessPosition(position.getRow()-1,position.getColumn())) == null){
                         moves.add(new ChessPosition(position.getRow()-1,position.getColumn()));
                         if(board.getPiece(new ChessPosition(position.getRow()-2,position.getColumn())) == null){moves.add(new ChessPosition(position.getRow()-2,position.getColumn()));}
                     }
                 }
                 else{
                     if(board.getPiece(new ChessPosition(position.getRow()-1,position.getColumn())) == null){moves.add(new ChessPosition(position.getRow()-1,position.getColumn()));}
                 }
                 //checking capture conditions (diagonal)
                 if(canMove(board, position.getRow()-1, position.getColumn()-1, ChessGame.TeamColor.BLACK)) {
                     moves.add(new ChessPosition(position.getRow()-1, position.getColumn()-1));}
                 if(canMove(board, position.getRow()-1, position.getColumn()+1, ChessGame.TeamColor.BLACK)) {
                     moves.add(new ChessPosition(position.getRow()-1, position.getColumn()+1));}
                 break;
         }
        //Creating proper return value and accounting for promotion
        for (ChessPosition pos : moves) {
            if(pos.getRow()==1 || pos.getRow()==8){
                theMoves.add(new ChessMove(position,pos,ChessPiece.PieceType.QUEEN));
                theMoves.add(new ChessMove(position,pos,ChessPiece.PieceType.BISHOP));
                theMoves.add(new ChessMove(position,pos,ChessPiece.PieceType.ROOK));
                theMoves.add(new ChessMove(position,pos,ChessPiece.PieceType.KNIGHT));
            }
            else {theMoves.add(new ChessMove(position,pos,null));}
        }
        return theMoves;
    }
}

class KingMoves implements PieceMoves {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        ArrayList<ChessMove> theMoves = new ArrayList<>();
        ArrayList<ChessPosition> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        for(int k=-1;k<2;k++){
            for(int m=-1;m<2;m++){
                if(canMove(board,position.getRow()+k,position.getColumn()+m,color)){
                    moves.add(new ChessPosition(position.getRow()+k,position.getColumn()+m));
                }
            }
        }
        for (ChessPosition pos : moves) {
            theMoves.add(new ChessMove(position,pos,null));
        }
        return theMoves;
    }
}

class KnightMoves implements PieceMoves {


    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        ArrayList<ChessMove> theMoves = new ArrayList<>();
        ArrayList<ChessPosition> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        //(-2,-1)(-2,1)(-1,-2)(-1,2)(1,-2)(1,2)(2,-1)(2,1)
        int[] rows = {-2,-1,1,2};
        int[] cols1 = {-1,1};
        int[] cols2 = {-2,2};
        for(int row:rows){
            if(row%2==0){
                for(int col:cols1){
                    if(canMove(board,position.getRow()+row,position.getColumn()+col,color)){moves.add(new ChessPosition(position.getRow()+row,position.getColumn()+col));}
                }
            }
            else{
                for(int col:cols2){
                    if(canMove(board,position.getRow()+row,position.getColumn()+col,color)){moves.add(new ChessPosition(position.getRow()+row,position.getColumn()+col));}
                }
            }
        }
        for (ChessPosition pos : moves) {
            theMoves.add(new ChessMove(position,pos,null));
        }
        return theMoves;
    }
}

class RookMoves implements PieceMoves {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        ArrayList<ChessMove> theMoves = new ArrayList<>();
        ArrayList<ChessPosition> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();
        int row = position.getRow();
        int tempRow = position.getRow();
        int col = position.getColumn();
        int tempCol = position.getColumn();
        while(tempRow<9){
            tempRow++;
            if(canMove(board,tempRow,col,color)){
                moves.add(new ChessPosition(tempRow,col));
                if(board.getPiece(new ChessPosition(tempRow,col)) != null){break;}
            }
            else{break;}
        }
        tempRow = position.getRow();
        while(tempRow>0){
            tempRow--;
            if(canMove(board,tempRow,col,color)){
                moves.add(new ChessPosition(tempRow,col));
                if(board.getPiece(new ChessPosition(tempRow,col)) != null){break;}
            }
            else{break;}
        }
        while(tempCol<9){
            tempCol++;
            if(canMove(board,row,tempCol,color)){
                moves.add(new ChessPosition(row,tempCol));
                if(board.getPiece(new ChessPosition(row,tempCol)) != null){break;}
            }
            else{break;}
        }
        tempCol = position.getColumn();
        while(tempCol>0){
            tempCol--;
            if(canMove(board,row,tempCol,color)){
                moves.add(new ChessPosition(row,tempCol));
                if(board.getPiece(new ChessPosition(row,tempCol)) != null){break;}
            }
            else{break;}
        }
        for (ChessPosition pos : moves) {
            theMoves.add(new ChessMove(position,pos,null));
        }
        return theMoves;
    }
}

class BishopMoves implements PieceMoves {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        throw new RuntimeException("Not implemented");
    }
}

class QueenMoves implements PieceMoves {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        throw new RuntimeException("Not implemented");
    }
}
