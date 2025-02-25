import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        //switching bewteen either Dao
        Server server = new Server();
        //Server server = new Server("memory");
        //Server server = new Server("sql");
        server.run(8080);
        //var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        //System.out.println("♕ 240 Chess Server: " + piece);
    }
}