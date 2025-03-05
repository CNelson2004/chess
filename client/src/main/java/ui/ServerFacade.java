package ui;

import com.google.gson.Gson;
import requests.*;
import results.*;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(int port){
        serverUrl = String.format("http://localhost:%d",port);
    }

    //public ServerFacade(int port){}
    public RegisterResult register(RegisterRequest r) throws ResponseException {
        //Creates HTTP request and sends it to the server
        //Receives the result and processes it
        return makeRequest("POST","/user",r,RegisterResult.class);
    }

    public LoginResult login(LoginRequest r) throws ResponseException {
        return makeRequest("POST","/session",r,LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest r) throws ResponseException {
        return makeRequest("DELETE","/session",r,LogoutResult.class);
    }

    public CreateResult create(CreateRequest r) throws ResponseException {
        return makeRequest("POST","/game",r,CreateResult.class);
    }

    //double check this one
    public ListResult list(ListRequest r) throws ResponseException {
        return makeRequest("GET","/game",r,ListResult.class);
    }

    public JoinResult join(JoinRequest r) throws ResponseException {
        return makeRequest("PUT","/game",r,JoinResult.class);
    }

    public ClearResult clear() throws ResponseException {
        return makeRequest("DELETE","/db",null,ClearResult.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException e){
            throw e;
        } catch (Exception e){
            throw new ResponseException(500,e.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
