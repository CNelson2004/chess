package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import ui.Draw;
import ui.GameClient;
import websocket.messages.*;

import static ui.EscapeSequences.*;

public class GameUI implements GameHandler{
    //Have game client hold the current board so that redraw in Game client can use it?
    ChessBoard board;
    ChessGame game;
    public GameUI(){}

    public ChessGame getGame(){return game;}
    public ChessBoard getBoard(){return board;}

    public void updateGame(ChessBoard game){
        //draws board for the client
        if(GameClient.getColor()==null){printMessage(new ErrorMessage("Error: Couldn't find player color"));}
        Draw.drawBoard(game, GameClient.getColor()); //Is this how to get the current color of the user?
    }

    public void printMessage(ServerMessage message){
        switch (message) {
            case NotificationMessage notificationMessage -> {
                System.out.println(SET_TEXT_COLOR_BLUE + notificationMessage.message + RESET_TEXT_COLOR + "\n");
            }
            case ErrorMessage errorMessage -> {
                System.out.println(SET_TEXT_COLOR_RED + errorMessage.errorMessage + RESET_TEXT_COLOR + "\n");
            }
            case LoadGameMessage loadGameMessage -> {
                game = loadGameMessage.game.game();
                board = loadGameMessage.game.game().getBoard();
                updateGame(loadGameMessage.game.game().getBoard());
            }
            case null, default -> {System.out.println(SET_TEXT_COLOR_GREEN + "Couldn't define message type" + RESET_TEXT_COLOR + "\n");}
        }
    }
}
