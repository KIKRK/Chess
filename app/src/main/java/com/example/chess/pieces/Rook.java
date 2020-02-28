package com.example.chess.pieces;

import com.example.chess.Square;
import com.example.chess.MoveDirection;
import com.example.chess.Player;

import java.util.HashSet;

public class Rook extends Piece {

    private boolean alreadyMoved;

    public Rook(Player player, boolean alreadyMoved) {
        super(player);
        this.alreadyMoved = alreadyMoved;
    }

    public boolean isAlreadyMoved() {
        return alreadyMoved;
    }

    public void setAlreadyMoved(boolean alreadyMoved) {
        this.alreadyMoved = alreadyMoved;
    }

    @Override
    public HashSet<Square> possibleMoves(Square[][] squares, Square currentSquare) {
        MoveDirection[] possibleDirections = {MoveDirection.N, MoveDirection.E, MoveDirection.S, MoveDirection.W};
        return currentSquare.scanAll(squares, possibleDirections, currentSquare.getPiece().getPlayer());
    }

}
