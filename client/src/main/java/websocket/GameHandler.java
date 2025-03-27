package websocket;

//import websocket.commands.UserGameCommand;
import model.GameData;
import websocket.messages.ServerMessage;

public interface GameHandler {
    void updateGame(GameData game);
    void printMessage(ServerMessage message);

}
