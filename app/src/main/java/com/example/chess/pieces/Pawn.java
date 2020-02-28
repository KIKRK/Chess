package com.example.chess.pieces;

import com.example.chess.Square;
import com.example.chess.Player;

import java.util.HashSet;

public class Pawn extends Piece {

    public Pawn(Player player) {
        super(player);
    }

    @Override
    public HashSet<Square> possibleMoves(Square[][] squares, Square currentSquare) {

        HashSet<Square> possibleMoves = new HashSet<>();
        int row = currentSquare.getRow();
        int column = currentSquare.getColumn();

        Square whiteForwardForward = null;
        Square blackForwardForward = null;
        Square whiteForward = null;
        Square whiteLeftForward = null;
        Square whiteRightForward = null;
        Square blackForward = null;
        Square blackLeftForward = null;
        Square blackRightForward = null;

        if (row < 7){
            whiteForward = squares[row+1][column]; }

        if (row > 0) {
            blackForward = squares[row - 1][column]; }

        if (row < 6){
            whiteForwardForward = squares[row+2][column]; }

        if (row > 1) {
            blackForwardForward = squares[row-2][column]; }

        if (column > 0){
            whiteLeftForward = squares[row+1][column-1];
            blackRightForward = squares[row-1][column-1]; }

        if (column < 7){
            whiteRightForward = squares[row+1][column+1];
            blackLeftForward = squares[row-1][column+1]; }

        if (getPlayer() == Player.WHITE){

            if (whiteForward.getPiece() == null) {
                possibleMoves.add(whiteForward); }

            if (row == 1 && whiteForwardForward.getPiece() == null){
                possibleMoves.add(whiteForwardForward); }

            if (column > 0 && whiteLeftForward.getPiece() != null && whiteLeftForward.getPiece().getPlayer() == Player.BLACK){
                possibleMoves.add(whiteLeftForward); }

            if (column < 7 && whiteRightForward.getPiece() != null && whiteRightForward.getPiece().getPlayer() == Player.BLACK){
                possibleMoves.add(whiteRightForward); }

        } else if (getPlayer() == Player.BLACK) {

            if (blackForward.getPiece() == null) {
                possibleMoves.add(blackForward); }

            if (row == 6 && blackForwardForward.getPiece() == null){
                possibleMoves.add(blackForwardForward); }

            if (column < 7 && blackLeftForward.getPiece() != null && blackLeftForward.getPiece().getPlayer() == Player.WHITE) {
                possibleMoves.add(blackLeftForward); }

            if (column > 0 && blackRightForward.getPiece() != null && blackRightForward.getPiece().getPlayer() == Player.WHITE) {
                possibleMoves.add(blackRightForward); }
        }

        return possibleMoves;
    }

}
