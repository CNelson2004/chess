package ui;

//import client.websocket.NotificationHandler;
//import webSocketMessages.Notification;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

import java.util.Arrays;
import java.util.Scanner;

import static chess.ChessPiece.PieceType.*;
import static chess.ChessPiece.PieceType.ROOK;
import static ui.EscapeSequences.*;

public class Repl{
    private final PreLoginClient preUI;
    private final PostLoginClient postUI;
    private final GameClient gameUI;

    public Repl(int port){
        preUI = new PreLoginClient(port);
        postUI = new PostLoginClient(port);
        gameUI = new GameClient(port);
    }

    public void run(){
        //Doing the preLoginUI
        System.out.println("Welcome to Chess, sign in to start.");
        System.out.println(preUI.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        EvalClient currentUI = preUI;
        while (!result.equals("quit")){
            String line = scanner.nextLine();
            try{
                result = currentUI.eval(line);
                switch (result) {
                    case "Transitioning to main page":
                        currentUI = postUI;
                        System.out.println(currentUI.help());
                        break;
                    case "Transitioning to login page":
                        currentUI = preUI;
                        System.out.println(currentUI.help());
                        break;
                    case "Transitioning to game page":
                        currentUI = gameUI;
                        System.out.println(currentUI.help());
                        System.out.println(gameUI.draw()); //delete after phase 5 complete
                        break;
                    default:
                        System.out.print(result);
                }
            } catch(ResponseException e){
                System.out.println(e.getMessage());
            }
            catch (ArrayIndexOutOfBoundsException e){
                System.out.println("Incorrect number of parameters");
            }
        }
    }
}
