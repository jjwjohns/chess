package chess.move_calculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class moves_calculator {

    public static Collection<ChessMove> Bishop_move_calculator(ChessPiece piece, ChessBoard board, ChessPosition position){
        Collection<ChessMove> Moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();

        int[][] possible_directions = {{1,1},{1,-1},{-1,1},{-1,-1}};

        for (int[] direction : possible_directions) {
            if (row + direction[0] >=1 && row+direction[0] <=8 && col+direction[1] >=1 && col+direction[1] <=8){
                for (int newR = row + direction[0], newC = col+direction[1]; newR <= 8 && newC <= 8 && newR >= 1 && newC >= 1; newR += direction[0], newC += direction[1]) {
                    ChessPosition newPosition = new ChessPosition(newR, newC);
                    if (board.getPiece(newPosition) == null){
                        Moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newR, newC), null));
                    }
                    else if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()){
                        Moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newR, newC), null));
                        break;
                    }
                    else {
                        break;
                    }
                }
            }
        }
        return Moves;
    }

    public static Collection<ChessMove> Knight_move_calculator(ChessPiece piece, ChessBoard board, ChessPosition position){
        Collection<ChessMove> Moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();

        int[][] possible_moves = {{1,2},{1,-2},{-1,2},{-1,-2},{2,1},{-2,1},{-2,-1},{2,-1}};
        for (int[] move : possible_moves) {
            int newR = row + move[0];
            int newC = col + move[1];

            if (newR <= 8 && newC <= 8 && newR >= 1 && newC >= 1) {
                ChessPosition newPosition = new ChessPosition(newR, newC);
                if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                    Moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newR, newC), null));
                }
            }
        }
        return Moves;
    }

    public static Collection<ChessMove> King_move_calculator(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> Moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        int[][] possible_moves = {{1,1},{1,-1},{1,0},{0,1},{0,-1},{-1,1},{-1,0},{-1,-1}};

        for (int[] move : possible_moves) {
            int newR = row + move[0];
            int newC = col + move[1];
            if (newR <= 8 && newC <= 8 && newR >= 1 && newC >= 1) {
                ChessPosition newPosition = new ChessPosition(newR, newC);
                if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                    Moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newR, newC), null));
                }
            }
        }
        return Moves;
    }

    public static Collection<ChessMove> Pawn_move_calculator(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> Moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();

        if (row >=1 && row <=8 && col>=1 && col+1<=8){
            if(piece.getTeamColor() == WHITE){
                ChessPosition newPosition = new ChessPosition(row+1, col);
                if (board.getPiece(newPosition) == null){
                    Moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row+1, col), null));
                }
            }
            if(piece.getTeamColor() == BLACK){
                ChessPosition newPosition = new ChessPosition(row-1, col);
                if (board.getPiece(newPosition) == null){
                    Moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row-1, col), null));
                }
            }
        }
        ChessPosition newPosition = new ChessPosition(row+1, col);
        ChessPosition new2Position = new ChessPosition(row+2, col);
        if (row == 2 && piece.getTeamColor() == WHITE && board.getPiece(newPosition) == null && board.getPiece(new2Position) == null){
            Moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row+2, col), null));
        }
        newPosition = new ChessPosition(row-1, col);
        new2Position = new ChessPosition(row-2, col);
        if (row == 7 && piece.getTeamColor() == BLACK && board.getPiece(newPosition) == null && board.getPiece(new2Position) == null){
            Moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row-2, col), null));
        }




        return Moves;
    }

    public static Collection<ChessMove> Rook_move_calculator(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> Moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();

        int[][] possible_directions = {{1,0},{0,1},{-1,0},{0,-1}};

        for (int[] direction : possible_directions) {
            if (row + direction[0] >=1 && row+direction[0] <=8 && col+direction[1] >=1 && col+direction[1] <=8){
                for (int newR = row + direction[0], newC = col+direction[1]; newR <= 8 && newC <= 8 && newR >= 1 && newC >= 1; newR += direction[0], newC += direction[1]) {
                    ChessPosition newPosition = new ChessPosition(newR, newC);
                    if (board.getPiece(newPosition) == null){
                        Moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newR, newC), null));
                    }
                    else if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()){
                        Moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newR, newC), null));
                        break;
                    }
                    else {
                        break;
                    }
                }
            }
        }
        return Moves;
    }

    public static Collection<ChessMove> Queen_move_calculator(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> Moves = Rook_move_calculator(piece, board, position);
        Moves.addAll(Bishop_move_calculator(piece, board, position));
        return Moves;
    }

}