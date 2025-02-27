import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import server.ExceptionHandler;
import server.Server;

public class Main {
    public static void main(String[] args){
        Server server = new Server("memory");  //<- makes server with memory Daos.
        //Server server = new Server("sql");    //<- makes server with sql Daos. [Also new Server();]
        server.run(8080);
        //var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        //System.out.println("♕ 240 Chess Server: " + piece);
    }
}