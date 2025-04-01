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
import java.util.Objects;

import static websocket.commands.UserGameCommand.CommandType.*;

@WebSocket
public class WebsocketHandler {
    private final SessionManager sessions = new SessionManager();
    AuthDao aDao;
    GameDao gDao;
    //static ChessGame currentGame;

    public WebsocketHandler(AuthDao aDao, GameDao gDao){
        this.aDao = aDao;
        this.gDao = gDao;
    }

    //public static ChessGame getGame(){return currentGame;}

    @OnWebSocketMessage
    public void onMessage(Session session, String message)throws DataAccessException, IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        //Getting special info if command is MakeMove
        ChessMove move = null;
        if(command.getCommandType()==MAKE_MOVE){
            MakeMoveCommand temp = new Gson().fromJson(message, MakeMoveCommand.class);
             move = temp.getMove();
             //ChessPosition startMove = tempMove.getStartPosition(); //rows are correct, but col is -1 from input
             //ChessPosition endMove = tempMove.getEndPosition();
             //move = new ChessMove(new ChessPosition(startMove.getRow(),startMove.getColumn()+1), new ChessPosition(endMove.getRow(),endMove.getColumn()+1),tempMove.getPromotionPiece());
        }
        //deciding what to do with message
        try {
            //getting info
            String username = aDao.getAuth(command.getAuthToken()).username();
            GameData game = gDao.getGame(command.getGameID());
            //Making sure the game & authToken exists
            if(username == null){send(session,new ErrorMessage("Error:Authtoken is invalid"));}
            else if(game == null){send(session,new ErrorMessage("Error:Game is unrecognized"));}
            else {
                //getting color info
                String color = null;
                if(game.blackUsername()!=null){if(game.blackUsername().equals(username)){color = "BLACK";}}
                if(game.whiteUsername()!=null){if(game.whiteUsername().equals(username)){color = "WHITE";}}
                switch (command.getCommandType()) {
                    case CONNECT -> connect(session, username, command.getGameID(), color, gDao, aDao);
                    case MAKE_MOVE -> makeMove(session, gDao, command.getGameID(), username, move, color);
                    case LEAVE -> leave(session, username, command.getGameID(), gDao, color);
                    case RESIGN -> resign(session, command.getGameID(), gDao, username);
                    default -> System.out.println("Cannot find command type");
                }
            }
        }catch (RuntimeException e){
            send(session, new ErrorMessage("Error: Bad information"));
        }catch (DataAccessException e){
            send(session, new ErrorMessage("Error: Bad information, info not in Database"));
        }
        catch(IOException e){
            send(session, new ErrorMessage("Error: Couldn't send data"));
        }
    }

    private void connect(Session session, String username, int gameID, String color, GameDao gDao, AuthDao aDao) throws IOException, DataAccessException {
        //send Load Game message to original client
        sessions.add(gameID,session);
        //send load game message
        send(session,new LoadGameMessage(gDao.getGame(gameID)));
        //send message to rest of clients
        String message;
        if(color != null){message = String.format("%s has entered the game as %s", username, color);}
        else{message = String.format("%s has entered the game as an observer", username);}
        broadcast(session,gameID,new NotificationMessage(message)); //broadcasts to all but user
    }

    private void makeMove(Session session, GameDao gDao, int gameID, String username, ChessMove move, String color) throws IOException, DataAccessException {
        chess.ChessGame.TeamColor theColor = null;
        //check if you are an observer
        if(!Objects.equals(username, gDao.getGame(gameID).whiteUsername()) && !Objects.equals(username, gDao.getGame(gameID).blackUsername())){
            send(session, new ErrorMessage("Error: You are an observer, you cannot move pieces"));}
        //If came is complete, then no more moves can be made
        else if(gDao.getGame(gameID).game().hasGameEnded()){send(session, new ErrorMessage("Error: Game Over-No More Moves"));}
        else if(move==null){send(session, new ErrorMessage("Error:Null move"));} //verify validity of move
        else {
            if (color.equalsIgnoreCase("white")) {
                theColor = chess.ChessGame.TeamColor.WHITE;
            } else{theColor = chess.ChessGame.TeamColor.BLACK;}
            //Checking if game is in checkmate or stalemate, and if so, make it complete
            if(gDao.getGame(gameID).game().isInCheckmate(theColor)||gDao.getGame(gameID).game().isInStalemate(theColor)){
                gDao.getGame(gameID).game().setGameEnded(true);
                send(session, new ErrorMessage("Error: Game Over-No More Moves"));
            }
            //Check move turn
            else if(theColor != gDao.getGame(gameID).game().getTeamTurn()){send(session, new ErrorMessage("Error: Not your turn"));}
            //Check if you are moving your piece
            else if(theColor != gDao.getGame(gameID).game().getBoard().getPiece(move.getStartPosition()).getTeamColor()){
                send(session, new ErrorMessage("Error: Not your piece"));
            }
            else {
                //update game to represent move
                try {
                    gDao.getGame(gameID).game().makeMove(move);
                    //^In debugging will say it ran into a NullPointerException in GetAllPieces, but it is accounted for
                    //send LoadGame message to all clients in game(including root)
                    broadcast(null, gameID, new LoadGameMessage(gDao.getGame(gameID)));
                    //send notification to all other clients in game
                    broadcast(session, gameID, new NotificationMessage(String.format("%s has moved from %s to %s", username, move.getStartPosition().toString(), move.getEndPosition().toString())));
                } catch (InvalidMoveException e) {
                    send(session, new ErrorMessage("Error: Invalid move"));
                }
                //If move results in check, send notification message to all clients (including root)
                if (gDao.getGame(gameID).game().isInCheck(ChessGame.TeamColor.valueOf(color))) {//Check if color needs to be changed in a different way
                    broadcast(null, gameID, new NotificationMessage(String.format("%s is in check", color)));
                }
            }
        }
    }

    private void leave(Session session, String username, int gameID, GameDao gDao, String color) throws IOException, DataAccessException {
        //update game to remove the client (Give color that client is)
        if(color != null){gDao.updateGame(gDao.getGame(gameID),color,null);}
        sessions.remove(session);
        //Tell other clients that original client left
        String message = String.format("%s has left the game",username);
        broadcast(session,gameID,new NotificationMessage(message));
    }

    private void resign(Session session, int gameID, GameDao gDao, String username) throws IOException, DataAccessException {
        //Check if session is an observer, if so, they cannot resign
        if(!Objects.equals(gDao.getGame(gameID).blackUsername(), username) && !Objects.equals(gDao.getGame(gameID).whiteUsername(), username)){
            send(session, new ErrorMessage("Error: Observer cannot resign")); //Change to notification message?
        }else if (gDao.getGame(gameID).game().hasGameEnded()) { //If one person resigned(WHITE), the other person cannot resign(BLACK)
                send(session, new ErrorMessage("Error: Opponent already resigned"));}
        else{
            //mark the game as over (no more moves can be made) & update game in database(Change database code or chess code to have a flag to make it so the game can no longer be updated)
            gDao.getGame(gameID).game().setGameEnded(true);
            //Tell all clients original client has resigned
            String message = String.format("%s has resigned", username);
            broadcast(null, gameID, new NotificationMessage(message));
        }
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
        session.getRemote().sendString(new Gson().toJson(msg)); //turns the message into a JSON
    }
}
