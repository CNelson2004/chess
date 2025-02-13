package server;

import requests.*;
import results.*;
import dataaccess.*;
import service.*;
import spark.*;
import com.google.gson.Gson;

public class Server {

    //In constructor for server initizlize DAOs
    MemoryUserDao uDao;
    MemoryAuthDao aDao;
    MemoryGameDao gDao;
    public Server(){
        uDao = new MemoryUserDao();
        aDao = new MemoryAuthDao();
        gDao = new MemoryGameDao();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort); //8080

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register); //Email of user must have @ and .com in it.
        Spark.post("/session", this::login);    //Problem with getting authToken from req!!!
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::list);
        Spark.post("/game", this::create);
        Spark.put("/game", this::join);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object addToken(Object request, String token){
        //Takes a Request without an auth token and adds it
        if(request instanceof JoinRequest){
            return new JoinRequest(((JoinRequest) request).playerColor(),((JoinRequest) request).gameID(),token);
        }
        else if(request instanceof CreateRequest){
            return new CreateRequest(((CreateRequest) request).gameName(),token);
        }
        else{
            return null;
        }
    }

    private Object clear(Request req,  Response res){
        ClearService serv = new ClearService();
        try {
            ClearResult result = serv.clear(uDao, aDao, gDao);
            res.type("application/json");
            return new Gson().toJson(result);
        }
        catch(DaoException e){
            res.status(500);
            return new Gson().toJson(new ExceptionHandler("Error: Database is null")); //500
        }
    }

    private Object register(Request req,  Response res){
        UserService serv = new UserService();
        RegisterRequest r = new Gson().fromJson(req.body(),RegisterRequest.class);
        try {
            RegisterResult result = serv.register(r, uDao, aDao);
            res.type("application/json");
            return new Gson().toJson(result); //200
        } catch(InputException e){
            res.status(400);
            return new Gson().toJson(new ExceptionHandler("Error: bad request")); //400
        } catch(DuplicateException e){
            res.status(403);
            return new Gson().toJson(new ExceptionHandler("Error: already taken")); //403
        } catch(DaoException e){
            res.status(500);
            return new Gson().toJson(new ExceptionHandler("Error: Database is null")); //500
        } catch(DataAccessException e){
            res.status(500);
            return new Gson().toJson(new ExceptionHandler("Error: Database Data was not found")); //500 Error
        }
    }

    private Object login(Request req,  Response res){
        UserService serv = new UserService();
        LoginRequest r = new Gson().fromJson(req.body(),LoginRequest.class);
        try{
            LoginResult result = serv.login(r,uDao,aDao);
            res.type("application/json");
            return new Gson().toJson(result); //200
        } catch(AuthorizationException e){
            res.status(401);
            return new Gson().toJson(new ExceptionHandler("Error: unauthorized")); //401
        } catch(DaoException e){
            res.status(500);
            return new Gson().toJson(new ExceptionHandler("Error: Database is null")); //500
        } catch(InputException e){
            res.status(400);
            return new Gson().toJson(new ExceptionHandler("Error: bad request")); //500 (Technically 400)
        } catch(DataAccessException e){
            res.status(500);
            return new Gson().toJson(new ExceptionHandler("Error: Database Data was not found")); //500 Error
        }
    }

    private Object logout(Request req,  Response res){
        UserService serv = new UserService();
        LogoutRequest r = new LogoutRequest(req.headers("Authorization"));
        try{
            LogoutResult result = serv.logout(r,aDao);
            res.type("application/json");
            return new Gson().toJson(result); //200
        } catch(AuthorizationException e){
            res.status(401);
            return new Gson().toJson(new ExceptionHandler("Error: unauthorized")); //401
        } catch(DaoException e){
            res.status(500);
            return new Gson().toJson(new ExceptionHandler("Error: Database is null")); //500
        } catch(DataAccessException e){
            res.status(500);
            return new Gson().toJson(new ExceptionHandler("Error: Database Data was not found")); //500 Error
        }
    }

    private Object list(Request req,  Response res){
        GameService serv = new GameService();
        ListRequest r = new ListRequest(req.headers("Authorization"));
        try {
            ListResult result = serv.list(r, aDao, gDao);
            res.type("application/json");
            return new Gson().toJson(result); //200
        } catch(AuthorizationException e){
            res.status(401);
            return new Gson().toJson(new ExceptionHandler("Error: unauthorized")); //401
        } catch(DaoException e){
            res.status(500);
            return new Gson().toJson(new ExceptionHandler("Error: Database is null")); //500
        }
    }

    private Object create(Request req,  Response res){
        GameService serv = new GameService();
        CreateRequest r = new Gson().fromJson(req.body(),CreateRequest.class);
        r = (CreateRequest) addToken(r,req.headers("Authorization"));
        try{
            CreateResult result = serv.create(r,aDao,gDao);
            res.type("application/json");
            return new Gson().toJson(result); //200
        } catch(InputException e){
            res.status(400);
            return new Gson().toJson(new ExceptionHandler("Error: bad request")); //400
        } catch(AuthorizationException e){
            res.status(401);
            return new Gson().toJson(new ExceptionHandler("Error: unauthorized")); //401
        } catch(DaoException e){
            res.status(500);
            return new Gson().toJson(new ExceptionHandler("Error: Database is null")); //500
        } catch(DataAccessException e) {
            res.status(500);
            return new Gson().toJson(new ExceptionHandler("Error: Database Data was not found")); //500 Error
        }
    }

    private Object join(Request req,  Response res){
        GameService serv = new GameService();
        JoinRequest r = new Gson().fromJson(req.body(),JoinRequest.class);
        r = (JoinRequest) addToken(r,req.headers("Authorization"));
        try{
            JoinResult result = serv.join(r,aDao,gDao);
            res.type("application/json");
            return new Gson().toJson(result); //200
        } catch(InputException e){
            res.status(400);
            return new Gson().toJson(new ExceptionHandler("Error: bad request")); //400
        } catch(AuthorizationException e){
            res.status(401);
            return new Gson().toJson(new ExceptionHandler("Error: unauthorized")); //401
        } catch(DuplicateException e){
            res.status(403);
            return new Gson().toJson(new ExceptionHandler("Error: already taken")); //403
        } catch(DaoException e){
            res.status(500);
            return new Gson().toJson(new ExceptionHandler("Error: Database is null")); //500
        } catch(DataAccessException e) {
            res.status(500);
            return new Gson().toJson(new ExceptionHandler("Error: Database Data was not found")); //500 Error
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    //server needs to initialize HTTP Handlers
//then just listen for incoming requests from clients
//When it hears a request:
//Finds out what type of request it is, which function it is calling
//Forward it to the appropriate handler
//Then the handler:
//Takes the json and (Ex: logout) makes a LogoutRequest object and gives it to logout in the Service classes
//When it receives the result back it:
//converts it back into an HTTP response (JSON?)
//and then sends that back to the client

//Handlers: One for each of the 7 functions: Register, login, logout, create, join, list, and clear
//Note: Can create base classes for Handlers, Services, Requests, Responses, DAOs to avoid code duplication
//Note: When making JSON object leave the areas null that you do'nt need, (like the message slot if successful).
}
