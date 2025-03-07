package ui;

import model.GameData;
import requests.*;
import results.*;

import java.util.Arrays;

public class PostLoginClient implements EvalClient {
    private final ServerFacade server;
    protected static String token;

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
        return String.format("Your game has been created with ID:%d", res.gameID());
    }

    public String list() throws ResponseException {
        ListRequest r = new ListRequest(token);
        ListResult res = server.list(r);
        //return list of all the games (w/ index number next to game name next to current player names (null if color not taken)
        StringBuilder allGames = new StringBuilder();
        for(GameData game : res.games()){
            allGames.append(String.format("Game ID: %d, Game Name: %s, White: %s, Black: %s\n",game.gameID(),game.gameName(),game.whiteUsername(),game.blackUsername()));
        }
        return allGames.toString();
    }

    public String join(String... params) throws ResponseException {
        JoinRequest r = new JoinRequest(params[1],Integer.parseInt(params[0]),token);
        server.join(r);
        GameClient.setId(params[0]);
        return "Transitioning to game page";
    }

    public String observe(String... params) throws ResponseException {
        //Join game except you switch to special client where you can't make moves?
        //You view from white's side
        JoinRequest r = new JoinRequest(params[1],Integer.parseInt(params[0]),token);
        server.join(r);
        GameClient.setId(params[0]);
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
