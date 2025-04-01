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
import websocket.commands.UserGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

public class WebsocketFacade extends Endpoint implements MessageHandler.Whole<String>{

    GameUI gameHandler;
    Session session;
    ChessGame game;

    public WebsocketFacade(String url, GameUI gameHandler){
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.gameHandler = gameHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler (Should this be set inside or outside
            //this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                //@Override
                //public void onMessage(String message) {
                    //deserialize message (Is it userCommand or Servermessage?)
                    //Notification notification = new Gson().fromJson(message, Notification.class);
                    //call gameHandler to process message
                    //gameHandler.printMessage(message);
                //}
            //});
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            gameHandler.printMessage(new ErrorMessage("Error: Couldn't create websocket"));
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
    //public void onClose(Session session, EndpointConfig endpointConfig) {}
    //public void onError(Session session, EndpointConfig endpointConfig) {}

    @Override
    public void onMessage(String message) {
        //message is a ServerMessage
        ServerMessage mes = new Gson().fromJson(message, ServerMessage.class);
        if(mes instanceof LoadGameMessage){game = ((LoadGameMessage) mes).game.game();}
        //call gameHandler to process message
        gameHandler.printMessage(mes);
    }

    public GameHandler getHandler(){return gameHandler;}
    public ChessBoard getBoard(){return gameHandler.getBoard();}
    public ChessGame getGame(){return gameHandler.getGame();}

    //private sendMessage(){}

    public void connect(String authToken, int gameID) throws ResponseException {
        try{
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT,authToken,gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
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
