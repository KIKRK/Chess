package com.example.chess.pieces;

import com.example.chess.Move;
import com.example.chess.Player;

import java.io.Serializable;

public abstract class Piece implements Move, Serializable {

    private Player player;

    public Piece(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}
