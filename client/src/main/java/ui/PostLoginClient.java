package ui;

import model.GameData;
import requests.*;
import results.*;

import java.util.Arrays;
import java.util.HashMap;

public class PostLoginClient implements EvalClient {
    private final ServerFacade server;
    protected static String token;
    protected static HashMap<Integer,Integer> gameIndexes = new HashMap<Integer,Integer>();
    private String currentCMD = "";

    public PostLoginClient(int port) {
        server = new ServerFacade(port);
    }

    public static void setToken(String value){token = value;}

    public static String getToken(){return token;}

    public String eval(String input) throws ResponseException{
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            currentCMD = cmd;
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
            if(currentCMD.equals("create")){throw new ResponseException(500,"Game name already taken");}
            if(currentCMD.equals("join")){throw new ResponseException(500,"Unrecognized color");}
            else{return ex.getMessage();}
        }
    }

    public String create(String... params) throws ResponseException {
        CreateRequest r = new CreateRequest(params[0],token);
        CreateResult res = server.create(r);
        return "Your game has been created\n";
    }

    public String list() throws ResponseException {
        ListRequest r = new ListRequest(token);
        ListResult res = server.list(r);
        //return list of all the games (w/ index number next to game name next to current player names (null if color not taken)
        StringBuilder allGames = new StringBuilder();
        gameIndexes.clear();
        if(res.games().isEmpty()){System.out.println("No current games");}
        int i=1;
        for(GameData game : res.games()){
            allGames.append(String.format("%d-%s, White: %s, Black: %s\n",i,game.gameName(),game.whiteUsername(),game.blackUsername()));
            gameIndexes.put(i,game.gameID());
            i++;
        }
        return allGames.toString();
    }

    public String join(String... params) throws ResponseException {
        //int gameID = gameIndexes.get(params[0]); -> JoinRequest r = new JoinRequest(params[1],gameID,token); -> server.join(r);
        // -> GameClient.setId(params[0]); -> GameClient.setColor(params[1]);
        if(params[1].equalsIgnoreCase("WHITE")){GameClient.color = "WHITE";}
        else if(params[1].equalsIgnoreCase("BLACK")){GameClient.color = "BLACK";}
        else{throw new ResponseException(500,"Unrecognized color");}
        return "Transitioning to game page";
    }

    public String observe(String... params) throws ResponseException {
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
