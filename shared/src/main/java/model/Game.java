package model;

import chess.ChessGame;

public record Game(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game, Integer gameOver) {
    public Game updateWhiteUsername(String newWhiteUsername) {
        return new Game(this.gameID, newWhiteUsername, this.blackUsername, this.gameName, this.game, this.gameOver);
    }

    public Game updateBlackUsername(String newBlackUsername) {
        return new Game(this.gameID, this.whiteUsername, newBlackUsername, this.gameName, this.game, this.gameOver);
    }

    public Game updateGameOver(){
        return new Game(this.gameID, this.whiteUsername, this.blackUsername, this.gameName, this.game, 1);
    }
}
