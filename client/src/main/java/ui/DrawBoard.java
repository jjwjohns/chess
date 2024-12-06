package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class DrawBoard {
    private static final int BOARD_SIZE_IN_SQUARES=10;
    private static final String EMPTY="   ";

    public static void drawWhite(ChessBoard board, Collection<ChessPosition> positions) {
        var out=new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String[] headers={EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EMPTY};

        out.print(ERASE_SCREEN);

        drawLetters(out, headers);
        drawRow(out, board, positions, 8, 0);
        drawRow(out, board, positions, 7, 0);
        drawRow(out, board, positions, 6, 0);
        drawRow(out, board, positions, 5, 0);
        drawRow(out, board, positions, 4, 0);
        drawRow(out, board, positions, 3, 0);
        drawRow(out, board, positions, 2, 0);
        drawRow(out, board, positions, 1, 0);
        drawLetters(out, headers);

        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
        out.println();
    }

    public static void drawBlack(ChessBoard board, Collection<ChessPosition> positions) {
        var out=new PrintStream(System.out, true, StandardCharsets.UTF_8);
        String[] headers={EMPTY, " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", EMPTY};

        out.print(ERASE_SCREEN);

        drawLetters(out, headers);
        drawRow(out, board, positions, 1, 1);
        drawRow(out, board, positions, 2, 1);
        drawRow(out, board, positions, 3, 1);
        drawRow(out, board, positions, 4, 1);
        drawRow(out, board, positions, 5, 1);
        drawRow(out, board, positions, 6, 1);
        drawRow(out, board, positions, 7, 1);
        drawRow(out, board, positions, 8, 1);
        drawLetters(out, headers);

        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
        out.println();
    }


    private static String getType(ChessBoard board, ChessPosition pos) {
        if (board.getPiece(pos) == null) {
            return EMPTY;
        }
        ChessPiece.PieceType type=board.getPiece(pos).getPieceType();
        ChessGame.TeamColor color=board.getPiece(pos).getTeamColor();

        if (color == ChessGame.TeamColor.WHITE) {
            return switch (type) {
                case KING -> EscapeSequences.WHITE_KING;
                case QUEEN -> EscapeSequences.WHITE_QUEEN;
                case BISHOP -> EscapeSequences.WHITE_BISHOP;
                case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
                case ROOK -> EscapeSequences.WHITE_ROOK;
                case PAWN -> EscapeSequences.WHITE_PAWN;
            };
        } else {
            return switch (type) {
                case KING -> EscapeSequences.BLACK_KING;
                case QUEEN -> EscapeSequences.BLACK_QUEEN;
                case BISHOP -> EscapeSequences.BLACK_BISHOP;
                case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
                case ROOK -> EscapeSequences.BLACK_ROOK;
                case PAWN -> EscapeSequences.BLACK_PAWN;
            };
        }
    }

    private static void drawRow(PrintStream out, ChessBoard board, Collection<ChessPosition> positions, Integer row, Integer flipped) {
        String dark;
        String light;
        if (flipped == 0) {
            dark=SET_BG_COLOR_DARK_GREEN;
            light=SET_BG_COLOR_LIGHT_GREY;
        } else {
            light=SET_BG_COLOR_DARK_GREEN;
            dark=SET_BG_COLOR_LIGHT_GREY;
        }


        for (int boardCol=0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            if (boardCol == 0 || boardCol == 9) {
                out.print(SET_BG_COLOR_BLACK + SET_TEXT_BOLD + " " + row + " ");
            } else {
                ChessPosition pos = new ChessPosition(row, boardCol);
                String printable=getType(board, pos);

                if (positions != null && positions.contains(pos)){
                    dark=SET_BG_COLOR_YELLOW;
                    light=SET_BG_COLOR_YELLOW;
                }
                else {
                    if (flipped == 0){
                        dark=SET_BG_COLOR_DARK_GREEN;
                        light=SET_BG_COLOR_LIGHT_GREY;
                    }
                    else {
                        light=SET_BG_COLOR_DARK_GREEN;
                        dark=SET_BG_COLOR_LIGHT_GREY;
                    }
                }

                String textColor="";
                if (board.getPiece(pos) != null) {
                    if (board.getPiece(pos).getTeamColor() == ChessGame.TeamColor.WHITE) {
                        textColor=SET_TEXT_COLOR_WHITE;
                    } else {
                        textColor=SET_TEXT_COLOR_BLACK;
                    }
                }
                if (row % 2 == 0) {
                    if (boardCol % 2 == 0) {
                        out.print(dark + textColor + printable);
                    } else {
                        out.print(light + textColor + printable);
                    }
                } else {
                    if (boardCol % 2 == 0) {
                        out.print(light + textColor + printable);
                    } else {
                        out.print(dark + textColor + printable);
                    }
                }

            }

            out.print(RESET_TEXT_COLOR);
        }
        out.println();
    }

    private static void drawLetters(PrintStream out, String[] headers) {
        for (int boardCol=0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            out.print(SET_TEXT_BOLD + SET_BG_COLOR_BLACK + headers[boardCol]);
        }

        out.println();
        out.print(RESET_TEXT_BOLD_FAINT);
    }
}
