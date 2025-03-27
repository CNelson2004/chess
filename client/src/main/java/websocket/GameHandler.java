package websocket;

//import websocket.commands.UserGameCommand;
import chess.ChessBoard;
import model.GameData;
import websocket.messages.ServerMessage;

public interface GameHandler {
    void updateGame(ChessBoard game);
    void printMessage(ServerMessage message);

}
