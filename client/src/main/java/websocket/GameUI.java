package websocket;

import model.GameData;
import websocket.messages.*;

import static ui.EscapeSequences.*;

public class GameUI implements GameHandler{
    WebsocketFacade wsFacade;

    public GameUI(WebsocketFacade wsFacade){
        this.wsFacade = wsFacade;
    }

    public void updateGame(GameData game){

    }

    public void printMessage(ServerMessage message){
        switch (message) {
            case NotificationMessage notificationMessage -> {
                System.out.println(SET_TEXT_COLOR_GREEN + notificationMessage.message);
                printPrompt();
            }
            case ErrorMessage errorMessage -> {
                System.out.println(SET_TEXT_COLOR_RED + errorMessage.errorMessage);
                printPrompt();
            }
            case LoadGameMessage loadGameMessage -> {
                //redraws board in its new state

            }
            case null, default -> {
            }
        }
    }


    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_BLUE); //Doubel check this
    }
}
