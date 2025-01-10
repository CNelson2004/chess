package chess;

import java.util.ArrayList;
import java.util.Collection;

interface PieceMoves {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
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
    public static boolean canCapture(ChessBoard board, int row, int col, ChessGame.TeamColor color){
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
         ChessPiece piece = board.getPiece(position);
         ChessGame.TeamColor color = piece.getTeamColor();

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
                 if(PawnMoves.canCapture(board, position.getRow()+1, position.getColumn()-1, ChessGame.TeamColor.WHITE)) {
                     moves.add(new ChessPosition(position.getRow()+1, position.getColumn()-1));}
                 if(PawnMoves.canCapture(board, position.getRow()+1, position.getColumn()+1, ChessGame.TeamColor.WHITE)) {
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
                 if(PawnMoves.canCapture(board, position.getRow()-1, position.getColumn()-1, ChessGame.TeamColor.BLACK)) {
                     moves.add(new ChessPosition(position.getRow()-1, position.getColumn()-1));}
                 if(PawnMoves.canCapture(board, position.getRow()-1, position.getColumn()+1, ChessGame.TeamColor.BLACK)) {
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
        throw new RuntimeException("Not implemented");
    }
}

class KnightMoves implements PieceMoves {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        throw new RuntimeException("Not implemented");
    }
}

class RookMoves implements PieceMoves {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        throw new RuntimeException("Not implemented");
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
