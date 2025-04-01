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
        ArrayList<Integer> row1,row2,row3,row4,row5,row6,row7,row8;
        row1=row2=row3=row4=row5=row6=row7=row8=new ArrayList<>();
        ArrayList<ArrayList<Integer>> allRows = new ArrayList<>(Arrays.asList(row1,row2,row3,row4,row5,row6,row7,row8));
        for(ChessPosition move: moves){
            switch(move.getRow()){
                case 1 -> row1.add(move.getColumn());
                case 2 -> row2.add(move.getColumn());
                case 3 -> row3.add(move.getColumn());
                case 4 -> row4.add(move.getColumn());
                case 5 -> row5.add(move.getColumn());
                case 6 -> row6.add(move.getColumn());
                case 7 -> row7.add(move.getColumn());
                case 8 -> row8.add(move.getColumn());
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
                drawHighlightedRow(out, row, board, allRows.get(row-1));
            }
            //draw letters at the bottom
            printLetters(out, true);
        }
        else{
            printLetters(out,true);
            for(int row=8;row>0;row--){
                drawHighlightedRow(out, row, board, allRows.get(row-1));
            }
            printLetters(out,true);
        }
    }

    private static void drawHighlightedRow(PrintStream out, int currentRow, ChessBoard board, ArrayList<Integer> cols){
        ChessPiece[] theRow = board.getBoard()[currentRow-1];
        //drawing numbers on left
        printNum(out,currentRow);
        //drawing row
        int squareRow=0;
        for(ChessPiece piece: theRow){
            String color = null;
            String icon = null;
            try {color = setTeamColor(piece);
                icon = getPieceIcon(color,piece.getPieceType());}
            catch (Exception _) {}
            //^Can be null if there is no piece on that square (& applies to below)
            selectHighlightedSquareType(out,currentRow,squareRow,color,icon,cols);
            squareRow++;
        }
        //drawing numbers on right
        printNum(out,currentRow);
        setBlack(out);
        out.println();
    }

    private static void selectHighlightedSquareType(PrintStream out, int currentRow, int squareRow, String color, String icon, ArrayList<Integer> cols){
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
                    drawSquare(out, "Green", color, icon);
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
                if(squareRow%2==0){drawSquare(out,"lightGreen");}
                else{drawSquare(out,"Green");}
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
            for (int row = 8; row > 0; row--) {  //breaking on third row
                drawRow(out, row, board);
            }
            //draw letters at the bottom
            printLetters(out, true);
        }
        else{
            printLetters(out,true);
            for(int row=1;row<9;row++){
                drawRow(out, row, board);
            }
            printLetters(out,true);
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

    private static void drawRow(PrintStream out, int currentRow, ChessBoard board){
        ChessPiece[] theRow = board.getBoard()[currentRow-1];
        //drawing numbers on left
        printNum(out,currentRow);
        //drawing row
        int squareRow=0;
        for(ChessPiece piece: theRow){
            String color = null;
            String icon = null;
            try {color = setTeamColor(piece);
                icon = getPieceIcon(color,piece.getPieceType());}
            catch (Exception _) {}
            //^Can be null if there is no piece on that square (& applies to below)
            selectSquareType(out,currentRow,squareRow,color,icon);
            squareRow++;
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
        if(color.equals("black")){setBrown(out);}
        else{setLightBrown(out);}
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

    private static void reset(PrintStream out){
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }


    //Static starting board
    public static void drawBoard(){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        drawChessBoard(out);
        reset(out);
    }

    public static void drawBoardBlack(){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        drawChessBoardBackwards(out);
        reset(out);
    }

    private static void drawChessBoard(PrintStream out){
        //draw letters at the top
        printLetters(out,true);
        //draw rows
        for(int row=8;row>0;row--){
            drawRow(out, row);
        }
        //draw letters at the bottom
        printLetters(out,true);
    }

    private static void drawChessBoardBackwards(PrintStream out){
        //draw letters at the top
        printLetters(out,false);
        //draw rows
        for(int row=1;row<9;row++){
            drawRow(out, row);
        }
        //draw letters at the bottom
        printLetters(out,false);
    }

    private static void drawRow(PrintStream out, int currentRow){
        String[] lineup = new String[]{};
        String color = "";
        if(currentRow==1){lineup = new String[]{WHITE_ROOK,WHITE_KNIGHT,WHITE_BISHOP,WHITE_QUEEN,WHITE_KING,WHITE_BISHOP,WHITE_KNIGHT,WHITE_ROOK};
            color = "white";}
        if(currentRow==2){lineup = new String[]{WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN,WHITE_PAWN};
            color = "white";}
        if(currentRow==7){lineup = new String[]{BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN,BLACK_PAWN};
            color = "black";}
        if(currentRow==8){lineup = new String[]{BLACK_ROOK,BLACK_KNIGHT,BLACK_BISHOP,BLACK_QUEEN,BLACK_KING,BLACK_BISHOP,BLACK_KNIGHT,BLACK_ROOK};
            color = "black";}

        //drawing numbers on left
        printNum(out,currentRow);
        for(int squareRow=0;squareRow<8;squareRow++){
            //drawing row
            if(currentRow==1||currentRow==7){
                if(squareRow%2==0){drawSquare(out,"black",color,lineup[squareRow]);}
                else{drawSquare(out,"white",color,lineup[squareRow]);}
            }
            else if(currentRow==2||currentRow==8){
                if(squareRow%2==0){drawSquare(out,"white",color,lineup[squareRow]);}
                else{drawSquare(out,"black",color,lineup[squareRow]);}
            }
            else{ //current row is even with no pieces
                drawNormalEmptySquare(out,currentRow,squareRow);
            }

        }
        //drawing numbers on right
        printNum(out,currentRow);
        setBlack(out);
        out.println();
    }

}
