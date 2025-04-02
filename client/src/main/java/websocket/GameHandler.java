package websocket;

import chess.ChessBoard;
import websocket.messages.ServerMessage;

public interface GameHandler {
    void updateGame(ChessBoard game);
    void printMessage(ServerMessage message);

}
