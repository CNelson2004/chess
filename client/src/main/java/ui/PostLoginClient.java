package ui;

import model.GameData;
import requests.*;
import results.*;

import java.util.Arrays;
import java.util.HashMap;

public class PostLoginClient implements EvalClient {
    private final ServerFacade server;
    protected static String token;
    protected static HashMap<Integer,Integer> gameIndexes = new HashMap<Integer,Integer>();;

    public PostLoginClient(int port) {
        server = new ServerFacade(port);
    }

    public static void setToken(String value){token = value;}

    public static String getToken(){return token;}

    public String eval(String input) throws ResponseException{
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String create(String... params) throws ResponseException {
        //get authentication token from the user using eval?
        CreateRequest r = new CreateRequest(params[0],token);
        CreateResult res = server.create(r);
        //return string saying you created the game
        return String.format("Your game has been created with ID:%d\n", res.gameID());
    }

    public String list() throws ResponseException {
        ListRequest r = new ListRequest(token);
        ListResult res = server.list(r);
        //return list of all the games (w/ index number next to game name next to current player names (null if color not taken)
        StringBuilder allGames = new StringBuilder();
        gameIndexes.clear();
        int i=1;
        for(GameData game : res.games()){
            allGames.append(String.format("%d-%s, White: %s, Black: %s\n",i,game.gameName(),game.whiteUsername(),game.blackUsername()));
            gameIndexes.put(i,game.gameID());
            i++;
        }
        return allGames.toString();
    }

    public String join(String... params) throws ResponseException {
        //int gameID = gameIndexes.get(params[0]);
//        JoinRequest r = new JoinRequest(params[1],gameID,token);
//        server.join(r);
//        GameClient.setId(params[0]);
//        GameClient.setColor(params[1]);
        if(params[1].equalsIgnoreCase("WHITE")){GameClient.color = "WHITE";}
        else if(params[1].equalsIgnoreCase("BLACK")){GameClient.color = "BLACK";}
        else{System.out.print("Sorry, player color lost.\n");}
        return "Transitioning to game page";
    }

    public String observe(String... params) throws ResponseException {
        //Join game except you switch to game you can't make moves
//        JoinRequest r = new JoinRequest("WHITE",Integer.parseInt(params[0]),token);
//        server.join(r);
//        GameClient.setId(params[0]);
//        GameClient.setColor(params[1]);
        GameClient.color = "WHITE";
        return "Transitioning to game page";
    }

    public String logout(String... params) throws ResponseException {
        LogoutRequest r = new LogoutRequest(token);
        server.logout(r);
        return "Transitioning to login page";
    }

    public String help(){
        return """
                * create <NAME> - create a game
                * list - list all current games
                * join <ID> [BLACK|WHITE] - Join a game as black or white
                * observe <ID> - observe a game
                * logout - leave chess
                * help - show all commands
                """;
    }
}
