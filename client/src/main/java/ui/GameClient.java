package ui;

import java.util.Arrays;

public class GameClient implements EvalClient {
    private final ServerFacade server;
    protected static String id;

    public GameClient(int port) {
        server = new ServerFacade(port);
    }

    public static void setId(String value){id = value;}
    //When a player joins the game draw the board from the black or white perspective

    public String eval(String input) throws ResponseException{
        return null;
    }

    public String help(){
        return null;
    }
}
