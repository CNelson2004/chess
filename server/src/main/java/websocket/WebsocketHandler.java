package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.messages.*;

import java.io.IOException;
import java.util.ArrayList;

import static websocket.commands.UserGameCommand.CommandType.*;

@WebSocket
public class WebsocketHandler {
    private final SessionManager sessions = new SessionManager();
    AuthDao aDao;
    GameDao gDao;

    public WebsocketHandler(AuthDao aDao, GameDao gDao){
        this.aDao = aDao;
        this.gDao = gDao;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message)throws DataAccessException, IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        ChessMove move = null;
        if(command.getCommandType()==MAKE_MOVE){
            MakeMoveCommand temp = new Gson().fromJson(message, MakeMoveCommand.class);
             ChessMove tempMove = temp.getMove(); //rows are correct, but col is -1 from input
             ChessPosition startMove = tempMove.getStartPosition();
             ChessPosition endMove = tempMove.getEndPosition();
             move = new ChessMove(new ChessPosition(startMove.getRow(),startMove.getColumn()+1), new ChessPosition(endMove.getRow(),endMove.getColumn()+1),tempMove.getPromotionPiece());
        }
        //Check if authToken or gameID in command doesn't exist. If so send an error message to Root Client
        try {
            aDao.getAuth(command.getAuthToken());
        } catch(DataAccessException e){
            send(session,new ErrorMessage("Error:Authtoken is invalid"));
        }
        try {
            gDao.getGame(command.getGameID());
        } catch(DataAccessException e){
            send(session,new ErrorMessage("Error:Game is unrecognized"));
        }
        //getting info from the message
        String username = aDao.getAuth(command.getAuthToken()).username();
        GameData game = gDao.getGame(command.getGameID());
        String color = null;
        if(game.blackUsername()!=null){if(game.blackUsername().equals(username)){color = "BLACK";}}
        if(game.whiteUsername()!=null){if(game.whiteUsername().equals(username)){color = "WHITE";}}
        //deciding what to do with message
        try {
            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, command.getGameID(), color, gDao);
                case MAKE_MOVE -> makeMove(session, gDao, command.getGameID(), username, move, color);
                case LEAVE -> leave(session, username, command.getGameID(), gDao, color);
                case RESIGN -> resign(session, command.getGameID(), gDao, username);
                default -> System.out.println("Cannot find command type");
            }
        }catch (DataAccessException e){
            send(session, new ErrorMessage("Error: Couldn't reach database"));
        }
        catch(IOException e){
            send(session, new ErrorMessage("Error: Couldn't send data"));
        }
    }

    private void connect(Session session, String username, int gameID, String color, GameDao gDao) throws IOException, DataAccessException {
        //Join game should be called before this is called, so color will be defined
        //send Load Game message to original client
        sessions.add(gameID,session);
        send(session,new LoadGameMessage(gDao.getGame(gameID)));
        //send message to rest of clients
        String message;
        if(color != null){message = String.format("%s has entered the game as %s", username, color);}
        else{message = String.format("%s has entered the game as an observer", username);}
        broadcast(session,gameID,new NotificationMessage(message)); //broadcasts to all but user
    }

    private void makeMove(Session session, GameDao gDao, int gameID, String username, ChessMove move, String color) throws IOException, DataAccessException {
        //Checking if game is in checkmate or stalemate, and if so, make it complete
        chess.ChessGame.TeamColor theColor = null;
        if(color.equalsIgnoreCase("white")){theColor = chess.ChessGame.TeamColor.WHITE;}
        else{theColor = chess.ChessGame.TeamColor.BLACK;}
        if(gDao.getGame(gameID).game().isInCheckmate(theColor)||gDao.getGame(gameID).game().isInStalemate(theColor)){
            gDao.getGame(gameID).game().setGameEnded(true);
            send(session, new ErrorMessage("Error: Game Over-No More Moves"));
        }
        //verify validity of move
        if(move==null){send(session, new ErrorMessage("Error:Null move"));}
        //Collection<ChessMove> validMoves = gDao.getGame(gameID).game().validMoves(move.getStartPosition());
        //if(!validMoves.contains(move)){send(session, new ErrorMessage("Error: Invalid move"));} //<-Redundant?
        //update game to represent move
        try {
            gDao.getGame(gameID).game().makeMove(move);
            //^In debugging will say it ran into a NullPointerException in GetAllPieces, but it is accounted for
        } catch(InvalidMoveException e) {
            send(session, new ErrorMessage("Error: Invalid move"));
        }
        //send LoadGame message to all clients in game(including root)
        broadcast(null,gameID,new LoadGameMessage(gDao.getGame(gameID)));
        //send notification to all other clients in game
        broadcast(session,gameID,new NotificationMessage(String.format("%s has moved from %s to %s",username,move.getStartPosition().toString(),move.getEndPosition().toString())));
        //If move results in check, send notification message to all clients (including root)
        if(gDao.getGame(gameID).game().isInCheck(ChessGame.TeamColor.valueOf(color))){//Check if color needs to be changed in a different way
            broadcast(null,gameID, new NotificationMessage(String.format("%s is in check",color)));
        }
    }

    private void leave(Session session, String username, int gameID, GameDao gDao, String color) throws IOException, DataAccessException {
        //update game to remove the client
        gDao.updateGame(gDao.getGame(gameID),color,null);
        sessions.remove(session);
        //Tell other clients that original client left
        String message = String.format("%s has left the game",username);
        broadcast(session,gameID,new NotificationMessage(message));
    }

    private void resign(Session session, int gameID, GameDao gDao, String username) throws IOException, DataAccessException {
        //mark the game as over (no more moves can be made) & update game in database
        //Change database code or chess code to have a flag to make it so the game can no longer be updated
        gDao.getGame(gameID).game().setGameEnded(true);
        //Tell all clients original client has resigned
        String message = String.format("%s has resigned",username);
        broadcast(null,gameID,new NotificationMessage(message));
    }

    public void broadcast(Session excludeUser, int gameID, ServerMessage message) throws IOException {
        //load game will draw the board
        //notificatoin and error will print
        ArrayList<Session> removeList = new ArrayList<>();
        for (Session s : sessions.getSessions(gameID)){
            if (s.isOpen()) {
                if(s!=excludeUser) {
                    send(s, message);
                }
            } else {
                removeList.add(s);
            }
        }
        for (var c : removeList) {
            sessions.remove(c);
        }
    }

    public void send(Session session, ServerMessage msg) throws IOException {
        session.getRemote().sendString(msg.toString()); //turns the message into a JSON
    }
}
