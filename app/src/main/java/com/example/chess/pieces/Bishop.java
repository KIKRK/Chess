package com.example.chess.pieces;

import com.example.chess.Square;
import com.example.chess.MoveDirection;
import com.example.chess.Player;

import java.util.HashSet;

public class Bishop extends Piece {

    public Bishop(Player player) {
        super(player);
    }

    @Override
    public HashSet<Square> possibleMoves(Square[][] squares, Square currentSquare) {
        MoveDirection[] possibleDirections = {MoveDirection.NE, MoveDirection.SE, MoveDirection.SW, MoveDirection.NW};
        return currentSquare.scanAll(squares, possibleDirections, currentSquare.getPiece().getPlayer());
    }


}
