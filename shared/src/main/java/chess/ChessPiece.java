package chess;

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

    private Collection<ChessMove> Bishop_move_calculator(ChessBoard board, ChessPosition position){
        Collection<ChessMove> Moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        int temp_row = row;
        int temp_col = col;
//        while (temp_col<=8 && temp_row>0 && temp_col>0 && temp_row<=8){
//
//        }

        Moves.add(new ChessMove(new ChessPosition(5,4), new ChessPosition(7,2), null));
        return Moves;
    }

    private Collection<ChessMove> Knight_move_calculator(ChessBoard board, ChessPosition position){
        Collection<ChessMove> Moves = new ArrayList<>();
        System.out.println("initialized");
        int row = position.getRow();
        int col = position.getColumn();
        System.out.println("got row and column" + row + "," + col);

        int[][] possible_moves = {{1,2},{1,-2},{-1,2},{-1,-2},{2,1},{-2,1},{-2,-1},{2,-1}};
        System.out.println("got possible moves");
        for (int[] move : possible_moves) {
            int newR = row + move[0];
            int newC = col + move[1];
            System.out.println("assigned new row and column");

            if (newR <= 8 && newC <= 8 && newR >= 1 && newC >= 1) {
                Moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newR, newC), null));
                System.out.println("added valid move" + newR + "," + newC);
            }
        }
        System.out.println("Returned Moves");
        return Moves;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        System.out.println("first function");
        if (this.type == PieceType.BISHOP) {
            return Bishop_move_calculator(board, myPosition);
        }
        else if (this.type == PieceType.KNIGHT) {
            System.out.println("entering function");
            return Knight_move_calculator(board, myPosition);
        }
        else return null;
    }
}



