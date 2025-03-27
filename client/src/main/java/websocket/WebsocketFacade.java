package websocket;

import com.google.gson.Gson;
import ui.ResponseException;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketFacade extends Endpoint {

    GameHandler gameHandler;
    Session session;

    public WebsocketFacade(String url, GameHandler gameHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.gameHandler = gameHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    //deserialize message (Is it userCommand or Servermessage?)
                    //Notification notification = new Gson().fromJson(message, Notification.class);

                    //call gameHandler to process message
                    //gameHandler.printMessage(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
    public void onClose(Session session, EndpointConfig endpointConfig) {}
    public void onError(Session session, EndpointConfig endpointConfig) {}

    //private sendMessage(){}

    public String connect(){

        return null;}

    public String makeMove(){

        return null;}

    public String leave(){

        return null;}

    public String resign(){

        return null;}

}
