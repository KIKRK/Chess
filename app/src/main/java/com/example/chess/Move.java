package com.example.chess;

import java.util.HashSet;

public interface Move {

    // Abstract method describing a set of possible destinations based on board situation and current position of a piece.
    HashSet<Square> possibleMoves(Square[][] squares, Square currentSquare);

}
