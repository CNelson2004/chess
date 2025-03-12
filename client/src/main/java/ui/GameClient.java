package ui;

import java.util.Arrays;

public class GameClient implements EvalClient {
    private final ServerFacade server;
    protected static String color;
    protected static String username;
    private String currentCMD = "";

    public GameClient(int port) {
        server = new ServerFacade(port);
    }

    public static void setColor(String value){color = value;}
    public static void setUsername(String value){username = value;}

    public String eval(String input) throws ResponseException{
        //try {}
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "quit" -> "quit";
                case "draw" -> draw();
                case "back" -> "Transitioning to main page";
                default -> help();
            };
        //catch{}
        //waiting for next phase
    }

    public String draw(){
        if (color.equals("WHITE")) {
            Draw.drawBoard();
        } else if (color.equals("BLACK")) {
            Draw.drawBoardBlack();
        } else {
            System.out.print("Error occurred, couldn't find player color.");
        }
        return "";
    }

    public String help(){
        return """
                * back - return to main screen
                * help - list commands
                """;
    }
}
