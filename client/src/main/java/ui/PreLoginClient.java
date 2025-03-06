package ui;

import java.util.Arrays;

import com.google.gson.Gson;
import com.sun.nio.sctp.NotificationHandler;
import requests.*;
import results.*;

public class PreLoginClient {
    private final ServerFacade server;

    public PreLoginClient(int port) {
        server = new ServerFacade(port);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        LoginRequest r = new LoginRequest(params[0],params[1]);
        LoginResult res = server.login(r);
        //transition to PostLoginUI?
        return null;
    }

    public String register(String... params) throws ResponseException {
        RegisterRequest r = new RegisterRequest(params[0],params[1],params[2]);
        RegisterResult res = server.register(r);
        //transition to PostLoginUI?
        return null;
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
