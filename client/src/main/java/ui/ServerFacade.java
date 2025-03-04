package ui;

import requests.*;
import results.*;

public class ServerFacade {
    public ServerFacade(int port){

    }
    //Review Client HTTP video
    public RegisterResult register(RegisterRequest r){
        return null;
        //Creates HTTP request and sends it to the server
        //Receives the result and processes it
    }

    public LoginResult login(LoginRequest r){
        return null;
    }

    public LogoutResult logout(LogoutRequest r){
        return null;
    }

    public CreateResult create(CreateRequest r){
        return null;
    }

    public ListResult list(ListRequest r){
        return null;
    }

    public JoinResult join(JoinRequest r){
        return null;
    }

    public ClearResult clear(){
        return null;
    }

    private <T> T makeRequest(String method, String path, Object Request, Class<T> responseClass){
        return null;
    }
}
