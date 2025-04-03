package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class Draw {

    public static void highlight(ChessBoard board, String color, Collection<ChessPosition> moves){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        //draw board with highlighted moves
        drawHighlightedBoard(out,board,color,moves);
        reset(out);
    }

    public static ArrayList<ArrayList<Integer>> createRowHolder(Collection<ChessPosition> moves){
        ArrayList<ArrayList<Integer>> allRows = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            allRows.add(new ArrayList<>());
        }
            for (ChessPosition move : moves) {
                switch (move.getRow()) {
                    case 1 -> allRows.get(0).add(move.getColumn());
                    case 2 -> allRows.get(1).add(move.getColumn());
                    case 3 -> allRows.get(2).add(move.getColumn());
                    case 4 -> allRows.get(3).add(move.getColumn());
                    case 5 -> allRows.get(4).add(move.getColumn());
                    case 6 -> allRows.get(5).add(move.getColumn());
                    case 7 -> allRows.get(6).add(move.getColumn());
                    case 8 -> allRows.get(7).add(move.getColumn());
                }
            }
        return allRows;
    }

    public static void drawHighlightedBoard(PrintStream out, ChessBoard board, String color, Collection<ChessPosition> moves){
        //getting the moves into sets
        ArrayList<ArrayList<Integer>> allRows = createRowHolder(moves);
        //drawing board
        if(color.equalsIgnoreCase("white")) {
            //draw letters at the top
            printLetters(out, true);
            //draw rows
            for (int row = 8; row > 0; row--) {
                drawHighlightedRow(out, row, board, allRows.get(row-1), color);
            }
            //draw letters at the bottom
            printLetters(out, true);
        }
        else{
            printLetters(out,false);
            for(int row=1;row<9;row++){
                drawHighlightedRow(out, row, board, allRows.get(row-1), color);
            }
            printLetters(out,false);
        }
    }

    private static void drawHighlightedRow(PrintStream out, int currentRow, ChessBoard board, ArrayList<Integer> cols, String theColor){
        ChessPiece[] theRow = board.getBoard()[currentRow-1];
        //drawing numbers on left
        printNum(out,currentRow);
        //drawing row
        int squareRow;
        int stop;
        if(theColor.equalsIgnoreCase("white")){
            squareRow=1;
            stop = 9;
        }
        else{squareRow=8;
            stop = 0;}
        while(squareRow != stop) {
            ChessPiece piece = theRow[squareRow - 1];
            String color = null;
            String icon = null;
            try {
                color = setTeamColor(piece);
                icon = getPieceIcon(color, piece.getPieceType());
                selectHighSquareType(out, currentRow, squareRow, color, icon, cols);
                squareRow = updateSquareRow(squareRow, theColor);
            } catch (Exception e) {
                selectHighSquareType(out, currentRow, squareRow, color, icon, cols);
                squareRow = updateSquareRow(squareRow, theColor);
            }
        }
        //drawing numbers on right
        printNum(out,currentRow);
        setBlack(out);
        out.println();
    }

    private static void selectHighSquareType(PrintStream out, int currentRow, int squareRow, String color, String icon, ArrayList<Integer> cols){
        boolean isHighlighted = cols.contains(squareRow);
        if(icon!=null){
            highlightedSquareHelper(out,isHighlighted,currentRow,color,squareRow,icon);
        }else{
            emptyHighlightedSquareHelper(out,isHighlighted,currentRow,squareRow);
        }
    }

    private static void highlightedSquareHelper(PrintStream out, boolean isHighlighted, int currentRow, String color, int squareRow, String icon){
        if(isHighlighted) {
            if (currentRow % 2 == 1) { //current row is odd
                if (squareRow % 2 == 0) {
                    drawSquare(out, "green", color, icon); //dark green instead of black
                } else {
                    drawSquare(out, "lightGreen", color, icon); //light green instead of white
                }
            } else { //current row is even
                if (squareRow % 2 == 0) {
                    drawSquare(out, "lightGreen", color, icon);
                } else {
                    drawSquare(out, "green", color, icon);
                }
            }
        }else{
            drawNormalPieceSquare(out,currentRow,squareRow,color,icon);
        }
    }

    private static void emptyHighlightedSquareHelper(PrintStream out, boolean isHighlighted, int currentRow, int squareRow){
        if(isHighlighted){
            if(currentRow%2==1){ //current row is odd
                if(squareRow%2==0){drawSquare(out,"green");} //dark green
                else{drawSquare(out,"lightGreen");} //light green
            }
            else{ //current row is even
                if(squareRow%2==0){drawSquare(out,"lightGreen");} //use the one with 4 parameters
                else{drawSquare(out,"green");}
            }
        }else{
            drawNormalEmptySquare(out,currentRow,squareRow);
        }
    }





    public static void drawBoard(ChessBoard board, String color){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        drawChessBoard(out, board, color);
        reset(out);
    }

    private static void drawChessBoard(PrintStream out, ChessBoard board, String color){
        //color is the color of the player that is currently using the function
        if(color.equalsIgnoreCase("white")) {
            //draw letters at the top
            printLetters(out, true);
            //draw rows
            for (int row = 8; row > 0; row--) {
                drawRow(out, row, board, color);
            }
            //draw letters at the bottom
            printLetters(out, true);
        }
        else{ //black
            printLetters(out,false);
            for(int row=1;row<9;row++){
                drawRow(out, row, board, color);
            }
            printLetters(out,false);
        }
    }

    private static String getPieceIcon(String color, ChessPiece.PieceType type){
        if(color.equals("white")){
            switch(type){
                case PAWN -> {return WHITE_PAWN;}
                case ROOK -> {return WHITE_ROOK;}
                case BISHOP -> {return WHITE_BISHOP;}
                case KNIGHT -> {return WHITE_KNIGHT;}
                case QUEEN -> {return WHITE_QUEEN;}
                case KING -> {return WHITE_KING;}
                default -> {return null;}
            }
        }
        else{
            switch(type){
                case PAWN -> {return BLACK_PAWN;}
                case ROOK -> {return BLACK_ROOK;}
                case BISHOP -> {return BLACK_BISHOP;}
                case KNIGHT -> {return BLACK_KNIGHT;}
                case QUEEN -> {return BLACK_QUEEN;}
                case KING -> {return BLACK_KING;}
            }
        }
        return null;
    }

    private static String setTeamColor(ChessPiece piece){
        String color;
        ChessGame.TeamColor theColor = piece.getTeamColor();
        color = switch (theColor) {
            case WHITE -> "white";
            case BLACK -> "black";
        };
        return color;
    }

    private static int updateSquareRow(int squareRow, String color){
        if(color.equalsIgnoreCase("white")){
            return ++squareRow;
        }else{
            return --squareRow;
        }
    }

    private static void drawRow(PrintStream out, int currentRow, ChessBoard board, String theColor){
        ChessPiece[] theRow = board.getBoard()[currentRow-1];
        //drawing numbers on left
        printNum(out,currentRow);
        //drawing row
        int squareRow;
        int stop;
        if(theColor.equalsIgnoreCase("white")){
            squareRow=1;
            stop = 9;
        }
        else{squareRow=8;
            stop = 0;}
        while(squareRow != stop){
            ChessPiece piece = theRow[squareRow-1];
            String color = null;
            String icon = null;
            try {
                color = setTeamColor(piece);
                icon = getPieceIcon(color, piece.getPieceType());
                selectSquareType(out, currentRow, squareRow, color, icon);
                squareRow = updateSquareRow(squareRow,theColor);
            }catch (Exception e) {
                selectSquareType(out,currentRow,squareRow,color,icon);
                squareRow = updateSquareRow(squareRow,theColor);
            }
            //^Can be null if there is no piece on that square
        }
        //drawing numbers on right
        printNum(out,currentRow);
        setBlack(out);
        out.println();
    }

    private static void selectSquareType(PrintStream out, int currentRow, int squareRow, String color, String icon){
        if(icon!=null){
            drawNormalPieceSquare(out,currentRow,squareRow,color,icon);
        }else{
            drawNormalEmptySquare(out,currentRow,squareRow);
        }
    }

    private static void drawNormalPieceSquare(PrintStream out, int currentRow, int squareRow, String color, String icon){
        if(currentRow%2==1){ //current row is odd
            if(squareRow%2==0){drawSquare(out,"black", color, icon);}
            else{drawSquare(out,"white", color, icon);}
        }
        else{ //current row is even
            if(squareRow%2==0){drawSquare(out,"white", color, icon);}
            else{drawSquare(out,"black", color, icon);}
        }
    }

    private static void drawNormalEmptySquare(PrintStream out, int currentRow, int squareRow){
        if(currentRow%2==1){ //current row is odd
            if(squareRow%2==0){drawSquare(out,"black");}
            else{drawSquare(out,"white");}
        }
        else{ //current row is even
            if(squareRow%2==0){drawSquare(out,"white");}
            else{drawSquare(out,"black");}
        }
    }

    private static void printLetters(PrintStream out, boolean forward){
        out.print(SET_BG_COLOR_DARK_BROWN);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(" ".repeat(3));
        String[] lineup;
        if(forward){lineup = new String[]{"a ","b ","c ","d ","e ","f ","g ","h "};}
        else{lineup = new String[]{"h ","g ","f ","e ","d ","c ","b ","a "};}
        for(int col=0;col<8;col++){
            out.print(TRUE_EMPTY);
            out.print(lineup[col]);
        }
        out.print(" ".repeat(3));
        setBlack(out);
        out.println();
    }

    private static void printNum(PrintStream out, int num){
        out.print(SET_BG_COLOR_DARK_BROWN);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(" ");
        out.print(num);
        out.print(" ");
    }

    private static void drawSquare(PrintStream out, String squareColor, String pieceColor, String piece){
        //set square drawing color
        switch (squareColor) {
            case "black" -> out.print(SET_BG_COLOR_BROWN);
            case "white" -> out.print(SET_BG_COLOR_LIGHT_BROWN);
            case "green" -> out.print(SET_BG_COLOR_DARK_GREEN);
            case "lightGreen" -> out.print(SET_BG_COLOR_GREEN);
        }
        //set piece drawing color
        if(pieceColor.equals("white")){out.print(SET_TEXT_COLOR_WHITE);}
        else{out.print(SET_TEXT_COLOR_BLACK);}
        //printing piece
        out.print(piece);
    }

    private static void drawSquare(PrintStream out, String color){
        switch (color) {
            case "black" -> setBrown(out);
            case "white" -> setLightBrown(out);
            case "green" -> setGreen(out);
            case "lightGreen" -> setLightGreen(out);
        }
        out.print(EMPTY);
    }

    private static void setBlack(PrintStream out){
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBrown(PrintStream out){
        out.print(SET_BG_COLOR_BROWN);
        out.print(SET_TEXT_COLOR_BROWN);
    }

    private static void setLightBrown(PrintStream out){
        out.print(SET_BG_COLOR_LIGHT_BROWN);
        out.print(SET_TEXT_COLOR_LIGHT_BROWN);
    }

    private static void setGreen(PrintStream out){
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_DARK_GREEN);
    }

    private static void setLightGreen(PrintStream out){
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_COLOR_GREEN);
    }

    private static void reset(PrintStream out){
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }
}
