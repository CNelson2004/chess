package ui;

import java.util.Arrays;

import requests.*;
import results.*;

public class PreLoginClient implements EvalClient {
    private final ServerFacade server;
    private String currentCMD = "";

    public PreLoginClient(int port) {server = new ServerFacade(port);}

    public String eval(String input) throws ResponseException{
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            currentCMD = cmd;
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "clear" -> clear();
                case "quit" -> "quit";
                default -> help();  //Change from default being help to "Unrecognized parameters?"
            };
        } catch (ResponseException ex) {
            if(currentCMD.equals("login")){throw new ResponseException(500,"Incorrect password");}
            else if(currentCMD.equals("register")){throw new ResponseException(500,"Username already taken");}
            else{throw ex;}
        }
    }

    public String clear() throws ResponseException{
        server.clear();
        return "Server cleared\n";
    }

    public String login(String... params) throws ResponseException {
        LoginRequest r = new LoginRequest(params[0],params[1]);
        LoginResult res = server.login(r);
        //Setting authToken for the other methods
        setVars(res.authToken(),res.username());
        return "Transitioning to main page";
    }

    public String register(String... params) throws ResponseException {
        RegisterRequest r = new RegisterRequest(params[0],params[1],params[2]);
        RegisterResult res = server.register(r);
        setVars(res.authToken(),res.username());
        return "Transitioning to main page";
    }

    private void setVars(String authToken,String username){
        PostLoginClient.setToken(authToken);
        PostLoginClient.setUsername(username);
        GameClient.setUsername(username);
    }

    public String help(){
        return """
                * register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                * login <USERNAME> <PASSWORD> - to play chess
                * help - with commands
                * quit - to exit
                """;
    }

}
