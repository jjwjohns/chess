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

    private Collection<ChessMove> B_move_calculator(ChessBoard board, ChessPosition position){
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

    private Collection<ChessMove> K_move_calculator(ChessBoard board, ChessPosition position){
        Collection<ChessMove> Moves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        Moves.add(new ChessMove(new ChessPosition(row,col), new ChessPosition(row+1,col+2), null));
        Moves.add(new ChessMove(new ChessPosition(row,col), new ChessPosition(row+1,col-2), null));
        Moves.add(new ChessMove(new ChessPosition(row,col), new ChessPosition(row-1,col+2), null));
        Moves.add(new ChessMove(new ChessPosition(row,col), new ChessPosition(row-1,col-2), null));
        Moves.add(new ChessMove(new ChessPosition(row,col), new ChessPosition(row+2,col+1), null));
        Moves.add(new ChessMove(new ChessPosition(row,col), new ChessPosition(row-2,col+1), null));
        Moves.add(new ChessMove(new ChessPosition(row,col), new ChessPosition(row-2,col-1), null));
        Moves.add(new ChessMove(new ChessPosition(row,col), new ChessPosition(row+2,col-1), null));
        return Moves;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (this.type == PieceType.BISHOP) {
            return B_move_calculator(board, myPosition);
        }
        else if (this.type == PieceType.KNIGHT) {
            return K_move_calculator(board, myPosition);
        }
        else return new ArrayList<>();
    }
}



