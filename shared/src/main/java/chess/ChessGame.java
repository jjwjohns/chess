package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard board;
    private ChessPiece[] whites;
    private ChessPiece[] blacks;

    public ChessGame() {
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

//    /**
//     * Gets a valid moves for a piece at the given location
//     *
//     * @param startPosition the piece to get valid moves for
//     * @return Set of valid moves for requested piece, or null if no piece at
//     * startPosition
//     */

    private Collection <ChessMove> board_check(ChessBoard board, Collection<ChessMove> moves){
        for (ChessMove move : moves){

        }
        return moves;
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> moves;
        ChessPiece piece = board.getPiece(startPosition);
        moves = piece.pieceMoves(board, startPosition);

        return board_check(board, moves);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                if (board.getPiece(new ChessPosition(i,j)) != null){
                    ChessPiece piece = board.getPiece(new ChessPosition(i,j));
                    if (piece.getTeamColor() != teamColor){
                        Collection<ChessMove> moves = piece.pieceMoves(board, new ChessPosition(i,j));
                        for (ChessMove move: moves){
                            if (board.getPiece(move.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING && board.getPiece(move.getEndPosition()).getTeamColor() != teamColor){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)){
            return false;
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board.getPiece(new ChessPosition(i, j)).getPieceType() == ChessPiece.PieceType.KING && board.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor) {
                    ChessPiece King = board.getPiece(new ChessPosition(i, j));
                    Collection<ChessMove> moves = King.pieceMoves(board, new ChessPosition(i,j));
                    for (ChessMove move : moves){
                        board.addPiece(move.getEndPosition(), King);
                        if (!isInCheck(teamColor)){
                            board.addPiece(move.getStartPosition(), King);
                            board.addPiece(move.getEndPosition(), null);
                            return false;
                        }
                        board.addPiece(move.getStartPosition(), King);
                        board.addPiece(move.getEndPosition(), null);

                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
//        if (isInCheck(teamColor) && validMoves(king).isEmpty()){
//            return true;
//        }
//        else {return false;}
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
