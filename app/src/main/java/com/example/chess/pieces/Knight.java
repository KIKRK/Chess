package com.example.chess.pieces;

import com.example.chess.Square;
import com.example.chess.Player;

import java.util.HashSet;

public class Knight extends Piece {

    public Knight(Player player) {
        super(player);
    }

    @Override
    public HashSet<Square> possibleMoves(Square[][] squares, Square currentSquare) {
        HashSet<Square> possibleMoves = new HashSet<>();
        int row = currentSquare.getRow();
        int column = currentSquare.getColumn();
        Player currentPlayer = currentSquare.getPiece().getPlayer();

        // in order: upRight, rightUp, rightDown, downRight, downLeft, leftDown, leftUp, upLeft
        if (row < 6 && column < 7
                && (squares[row + 2][column + 1].getPiece() == null
                 || squares[row + 2][column + 1].getPiece().getPlayer() != currentPlayer)){
            possibleMoves.add(squares[row + 2][column + 1]);
        }

        if (row < 7 && column < 6
                && (squares[row + 1][column + 2].getPiece() == null
                 || squares[row + 1][column + 2].getPiece().getPlayer() != currentPlayer)){
            possibleMoves.add(squares[row + 1][column + 2]);
        }

        if (row > 0 && column < 6
                && (squares[row - 1][column + 2].getPiece() == null
                 || squares[row - 1][column + 2].getPiece().getPlayer() != currentPlayer)){
            possibleMoves.add(squares[row - 1][column + 2]);
        }

        if (row > 1 && column < 7
                && (squares[row - 2][column + 1].getPiece() == null
                 || squares[row - 2][column + 1].getPiece().getPlayer() != currentPlayer)){
            possibleMoves.add(squares[row - 2][column + 1]);
        }

        if (row > 1 && column > 0
                && (squares[row - 2][column - 1].getPiece() == null
                 || squares[row - 2][column - 1].getPiece().getPlayer() != currentPlayer)){
            possibleMoves.add(squares[row - 2][column - 1]);
        }

        if (row > 0 && column > 1
                && (squares[row - 1][column - 2].getPiece() == null
                 || squares[row - 1][column - 2].getPiece().getPlayer() != currentPlayer)){
            possibleMoves.add(squares[row - 1][column - 2]);
        }

        if (row < 7 && column > 1
                && (squares[row + 1][column - 2].getPiece() == null
                 || squares[row + 1][column - 2].getPiece().getPlayer() != currentPlayer)){
            possibleMoves.add(squares[row + 1][column - 2]);
        }

        if (row < 6 && column > 0
                && (squares[row + 2][column - 1].getPiece() == null
                 || squares[row + 2][column - 1].getPiece().getPlayer() != currentPlayer)){
            possibleMoves.add(squares[row + 2][column - 1]);
        }

        return possibleMoves;
    }

}
