package ui;

//import client.websocket.NotificationHandler;
//import webSocketMessages.Notification;

import java.util.Arrays;
import java.util.Scanner;

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
                System.out.print(result);
                System.out.println("\n");
                switch (result) {
                    case "Transitioning to main page" -> {
                        currentUI = postUI;
                        System.out.println(currentUI.help());
                    }
                    case "Transitioning to login page" -> {
                        currentUI = preUI;
                        System.out.println(currentUI.help());
                    }
                    case "Transitioning to game page" -> {
                        currentUI = gameUI;
                        System.out.println(currentUI.help());
                    }
                }
            } catch(ResponseException e){
                String message = e.toString();
                System.out.print(message);
            }
        }
    }
}
