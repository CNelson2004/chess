package websocket;

import chess.ChessGame;
import chess.ChessMove;
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

    public WebsocketHandler(AuthDao aDao, GameDao gDao){
        this.aDao = aDao;
        this.gDao = gDao;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message)throws DataAccessException, IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        //Getting special info if command is MakeMove
        ChessMove move = null;
        if(command.getCommandType()==MAKE_MOVE){
            MakeMoveCommand temp = new Gson().fromJson(message, MakeMoveCommand.class);
             move = temp.getMove();
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
                    case CONNECT -> connect(session, username, command.getGameID(), color, gDao);
                    case MAKE_MOVE -> makeMove(session, gDao, command.getGameID(), username, move, color);
                    case LEAVE -> leave(session, username, command.getGameID(), gDao, color);
                    case RESIGN -> resign(session, command.getGameID(), gDao, username, color);
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

    private GameData getGame(GameDao gDao, int gameID) throws DataAccessException {return gDao.getGame(gameID);}

    private void connect(Session ses, String name, int gameID, String color, GameDao gDao) throws IOException, DataAccessException {
        GameData game = getGame(gDao,gameID);
        sessions.add(gameID,ses);
        //send Load Game message to original client
        send(ses,new LoadGameMessage(game));
        //send message to rest of clients
        String message;
        if(color != null){message = String.format("%s has entered the game as %s", name, color);}
        else{message = String.format("%s has entered the game as an observer", name);}
        broadcast(ses,gameID,new NotificationMessage(message)); //broadcasts to all but user
    }

    private void makeMove(Session ses, GameDao gDao, int gameID, String name, ChessMove move, String color) throws IOException, DataAccessException {
        GameData game = getGame(gDao,gameID);
        chess.ChessGame.TeamColor theColor = null;
        //check if you are an observer
        if(!Objects.equals(name, game.whiteUsername()) && !Objects.equals(name, game.blackUsername())){
            send(ses, new ErrorMessage("Error: You are an observer, you cannot move pieces"));}
        //If came is complete, then no more moves can be made
        else if(game.game().hasGameEnded()){send(ses, new ErrorMessage("Error: Game Over-No More Moves"));}
        else if(move==null){send(ses, new ErrorMessage("Error:Null move"));} //verify validity of move
        else {
            if (color.equalsIgnoreCase("white")) {
                theColor = chess.ChessGame.TeamColor.WHITE;
            } else{theColor = chess.ChessGame.TeamColor.BLACK;}
            //Checking if game is in checkmate or stalemate, and if so, make it complete
            if(game.game().isInCheckmate(theColor)||game.game().isInStalemate(theColor)){
                game.game().setGameEnded(true);
                send(ses, new ErrorMessage("Error: Game Over-No More Moves"));
            }
            //Check move turn
            else if(theColor != game.game().getTeamTurn()){send(ses, new ErrorMessage("Error: Not your turn"));}
            //Check if you are moving your piece
            else if(theColor != game.game().getBoard().getPiece(move.getStartPosition()).getTeamColor()){
                send(ses, new ErrorMessage("Error: Not your piece"));
            }
            else {
                //update game to represent move
                try {
                    //updating game in database
                    game.game().makeMove(move);
                    gDao.updateGame(game,color,name);
                    //send LoadGame message to all clients in game(including root)
                    broadcast(null, gameID, new LoadGameMessage(game));
                    //send notification to all other clients in game
                    String start = convertMove(move.getStartPosition().getRow(),move.getStartPosition().getColumn());
                    String end = convertMove(move.getEndPosition().getRow(),move.getEndPosition().getColumn());
                    broadcast(ses, gameID, new NotificationMessage(String.format("%s has moved from %s to %s", name, start, end)));
                } catch (InvalidMoveException e) {
                    send(ses, new ErrorMessage("Error: Invalid move"));
                }
                //If move results in check, send notification message to all clients (including root)
                if (game.game().isInCheck(ChessGame.TeamColor.valueOf("WHITE"))) {
                    broadcast(null, gameID, new NotificationMessage(String.format("%s is in check", color)));
                }
                if (game.game().isInCheck(ChessGame.TeamColor.valueOf("BLACK"))) {
                    broadcast(null, gameID, new NotificationMessage(String.format("%s is in check", color)));
                }
            }
        }
    }

    private String convertMove(int row, int col){
        String theRow = String.valueOf(row);
        String theCol = letterConverter(col);
        return String.format(theCol+theRow);
    }

    private String letterConverter(int col){
            switch (col) {
                case 1 -> {return "a";}
                case 2 -> {return "b";}
                case 3 -> {return "c";}
                case 4 -> {return "d";}
                case 5 -> {return "e";}
                case 6 -> {return "f";}
                case 7 -> {return "g";}
                case 8 -> {return "h";}
                default -> {return "z";} //Error
            }
    }

    private void leave(Session session, String username, int gameID, GameDao gDao, String color) throws IOException, DataAccessException {
        GameData game = getGame(gDao,gameID);
        //update game to remove the client
        if(color != null){gDao.updateGame(game,color,null);}
        sessions.remove(session);
        //Tell other clients that original client left
        String message = String.format("%s has left the game",username);
        broadcast(session,gameID,new NotificationMessage(message));
    }

    private void resign(Session ses, int gameID, GameDao gDao, String name, String color) throws IOException, DataAccessException {
        GameData game = getGame(gDao,gameID);
        //Check if session is an observer, if so, they cannot resign
        if(!Objects.equals(game.blackUsername(), name) && !Objects.equals(game.whiteUsername(), name)){
            send(ses, new ErrorMessage("Error: Observer cannot resign")); //Change to notification message?
        }else if (game.game().hasGameEnded()) { //If one person resigned(WHITE), the other person cannot resign(BLACK)
                send(ses, new ErrorMessage("Error: Opponent already resigned"));}
        else{
            //mark the game as over (no more moves can be made) & put it in Dao
            game.game().setGameEnded(true);
            gDao.updateGame(game,color,name);
            //Tell all clients original client has resigned
            String message = String.format("%s has resigned", name);
            broadcast(null, gameID, new NotificationMessage(message));
        }
    }

    public void broadcast(Session excludeUser, int gameID, ServerMessage message) throws IOException {
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
