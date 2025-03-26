package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage{
    GameData game;

    public LoadGameMessage(GameData game) { //Could change to chessgame instead of GameData?
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
}
