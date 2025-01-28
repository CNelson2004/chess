package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
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
