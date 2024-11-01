package chess.movecalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class MovesCalculator {

    public static Collection<ChessMove> generalCalculator(ChessPiece piece, ChessBoard board, int row, int col, int[][] moves){
        Collection<ChessMove> chessMoves = new ArrayList<>();
        for (int[] direction : moves) {
            if (row + direction[0] >= 1 && row + direction[0] <= 8 && col + direction[1] >= 1 && col + direction[1] <= 8) {
                int newR = row + direction[0];
                int newC = col + direction[1];
                while (newR <= 8 && newC <= 8 && newR >= 1 && newC >= 1) {
                    ChessPosition newPosition = new ChessPosition(newR, newC);
                    if (board.getPiece(newPosition) == null) {
                        chessMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newR, newC), null));
                    } else if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                        chessMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newR, newC), null));
                        break;
                    } else {
                        break;
                    }
                    newR += direction[0];
                    newC += direction[1];
                }
            }
        }
        return chessMoves;
    }

    public static Collection<ChessMove> bishopMoveCalculator(ChessPiece piece, ChessBoard board, ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();

        int[][] possibleDirections = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        return generalCalculator(piece, board, row, col, possibleDirections);
    }

    public static Collection<ChessMove> knightMoveCalculator(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> chessMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();

        int[][] possibleMoves = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {-2, 1}, {-2, -1}, {2, -1}};
        return getMoves(piece, board, chessMoves, row, col, possibleMoves);
    }

    private static Collection<ChessMove> getMoves(ChessPiece piece, ChessBoard board, Collection<ChessMove> moves, int row, int col, int[][] ways) {
        for (int[] move : ways) {
            int newR = row + move[0];

            int newC = col + move[1];

            if (newR <= 8 && newC <= 8 && newR >= 1 && newC >= 1) {
                ChessPosition newPosition = new ChessPosition(newR, newC);
                if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(newR, newC), null));
                }
            }
        }
        return moves;
    }

    public static Collection<ChessMove> kingMoveCalculator(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> chessMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        int[][] possibleMoves = {{1, 1}, {1, -1}, {1, 0}, {0, 1}, {0, -1}, {-1, 1}, {-1, 0}, {-1, -1}};

        return getMoves(piece, board, chessMoves, row, col, possibleMoves);
    }


    private static void addPromotionMoves(Collection<ChessMove> chessMoves, ChessPosition from, ChessPosition to) {
        chessMoves.add(new ChessMove(from, to, ChessPiece.PieceType.QUEEN));
        chessMoves.add(new ChessMove(from, to, ChessPiece.PieceType.KNIGHT));
        chessMoves.add(new ChessMove(from, to, ChessPiece.PieceType.BISHOP));
        chessMoves.add(new ChessMove(from, to, ChessPiece.PieceType.ROOK));
    }

    public static Collection<ChessMove> pawnMoveCalculator(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> chessMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();


//        Checking for basic one forward move
        if (row >= 1 && row <= 8 && col >= 1 && col + 1 <= 8) {
            if (piece.getTeamColor() == WHITE) {
                ChessPosition newPosition = new ChessPosition(row + 1, col);
                if (board.getPiece(newPosition) == null) {
                    if (row + 1 == 8) {
                        addPromotionMoves(chessMoves, new ChessPosition(row, col), new ChessPosition(row + 1, col));
                    } else {
                        chessMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + 1, col), null));
                    }
                }
            }
            if (piece.getTeamColor() == BLACK) {
                ChessPosition newPosition = new ChessPosition(row - 1, col);
                if (board.getPiece(newPosition) == null) {
                    if (row - 1 == 1) {
                        addPromotionMoves(chessMoves, new ChessPosition(row, col), new ChessPosition(row - 1, col));
                    } else {
                        chessMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row - 1, col), null));
                    }
                }
            }
        }
//        checking for starting double move
        ChessPosition newPosition = new ChessPosition(row + 1, col);
        ChessPosition new2Position = new ChessPosition(row + 2, col);
        if (row == 2 && piece.getTeamColor() == WHITE && board.getPiece(newPosition) == null && board.getPiece(new2Position) == null) {
            chessMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + 2, col), null));
        }
        newPosition = new ChessPosition(row - 1, col);
        new2Position = new ChessPosition(row - 2, col);
        if (row == 7 && piece.getTeamColor() == BLACK && board.getPiece(newPosition) == null && board.getPiece(new2Position) == null) {
            chessMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row - 2, col), null));
        }
//        checking for White capture
        if (row <8 && col <8){
            newPosition = new ChessPosition(row + 1, col + 1);
            if (board.getPiece(newPosition) != null) {
                if (piece.getTeamColor() == WHITE && board.getPiece(newPosition).getTeamColor() == BLACK) {
                    if (row + 1 == 8) {
                        addPromotionMoves(chessMoves, new ChessPosition(row, col), new ChessPosition(row + 1, col + 1));
                    } else {
                        chessMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + 1, col + 1), null));
                    }
                }
            }
        }
        if (row < 8 && col >1){
            newPosition = new ChessPosition(row + 1, col - 1);
            if (board.getPiece(newPosition) != null) {
                if (piece.getTeamColor() == WHITE && board.getPiece(newPosition).getTeamColor() == BLACK) {
                    if (row + 1 == 8) {
                        addPromotionMoves(chessMoves, new ChessPosition(row, col), new ChessPosition(row + 1, col - 1));
                    } else {
                        chessMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row + 1, col - 1), null));
                    }
                }
            }
        }
//        checking for Black capture
        if (row>1 && col <8){
            newPosition = new ChessPosition(row - 1, col + 1);
            if (board.getPiece(newPosition) != null) {
                if (piece.getTeamColor() == BLACK && board.getPiece(newPosition).getTeamColor() == WHITE) {
                    if (row - 1 == 1) {
                        addPromotionMoves(chessMoves, new ChessPosition(row, col), new ChessPosition(row - 1, col + 1));
                    } else {
                        chessMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row - 1, col + 1), null));
                    }
                }
            }
        }
        if (row > 1 && col > 1) {
            newPosition = new ChessPosition(row - 1, col - 1);
            if (board.getPiece(newPosition) != null) {
                if (piece.getTeamColor() == BLACK && board.getPiece(newPosition).getTeamColor() == WHITE) {
                    if (row - 1 == 1) {
                        addPromotionMoves(chessMoves, new ChessPosition(row, col), new ChessPosition(row - 1, col - 1));
                    } else {
                        chessMoves.add(new ChessMove(new ChessPosition(row, col), new ChessPosition(row - 1, col - 1), null));
                    }
                }
            }
        }
        return chessMoves;
    }

    public static Collection<ChessMove> rookMoveCalculator(ChessPiece piece, ChessBoard board, ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();

        int[][] possibleDirections = {{1,0},{0,1},{-1,0},{0,-1}};

        return generalCalculator(piece, board, row, col, possibleDirections);
    }

    public static Collection<ChessMove> queenMoveCalculator(ChessPiece piece, ChessBoard board, ChessPosition position) {
        Collection<ChessMove> chessMoves = rookMoveCalculator(piece, board, position);
        chessMoves.addAll(bishopMoveCalculator(piece, board, position));
        return chessMoves;
    }

}