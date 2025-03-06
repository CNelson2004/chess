package ui;

//import client.websocket.NotificationHandler;
//import webSocketMessages.Notification;

import java.util.Arrays;
import java.util.Scanner;

public class Repl {
    private final PreLoginClient preUI;
    private final PostLoginClient postUI;
    private final GameClient gameUI;

    public Repl(int port){
        preUI = new PreLoginClient(port);
        postUI = new PostLoginClient(port);
        gameUI = new GameClient(port);
    }

    public void run(){}
}
