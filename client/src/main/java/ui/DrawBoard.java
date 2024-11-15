package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawBoard {
    private static final int BOARD_SIZE_IN_SQUARES = 10;
    private static final String EMPTY = "   ";

    public static void drawBlack() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawLetters(out);
        drawWhiteTop(out);
        drawMiddleBlack(out);
        drawBlackBottom(out);
        drawLetters(out);

        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
        out.println();
    }

    public static void drawWhite() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawLetters(out);
        drawBlacktop(out);
        drawMiddleWhite(out);
        drawWhiteBottom(out);
        drawLetters(out);

        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
        out.println();
    }

    private static void drawLetters(PrintStream out) {

        String[] headers = {EMPTY ," a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EMPTY};
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            out.print(SET_TEXT_BOLD + SET_BG_COLOR_BLACK + headers[boardCol]);
        }

        out.println();
        out.print(RESET_TEXT_BOLD_FAINT);
    }

    private static void drawWhiteTop(PrintStream out) {
        String[] pieces = {" 1 ",WHITE_ROOK,WHITE_KNIGHT,WHITE_BISHOP,WHITE_KING,WHITE_QUEEN,WHITE_BISHOP,WHITE_KNIGHT,WHITE_ROOK," 1 "};
        loopPieces(out, pieces, SET_BG_COLOR_DARK_GREEN ,SET_TEXT_COLOR_WHITE, SET_BG_COLOR_LIGHT_GREY);
        loopWhitePawns(out, SET_BG_COLOR_LIGHT_GREY, SET_BG_COLOR_DARK_GREEN);
    }

    private static void drawBlacktop(PrintStream out) {
        String[] pieces = {" 8 ",BLACK_ROOK,BLACK_KNIGHT,BLACK_BISHOP,BLACK_QUEEN,BLACK_KING,BLACK_BISHOP,BLACK_KNIGHT,BLACK_ROOK," 8 "};
        loopPieces(out, pieces, SET_BG_COLOR_DARK_GREEN, SET_TEXT_COLOR_BLACK, SET_BG_COLOR_LIGHT_GREY);

        loopBlackPawns(out, SET_BG_COLOR_LIGHT_GREY, SET_BG_COLOR_DARK_GREEN);
    }

    private static void drawBlackBottom(PrintStream out) {
        loopBlackPawns(out, SET_BG_COLOR_DARK_GREEN, SET_BG_COLOR_LIGHT_GREY);

        String[] pieces = {" 8 ",BLACK_ROOK,BLACK_KNIGHT,BLACK_BISHOP,BLACK_KING,BLACK_QUEEN,BLACK_BISHOP,BLACK_KNIGHT,BLACK_ROOK," 8 "};

        loopPieces(out, pieces, SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK, SET_BG_COLOR_DARK_GREEN);
    }

    private static void drawWhiteBottom(PrintStream out) {
        loopWhitePawns(out, SET_BG_COLOR_DARK_GREEN, SET_BG_COLOR_LIGHT_GREY);

        String[] pieces = {" 1 ",WHITE_ROOK,WHITE_KNIGHT,WHITE_BISHOP,WHITE_QUEEN,WHITE_KING,WHITE_BISHOP,WHITE_KNIGHT,WHITE_ROOK," 1 "};

        loopPieces(out, pieces, SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_WHITE, SET_BG_COLOR_DARK_GREEN);
    }

    private static void drawMiddleBlack(PrintStream out) {
        loopRow(out, " 3 ", SET_BG_COLOR_DARK_GREEN, SET_BG_COLOR_LIGHT_GREY);
        loopRow(out, " 4 ", SET_BG_COLOR_LIGHT_GREY, SET_BG_COLOR_DARK_GREEN);
        loopRow(out, " 5 ", SET_BG_COLOR_DARK_GREEN, SET_BG_COLOR_LIGHT_GREY);
        loopRow(out, " 6 ", SET_BG_COLOR_LIGHT_GREY, SET_BG_COLOR_DARK_GREEN);
    }

    private static void drawMiddleWhite(PrintStream out) {
        loopRow(out, " 6 ", SET_BG_COLOR_DARK_GREEN, SET_BG_COLOR_LIGHT_GREY);
        loopRow(out, " 5 ", SET_BG_COLOR_LIGHT_GREY, SET_BG_COLOR_DARK_GREEN);
        loopRow(out, " 4 ", SET_BG_COLOR_DARK_GREEN, SET_BG_COLOR_LIGHT_GREY);
        loopRow(out, " 3 ", SET_BG_COLOR_LIGHT_GREY, SET_BG_COLOR_DARK_GREEN);
    }

    private static void loopBlackPawns(PrintStream out, String setBgColorDarkGreen, String setBgColorLightGrey) {
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            if (boardCol == 0 || boardCol == 9){
                out.print(SET_BG_COLOR_BLACK + SET_TEXT_BOLD + " 7 ");
            }
            else if (boardCol % 2 == 0) {
                out.print(setBgColorDarkGreen + SET_TEXT_COLOR_BLACK + BLACK_PAWN);
            }
            else {
                out.print(setBgColorLightGrey + SET_TEXT_COLOR_BLACK + BLACK_PAWN);
            }
            out.print(RESET_TEXT_COLOR);
        }
        out.println();
    }

    private static void loopWhitePawns(PrintStream out, String setBgColorDarkGreen, String setBgColorLightGrey) {
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            if (boardCol == 0 || boardCol == 9){
                out.print(SET_BG_COLOR_BLACK + SET_TEXT_BOLD + " 2 ");
            }
            else if (boardCol % 2 == 0) {
                out.print(setBgColorDarkGreen + SET_TEXT_COLOR_WHITE + WHITE_PAWN);
            }
            else {
                out.print(setBgColorLightGrey + SET_TEXT_COLOR_WHITE + WHITE_PAWN);
            }
            out.print(RESET_TEXT_COLOR);
        }
        out.println();
    }

    private static void loopPieces(PrintStream out,String[] pieces,String setBgColorLightGrey,String setTextColorBlack,String setBgColorDarkGreen){
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            if (boardCol == 0 || boardCol == 9){
                out.print(SET_BG_COLOR_BLACK + SET_TEXT_BOLD + pieces[boardCol]);
            }
            else if (boardCol % 2 == 0) {
                out.print(setBgColorLightGrey + setTextColorBlack + pieces[boardCol]);
            }
            else {
                out.print(setBgColorDarkGreen + setTextColorBlack + pieces[boardCol]);
            }
            out.print(RESET_TEXT_COLOR);
        }
        out.println();
    }

    private static void loopRow(PrintStream out, String s, String setBgColorDarkGreen, String setBgColorLightGrey) {
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            if (boardCol == 0 || boardCol == 9){
                out.print(SET_BG_COLOR_BLACK + SET_TEXT_BOLD + s);
            }
            else if (boardCol % 2 == 0) {
                out.print(setBgColorDarkGreen + EMPTY);
            }
            else {
                out.print(setBgColorLightGrey + EMPTY);
            }
            out.print(RESET_TEXT_COLOR);
        }
        out.println();
    }
}
