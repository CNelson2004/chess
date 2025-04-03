package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import ui.ResponseException;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import websocket.commands.*;
import websocket.messages.*;

public class WebsocketFacade extends Endpoint{ //Do I need to implement this?

    GameUI gameHandler;
    Session session;
    ChessGame game;

    public WebsocketFacade(String url, GameUI gameHandler) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.gameHandler = gameHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            session.addMessageHandler(handler);

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            gameHandler.printMessage(new ErrorMessage("Error: Couldn't create websocket"));
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    MessageHandler handler = new MessageHandler.Whole<String>() {
        @Override
        public void onMessage(String message) {
            ServerMessage mes = new Gson().fromJson(message, ServerMessage.class);
            //Checking specifically which subclass it is
            if(mes.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION){
                mes = new Gson().fromJson(message, NotificationMessage.class);}
            else if(mes.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){
                mes = new Gson().fromJson(message, ErrorMessage.class);}
            else if (mes.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                mes = new Gson().fromJson(message, LoadGameMessage.class);}
            //call gameHandler to process message
            gameHandler.printMessage(mes);
        }
    };

    public ChessBoard getBoard(){return gameHandler.getBoard();}
    public ChessGame getGame(){return gameHandler.getGame();}

    //private sendMessage(){} //unused method

    public void connect(String authToken, int gameID) throws ResponseException {
        try{
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT,authToken,gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command)); //change this to make the sendMessage func do this?
        }catch (IOException e){
            throw new ResponseException(500,e.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ResponseException {
        try{
            MakeMoveCommand command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE,authToken,gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }catch (IOException e){
            throw new ResponseException(500,e.getMessage());
        }
    }

    public void leave(String authToken, int gameID) throws ResponseException {
        try{
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE,authToken,gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }catch (IOException e){
            throw new ResponseException(500,e.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws ResponseException {
        try{
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN,authToken,gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        }catch (IOException e){
            throw new ResponseException(500,e.getMessage());
        }
    }

}
