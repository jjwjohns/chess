package chess;
import chess.move_calculator.moves_calculator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;



    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

//    private Collection<ChessMove> Bishop_move_calculator(ChessBoard board, ChessPosition position){
//        Collection<ChessMove> Moves = new ArrayList<>();
//        int row = position.getRow();
//        int col = position.getColumn();
//
//        int[][] possible_directions = {{1,1},{1,-1},{-1,1},{-1,-1}};
//
//        for (int[] direction : possible_directions) {
//            if (row + direction[0] >=1 && row+direction[0] <=8 && col+direction[1] >=1 && col+direction[1] <=8){
//                for (int newR = row + direction[0], newC = col+direction[1]; newR <= 8 && newC <= 8 && newR >= 1 && newC >= 1; newR += direction[0], newC += direction[1]) {
//                    ChessPosition newPosition = new ChessPosition(newR, newC);
//                    if (board.getPiece(newPosition) == null){
//                        Moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newR, newC), null));
//                    }
//                    else if (board.getPiece(newPosition).getTeamColor() != this.pieceColor){
//                        Moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newR, newC), null));
//                        break;
//                    }
//                    else {
//                        break;
//                    }
//                }
//            }
//        }
//        return Moves;
//    }
//
//    private Collection<ChessMove> Knight_move_calculator(final ChessGame.TeamColor pieceColor, ChessBoard board, ChessPosition position){
//        Collection<ChessMove> Moves = new ArrayList<>();
//        int row = position.getRow();
//        int col = position.getColumn();
//
//        int[][] possible_moves = {{1,2},{1,-2},{-1,2},{-1,-2},{2,1},{-2,1},{-2,-1},{2,-1}};
//        for (int[] move : possible_moves) {
//            int newR = row + move[0];
//            int newC = col + move[1];
//
//            if (newR <= 8 && newC <= 8 && newR >= 1 && newC >= 1) {
//                ChessPosition newPosition = new ChessPosition(newR, newC);
//                if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != this.pieceColor) {
//                    Moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newR, newC), null));
//                }
//            }
//        }
//        return Moves;
//    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (this.type == PieceType.BISHOP) {
            return chess.move_calculator.moves_calculator.Bishop_move_calculator(this, board, myPosition);
        }
        else if (this.type == PieceType.KNIGHT) {
            return chess.move_calculator.moves_calculator.Knight_move_calculator(this, board, myPosition);
        }
        else return null;
    }
}



