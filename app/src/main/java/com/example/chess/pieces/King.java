package com.example.chess.pieces;

import com.example.chess.Square;
import com.example.chess.MoveDirection;
import com.example.chess.Player;

import java.util.HashSet;

public class King extends Piece {

    private boolean alreadyMoved;

    public King(Player player, boolean alreadyMoved) {
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
        MoveDirection[] possibleDirections = {MoveDirection.N, MoveDirection.E, MoveDirection.S, MoveDirection.W,
                MoveDirection.NE, MoveDirection.SE, MoveDirection.SW, MoveDirection.NW};
        int xPosition = currentSquare.getRow();
        int yPosition = currentSquare.getColumn();

        HashSet<Square> scannedPossibilities = currentSquare.scanAll(squares, possibleDirections, currentSquare.getPiece().getPlayer());
        HashSet<Square> adjacentSquares = new HashSet<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int adjacentRow = xPosition + i;
                int adjacentColumn = yPosition + j;
                if (adjacentRow >= 0 && adjacentRow <= 7 && adjacentColumn >= 0 && adjacentColumn <= 7) {
                    adjacentSquares.add(squares[adjacentRow][adjacentColumn]);
                }
            }
        }
        scannedPossibilities.retainAll(adjacentSquares);

        return scannedPossibilities;
    }




}
