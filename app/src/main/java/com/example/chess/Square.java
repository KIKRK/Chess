package com.example.chess;

import android.content.Context;

import com.example.chess.pieces.Bishop;
import com.example.chess.pieces.King;
import com.example.chess.pieces.Knight;
import com.example.chess.pieces.Pawn;
import com.example.chess.pieces.Piece;
import com.example.chess.pieces.Queen;
import com.example.chess.pieces.Rook;

import java.io.Serializable;
import java.util.HashSet;


public class Square extends androidx.appcompat.widget.AppCompatButton implements Serializable {

    private Piece piece;
    private int row;
    private int column;

    public Square(Context context, int row, int column) {
        super(context);
        this.row = row;
        this.column = column;
    }


    public Piece getPiece() {
        return piece;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }


    // Each piece is visualised by relevant Unicode character.
    public void setPiece(Piece piece) {
        this.piece = piece;

        if (piece.getPlayer() == Player.WHITE) {
            if (piece instanceof Pawn) {
                setText("\u2659");
            } else if (piece instanceof Rook) {
                setText("\u2656");
            } else if (piece instanceof Knight) {
                setText("\u2658");
            } else if (piece instanceof Bishop) {
                setText("\u2657");
            } else if (piece instanceof Queen) {
                setText("\u2655");
            } else if (piece instanceof King) {
                setText("\u2654");
            }
        } else if (piece.getPlayer() == Player.BLACK) {
            if (piece instanceof Pawn) {
                setText("\u265F");
            } else if (piece instanceof Rook) {
                setText("\u265C");
            } else if (piece instanceof Knight) {
                setText("\u265E");
            } else if (piece instanceof Bishop) {
                setText("\u265D");
            } else if (piece instanceof Queen) {
                setText("\u265B");
            } else if (piece instanceof King) {
                setText("\u265A");
            }
        }
    }

    public void removePiece(){
        setText("");
        piece = null;
    }



    // Section below contains methods to determine possible moves available for the piece on the given square.
    // Defined methods are used in some implementations of the abstract possibleMoves method.


    // Method returning possible moves by scanning single supplied direction square-by-square using checkButton method.
    private HashSet<Square> scanDirection(Square[][] squares, MoveDirection direction, Player currentPlayer) {

        HashSet<Square> possibleMoves = new HashSet<>();
        int currentRow = row;
        int currentColumn = column;

        int[] increments = getIncrements(direction);
        int rowIncr = increments[0];
        int columnIncr = increments[1];
        Square squareToCheck;

        currentRow += rowIncr;
        currentColumn += columnIncr;

        while ((currentRow >= 0 && currentRow <= 7) && (currentColumn >= 0 && currentColumn <= 7)){
            squareToCheck = squares[currentRow][currentColumn];
            if (checkButton(squareToCheck, possibleMoves, currentPlayer)) {
                currentRow += rowIncr;
                currentColumn += columnIncr;
            } else{
                break;
            }
        }
        return possibleMoves;
    }


    // Method scanning all supplied directions at once to return possible moves.
    public HashSet<Square> scanAll(Square[][] buttons, MoveDirection[] directions, Player currentPlayer) {

        HashSet<Square> possibleMoves = new HashSet<>();

        for (MoveDirection direction : directions) {
            possibleMoves.addAll(scanDirection(buttons, direction, currentPlayer));
        }
        return possibleMoves;
    }


    private boolean checkButton(Square buttonToCheck, HashSet<Square> possibleMoves, Player currentPlayer){
        if (buttonToCheck.getPiece() == null) {
            possibleMoves.add(buttonToCheck);
            return true;
        } else if (buttonToCheck.getPiece().getPlayer() != currentPlayer) {
            possibleMoves.add(buttonToCheck);
            return false;
        } else {
            return false;
        }
    }


    private int[] getIncrements(MoveDirection direction) {

        int rowIncr = 0;
        int columnIncr = 0;

        switch (direction) {
            case N:
                rowIncr = 1;
                break;
            case NE:
                rowIncr = 1;
                columnIncr = 1;
                break;
            case E:
                columnIncr = 1;
                break;
            case SE:
                rowIncr = -1;
                columnIncr = 1;
                break;
            case S:
                rowIncr = -1;
                break;
            case SW:
                rowIncr = -1;
                columnIncr = -1;
                break;
            case W:
                columnIncr = -1;
                break;
            case NW:
                rowIncr = 1;
                columnIncr = -1;
                break;
        }
        return new int[]{rowIncr, columnIncr};
    }




    // Section below contains methods to determine potential danger to the given square from opponent's pieces.
    // Defined methods are used during checks for check, checkmate & stalemate).

    // Method scanning single direction in search for opponent's bishops, rooks or queens to determine if square is in danger from them.
    private boolean squareInDangerSingleDirection(Square[][] squares, MoveDirection direction, Player player) {

        int currentRow = row;
        int currentColumn = column;

        int[] increments = getIncrements(direction);
        int rowIncr = increments[0];
        int columnIncr = increments[1];
        Square buttonToCheck;

        currentRow += rowIncr;
        currentColumn += columnIncr;

        while ((currentRow >= 0 && currentRow <= 7) && (currentColumn >= 0 && currentColumn <= 7)) {
            buttonToCheck = squares[currentRow][currentColumn];

            if (buttonToCheck.getPiece() == null) {
                currentRow += rowIncr;
                currentColumn += columnIncr;
            } else if (buttonToCheck.getPiece().getPlayer() == player){
                return false;
            } else {
                if ((direction == MoveDirection.N || direction == MoveDirection.E || direction == MoveDirection.S || direction == MoveDirection.W)
                        && (buttonToCheck.getPiece() instanceof Rook || buttonToCheck.getPiece() instanceof Queen)) {
                    return true;
                } else if ((direction == MoveDirection.NE || direction == MoveDirection.SE || direction == MoveDirection.SW || direction == MoveDirection.NW)
                        && (buttonToCheck.getPiece() instanceof Bishop || buttonToCheck.getPiece() instanceof Queen)) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        return false;

    }


    // Method scanning all supplied directions in search for opponent's bishops, rooks or queens to determine if square is in danger from them.
    private boolean squareInDangerAllDirections(Square[][] squares, MoveDirection[] directions, Player player) {

        for (MoveDirection direction : directions) {
            if (squareInDangerSingleDirection(squares, direction, player)){
                return true;
            }
        }
        return false;
    }


    // Method to determine if square is in danger from opponent's pawn diagonal attack.
    private boolean squareInDangerPawn(Square[][] squares, Player currentPlayer) {

        Square whiteLeftForward = null;
        Square whiteRightForward = null;
        Square blackLeftForward = null;
        Square blackRightForward = null;

        if (row < 7 && column > 0){
            whiteLeftForward = squares[row + 1][column - 1]; }

        if (row < 7 && column < 7){
            whiteRightForward = squares[row + 1][column + 1]; }

        if (row > 0 && column > 0){
            blackRightForward = squares[row - 1][column - 1]; }

        if (row > 0 && column < 7){
            blackLeftForward = squares[row - 1][column + 1]; }

        if (currentPlayer == Player.WHITE) {

            if (row < 7 && column > 0) {
                if (whiteLeftForward.getPiece() != null && whiteLeftForward.getPiece() instanceof Pawn
                        && whiteLeftForward.getPiece().getPlayer() == Player.BLACK) {
                    return true; } }

            if (row < 7 && column < 7) {
                if (whiteRightForward.getPiece() != null && whiteRightForward.getPiece() instanceof Pawn
                        && whiteRightForward.getPiece().getPlayer() == Player.BLACK) {
                    return true; } }

        } else if (currentPlayer == Player.BLACK) {

            if (row > 0 && column < 7) {
                if (blackLeftForward.getPiece() != null && blackLeftForward.getPiece() instanceof Pawn
                        && blackLeftForward.getPiece().getPlayer() == Player.WHITE) {
                    return true; } }

            if (row > 0 && column > 0) {
                if (blackRightForward.getPiece() != null && blackRightForward.getPiece() instanceof Pawn
                        && blackRightForward.getPiece().getPlayer() == Player.WHITE) {
                    return true; } }

        }

        return false;

    }


    // Method to determine if square is in danger from opponent's knight attack.
    private boolean squareInDangerKnight(Square[][] squares, Player currentPlayer) {

        // in order: upRight, rightUp, rightDown, downRight, downLeft, leftDown, leftUp, upLeft
        if (row < 6 && column < 7) {
            if (squares[row + 2][column + 1].getPiece() instanceof Knight
                    && squares[row + 2][column + 1].getPiece().getPlayer() != currentPlayer)
            {
                return true; } }

        if (row < 7 && column < 6) {
            if (squares[row + 1][column + 2].getPiece() instanceof Knight
                    && squares[row + 1][column + 2].getPiece().getPlayer() != currentPlayer)
            {
                return true; } }

        if (row > 0 && column < 6) {
            if (squares[row - 1][column + 2].getPiece() instanceof Knight
                    && squares[row - 1][column + 2].getPiece().getPlayer() != currentPlayer)
            {
                return true; } }

        if (row > 1 && column < 7) {
            if (squares[row - 2][column + 1].getPiece() instanceof Knight
                    && squares[row - 2][column + 1].getPiece().getPlayer() != currentPlayer)
            {
                return true; } }

        if (row > 1 && column > 0) {
            if (squares[row - 2][column - 1].getPiece() instanceof Knight
                    && squares[row - 2][column - 1].getPiece().getPlayer() != currentPlayer)
            {
                return true; } }

        if (row > 0 && column > 1) {
            if (squares[row - 1][column - 2].getPiece() instanceof Knight
                    && squares[row - 1][column - 2].getPiece().getPlayer() != currentPlayer)
            {
                return true; } }

        if (row < 7 && column > 1) {
            if (squares[row + 1][column - 2].getPiece() instanceof Knight
                    && squares[row + 1][column - 2].getPiece().getPlayer() != currentPlayer)
            {
                return true; } }

        if (row < 6 && column > 0) {
            if (squares[row + 2][column - 1].getPiece() instanceof Knight
                    && squares[row + 2][column - 1].getPiece().getPlayer() != currentPlayer)
            {
                return true; } }

        return false;

    }

    // Method to determine if square is in danger from opponent's king attack.
    public boolean squareInDangerKing(Square[][] buttons, Player currentPlayer){

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int adjacentRow = row + i;
                int adjacentColumn = column + j;
                if (adjacentRow >= 0 && adjacentRow <= 7 && adjacentColumn >= 0 && adjacentColumn <= 7) {
                    if (buttons[adjacentRow][adjacentColumn].getPiece() != null
                            && buttons[adjacentRow][adjacentColumn].getPiece() instanceof King
                            && buttons[adjacentRow][adjacentColumn].getPiece().getPlayer() != currentPlayer)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    // Method to determine if square is in danger from any opponent's piece, regardless of its type.
    public boolean squareInDanger(Square[][] buttons, Player currentPlayer) {
        MoveDirection[] possibleDirections = {MoveDirection.N, MoveDirection.E, MoveDirection.S, MoveDirection.W,
                MoveDirection.NE, MoveDirection.SE, MoveDirection.SW, MoveDirection.NW};

        return (squareInDangerAllDirections(buttons, possibleDirections, currentPlayer)
                || squareInDangerKnight(buttons, currentPlayer)
                || squareInDangerPawn(buttons, currentPlayer)
                || squareInDangerKing(buttons, currentPlayer));
    }

    // Method to determine if square is in danger from any opponent's piece but king.
    public boolean squareInDangerNoKing(Square[][] buttons, Player currentPlayer) {
        MoveDirection[] possibleDirections = {MoveDirection.N, MoveDirection.E, MoveDirection.S, MoveDirection.W,
                MoveDirection.NE, MoveDirection.SE, MoveDirection.SW, MoveDirection.NW};

        return (squareInDangerAllDirections(buttons, possibleDirections, currentPlayer)
                || squareInDangerKnight(buttons, currentPlayer)
                || squareInDangerPawn(buttons, currentPlayer));
    }


}

