package ui;

import chess.*;
import websocket.GameHandler;
import websocket.GameUI;
import websocket.WebsocketFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class GameClient implements EvalClient {
    private final ServerFacade server;
    private WebsocketFacade wsFacade;
    GameHandler gameHandler;
    protected static String color;
    protected static String username;
    protected static int gameID;
    protected static String token;
    private String currentCMD = "";
    private String url;
    private boolean confirmResign = false;
    private boolean first = true;

    public GameClient(int port, String url) {
        server = new ServerFacade(port);
        this.url = url;
        wsFacade = new WebsocketFacade(url, new GameUI());
    }

    public static void setColor(String value){color = value;}
    public static String getColor(){return color;}
    public static void setUsername(String value){username = value;}
    public static String getUsername(){return username;}
    public static void setGameID(int value){gameID = value;}
    public static int getGameID(){return gameID;}
    public static void setToken(String value){token = value;}
    public static String getToken(){return token;}

    public String eval(String input) throws ResponseException{
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "quit" -> "quit";
                case "redraw" -> draw();
                case "leave" -> leave();
                case "move" -> makeMove(params);
                case "resign" -> resign();
                case "highlight" -> highlightMoves(params);
                default -> help();
            };
        }catch(ResponseException e){
            throw new ResponseException(500,"Game failure");
        }
    }

    public void initial() throws ResponseException {
        try {
            wsFacade.connect(token, gameID);
        } catch(ResponseException e){
        throw new ResponseException(500,"Game failure");
    }
    }

    public String draw(){
        Draw.drawBoard(wsFacade.getBoard(),color);
        return "";
    }

    public String leave() throws ResponseException {
        wsFacade.leave(token,gameID);
        wsFacade = null;
        return "Transitioning to main page";
    }

    public String makeMove(String... params) throws ResponseException {
        //User inputs what move they want to make (How does user input their move? [in what way through text?]
        //(Input row and then col you want to move to)
        ChessPosition start = new ChessPosition(Integer.parseInt(params[0]),Integer.parseInt(params[1]));
        ChessPosition end = new ChessPosition(Integer.parseInt(params[2]),Integer.parseInt(params[3]));
        //Use board to figure out promotion piece
        ChessBoard board = wsFacade.getBoard();
        ChessPiece.PieceType current = board.getPiece(start).getPieceType();
        //automatically set promotion type as queen (change to prompt user to pick promotion piece?)
        ChessPiece.PieceType prom = null;
        if(current == ChessPiece.PieceType.PAWN && (start.getRow()==1 || start.getRow()==8)){
            prom = ChessPiece.PieceType.QUEEN;
        }
        //making the move
        ChessMove move = new ChessMove(start,end,prom);
        wsFacade.makeMove(token,gameID,move);
        confirmResign = false;
        return "";}

    public String resign() throws ResponseException {
        //double checks if user wants to resign
        if(!confirmResign){
            confirmResign = true;
            return "Are you sure you want to resign?";
        }
        //resigns without making them leave the game
        wsFacade.resign(token,gameID);
        return "";
    }

    public String highlightMoves(String... params){
        //User inputs piece for which they want to highlight legal moves
        //(Getting info)
        ChessGame game = wsFacade.getGame();
        ChessBoard board = game.getBoard();
        Collection<ChessMove> allMoves = game.validMoves(new ChessPosition(Integer.parseInt(params[0]),Integer.parseInt(params[1])));
        Collection<ChessPosition> moves = new ArrayList<>();
        for(ChessMove move: allMoves){
            moves.add(move.getEndPosition());
        }
        //drawing highlighted board
        Draw.highlight(board,color,moves);
        return "";}

    public String help(){
        return """
                * help - list commands
                * redraw - redraws chess board
                * move <Starting Row> <Starting Col> <Ending Row> <Ending Col>- make a chess move
                * leave - return to main screen
                * resign - forfeit game
                * highlight <Piece Row> <Piece Col> - highlights legal moves
                """;
    }
}
