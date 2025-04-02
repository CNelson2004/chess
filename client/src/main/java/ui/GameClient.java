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
            currentCMD = cmd;
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
            if(currentCMD.startsWith("move") || currentCMD.startsWith("highlight")){
                System.out.println("Invalid move syntax");
                return help();                                 //make sure this works
            }else{
                //leave the game and throw an error so they can rejoin
                leave();
                throw new ResponseException(500,"Game failure");
            }
        }
    }

    public void initial() throws ResponseException {
        try {
            wsFacade = new WebsocketFacade(url, new GameUI());
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

    private int getCol(String letter){
        switch(letter){
            case "a" -> {return 1;}
            case "b" -> {return 2;}
            case "c" -> {return 3;}
            case "d" -> {return 4;}
            case "e" -> {return 5;}
            case "f" -> {return 6;}
            case "g" -> {return 7;}
            case "h" -> {return 8;}
            default -> {return -1;}
        }
    }

    private ChessPosition getPos(String pos){ //format c2 (col then row)
        int row = Integer.parseInt(Character.toString(pos.charAt(1)));
        int col = getCol(Character.toString(pos.charAt(0)));
        return new ChessPosition(row,col);
    }

    private ChessPiece.PieceType getProm(String promType){
        switch(promType){
            case "rook" -> {return ChessPiece.PieceType.ROOK;}
            case "knight" -> {return ChessPiece.PieceType.KNIGHT;}
            case "bishop" -> {return ChessPiece.PieceType.BISHOP;}
            case "queen" -> {return ChessPiece.PieceType.QUEEN;}
            default -> {return null;}
        }
    }

    public String makeMove(String... params) throws ResponseException {
        //User inputs what move they want to make (How does user input their move? [in what way through text?]
        //(Input row and then col you want to move to)
        ChessPosition start;
        ChessPosition end;
        try {
            start = getPos(params[0]);
            end = getPos(params[1]);
        } catch (Exception e){
            throw new ResponseException(500,"Wrong input");
        }
        //Use board to figure out promotion piece
        ChessBoard board = wsFacade.getBoard();
        ChessPiece.PieceType current = board.getPiece(start).getPieceType();
        //automatically set promotion type as queen (change to prompt user to pick promotion piece?)
        ChessPiece.PieceType prom = null;
        if(current == ChessPiece.PieceType.PAWN && (end.getRow()==1 || end.getRow()==8)){
            prom = getProm(params[2]);
        }
        //making the move
        ChessMove move = new ChessMove(start,end,prom);
        wsFacade.makeMove(token,gameID,move);            //make sure this promotes the pawn when needed
        confirmResign = false;
        return "";}

    public String resign() throws ResponseException {
        //double checks if user wants to resign
        if(!confirmResign){
            confirmResign = true;
            return "Are you sure you want to resign? If so, type resign again \n";
        }
        //resigns without making them leave the game
        wsFacade.resign(token,gameID);
        return "";
    }

    public String highlightMoves(String... params) throws ResponseException {
        //User inputs piece for which they want to highlight legal moves
        //(Getting info)
        ChessGame game = wsFacade.getGame();
        ChessBoard board = game.getBoard();
        Collection<ChessMove> allMoves;
        try {
            allMoves = game.validMoves(getPos(params[0]));
        } catch (Exception e){
            throw new ResponseException(500,"Wrong input");
        }
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
                * move <Starting Position> <Ending Position> <Promotion Piece>- make a chess move
                  If applicable, add Promotion piece for the pawn reaching the end of the board
                  Ex: b7 b8 queen
                * leave - return to main screen
                * resign - forfeit game
                * highlight <Position> - highlights legal moves
                """;
    }
}
