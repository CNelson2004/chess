package ui;

import websocket.GameHandler;
import websocket.GameUI;
import websocket.WebsocketFacade;

import java.util.Scanner;

public class Repl{
    private final PreLoginClient preUI;
    private final PostLoginClient postUI;
    private final GameClient gameUI;

    public Repl(int port, String url){
        preUI = new PreLoginClient(port);
        postUI = new PostLoginClient(port);
        gameUI = new GameClient(port, url);
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
                        break;
                    default:
                        System.out.print(result);
                }
            } catch(ResponseException e){
                System.out.println(e.getMessage());
            }
            catch (ArrayIndexOutOfBoundsException e){
                System.out.println("Incorrect parameters");
            }
        }
    }
}
