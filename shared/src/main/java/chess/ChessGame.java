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
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        board.resetBoard();
        turn = TeamColor.WHITE;
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


    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> moves;
        ChessPiece piece = board.getPiece(startPosition);
        moves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> valid = new ArrayList<>();

        for (ChessMove move : moves){
            ChessPosition end = move.getEndPosition();
            ChessPiece target = board.getPiece(end);

            board.addPiece(end, piece);
            board.addPiece(move.getStartPosition(), null);
            if (!isInCheck(piece.getTeamColor())){
                valid.add(move);
            }
            board.addPiece(move.getEndPosition(), target);
            board.addPiece(move.getStartPosition(), piece);
        }

        return valid;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        try{
            ChessPiece piece = board.getPiece(move.getStartPosition());
            if (piece.getTeamColor() != turn){
                throw new InvalidMoveException("Wrong team's move");
            }
            Collection<ChessMove> valid = validMoves(move.getStartPosition());
            if (valid.contains(move)){
//                pawns
                if (board.getPiece(move.getStartPosition()).getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null){
                    ChessPiece promotion = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
                    board.addPiece(move.getEndPosition(), promotion);
                    board.addPiece(move.getStartPosition(), null);
                }
                else {
                    board.addPiece(move.getEndPosition(), piece);
                    board.addPiece(move.getStartPosition(), null);
                }
                if (piece.getTeamColor() == TeamColor.WHITE){
                    setTeamTurn(TeamColor.BLACK);
                }
                else if (piece.getTeamColor() == TeamColor.BLACK){
                    setTeamTurn(TeamColor.WHITE);
                }
            }

            else {
                throw new InvalidMoveException("This move is invalid");
            }

        }catch (Exception e){
            throw new InvalidMoveException("general exception");
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    private boolean isKingInMoves(Collection<ChessMove> moves, TeamColor teamColor) {
        for (ChessMove move : moves) {
            ChessPosition end = move.getEndPosition();
            ChessPiece targetPiece = board.getPiece(end);

            if (targetPiece != null && targetPiece.getPieceType() == ChessPiece.PieceType.KING
                    && targetPiece.getTeamColor() == teamColor) {
                return true;
            }
        }
        return false;
    }

    public boolean isInCheck(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(position);
                if (piece == null || piece.getTeamColor() == teamColor){
                    continue;
                }

                Collection<ChessMove> moves = piece.pieceMoves(board, position);
                if (isKingInMoves(moves, teamColor)) {
                    return true;
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

    private boolean movesRemovesCheck(ChessPiece piece, Collection<ChessMove> moves, TeamColor teamColor) {
        for (ChessMove move : moves) {
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();
            ChessPiece target = board.getPiece(end);

            board.addPiece(end, piece);
            board.addPiece(start, null);
            if (!isInCheck(teamColor)) {
                board.addPiece(end, target);
                board.addPiece(start, piece);
                return true;
            }
            board.addPiece(end, target);
            board.addPiece(start, piece);
        }
        return false;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)){
            return false;
        }
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);

                if (board.getPiece(position) == null || piece.getTeamColor() != teamColor) {
                    continue;
                }
                Collection<ChessMove> moves = piece.pieceMoves(board, position);
                if (movesRemovesCheck(piece, moves, teamColor)) {
                    return false;
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
        if (isInCheck(teamColor)){
            return false;
        }

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(position);

                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }
                Collection<ChessMove> valid = validMoves(position);
                if (!valid.isEmpty()){
                    return false;
                }
            }
        }
        return true;
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
