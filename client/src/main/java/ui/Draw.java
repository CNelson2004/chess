package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class Draw {
//    public static void main(String[] args){
//        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
//        drawBoard();
//    }

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
            else if(currentRow%2==1){ //current row is odd but has no pieces
                if(squareRow%2==0){drawSquare(out,"black");}
                else{drawSquare(out,"white");}
            }
            else{ //current row is even with no pieces
                if(squareRow%2==0){drawSquare(out,"white");}
                else{drawSquare(out,"black");}
            }

        }
        //drawing numbers on right
        printNum(out,currentRow);
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
        if(squareColor.equals("black")){out.print(SET_BG_COLOR_BROWN);} //change black to brown and white to light brown
        else{out.print(SET_BG_COLOR_LIGHT_BROWN);}
        if(pieceColor.equals("white")){out.print(SET_TEXT_COLOR_WHITE);}
        else{out.print(SET_TEXT_COLOR_BLACK);}
        out.print(piece);
    }

    private static void drawSquare(PrintStream out, String color){
        if(color.equals("black")){setBrown(out);} //change black to brown and white to light brown
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
}
