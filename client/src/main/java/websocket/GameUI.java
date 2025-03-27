package websocket;

import chess.ChessBoard;
import ui.Draw;
import ui.GameClient;
import websocket.messages.*;

import static ui.EscapeSequences.*;

public class GameUI implements GameHandler{
    //WebsocketFacade wsFacade;
    //public GameUI(WebsocketFacade wsFacade){this.wsFacade = wsFacade;}
    public GameUI(){}

    public void updateGame(ChessBoard game){
        if(GameClient.getColor()==null){printMessage(new ErrorMessage("Error: Couldn't find player color"));}
        Draw.drawBoard(game, GameClient.getColor()); //Is this how to get the current color of the user?
    }

    //public static void drawGame(ChessBoard game){}

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
                updateGame(loadGameMessage.game.game().getBoard());
            }
            case null, default -> {
            }
        }
    }


    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_BLUE); //Doubel check this
    }
}
