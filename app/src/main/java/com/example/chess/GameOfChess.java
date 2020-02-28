package com.example.chess;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.example.chess.pieces.King;
import com.example.chess.pieces.Pawn;
import com.example.chess.pieces.Piece;
import com.example.chess.pieces.Rook;

import java.util.ArrayList;
import java.util.HashSet;

public class GameOfChess implements View.OnClickListener{

    private ChessBoard board;
    private Square currentlySelectedSquare;
    private Animation selectedPieceAnimation;
    private Square whiteKingSquare;
    private Square blackKingSquare;
    private Square enPassantSquare;
    private ArrayList<Square> whitePieceSquares;
    private ArrayList<Square> blackPieceSquares;
    private Player currentPlayer;
    private Square pawnToPromoteSquare;
    private int moveNumber;

    private Context context;

    public GameOfChess(Context context) {
        this.context = context;
    }

    public void loadSavedState(View view, Resources resources, Bundle savedInstanceState) {
        ChessBoard tempBoard = (ChessBoard) savedInstanceState.getSerializable("BOARD");
        whiteKingSquare = (Square) savedInstanceState.getSerializable("WHITE_KING_SQUARE");
        blackKingSquare = (Square) savedInstanceState.getSerializable("BLACK_KING_SQUARE");
        enPassantSquare = (Square) savedInstanceState.getSerializable("EN_PASSANT_SQUARE");
        pawnToPromoteSquare = (Square) savedInstanceState.getSerializable("PAWN_TO_PROMOTE_SQUARE");
        whitePieceSquares = (ArrayList<Square>) savedInstanceState.getSerializable("WHITE_PIECE_SQUARES");
        blackPieceSquares = (ArrayList<Square>) savedInstanceState.getSerializable("BLACK_PIECE_SQUARES");
        currentPlayer = (Player) savedInstanceState.getSerializable("CURRENT_PLAYER");
        moveNumber = savedInstanceState.getInt("MOVE_NUMBER");

        selectedPieceAnimation = AnimationUtils.loadAnimation(context, R.anim.selected_piece_animation);
        currentlySelectedSquare = null;

        ConstraintLayout layout = view.findViewById(R.id.constraintBoard);
        int screenOrientation = resources.getConfiguration().orientation;

        board = new ChessBoard(8, 8, layout, screenOrientation, context);

        Square[][] squares = board.getSquares();
        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                squares[i][j].setOnClickListener(this);
                if (tempBoard != null) {
                    if (tempBoard.getSquares()[i][j].getPiece() != null) {
                        squares[i][j].setPiece(tempBoard.getSquares()[i][j].getPiece());
                    } else {
                        squares[i][j].removePiece();
                    }
                }
            }
        }
    }

    public void saveState(Bundle outState){
        outState.putSerializable("BOARD", board);
        outState.putSerializable("WHITE_KING_SQUARE", whiteKingSquare);
        outState.putSerializable("BLACK_KING_SQUARE", blackKingSquare);
        outState.putSerializable("EN_PASSANT_SQUARE", enPassantSquare);
        outState.putSerializable("PAWN_TO_PROMOTE_SQUARE", pawnToPromoteSquare);
        outState.putSerializable("WHITE_PIECE_SQUARES", whitePieceSquares);
        outState.putSerializable("BLACK_PIECE_SQUARES", blackPieceSquares);
        outState.putSerializable("CURRENT_PLAYER", currentPlayer);
        outState.putInt("MOVE_NUMBER", moveNumber);
    }


    public void initialSetting(View view, Resources resources){
        ConstraintLayout layout = view.findViewById(R.id.constraintBoard);
        int screenOrientation = resources.getConfiguration().orientation;

        board = new ChessBoard(8, 8, layout, screenOrientation, context);

        selectedPieceAnimation = AnimationUtils.loadAnimation(context, R.anim.selected_piece_animation);
        currentlySelectedSquare = null;

        whiteKingSquare = board.getSquares()[0][4];
        blackKingSquare = board.getSquares()[7][4];
        currentPlayer = Player.WHITE;
        enPassantSquare = null;
        pawnToPromoteSquare = null;

        whitePieceSquares = new ArrayList<>();
        blackPieceSquares = new ArrayList<>();

        moveNumber = 1;

        for (int row = 0; row <= 1; row++) {
            for (int column = 0; column <= 7; column++) {
                whitePieceSquares.add(board.getSquares()[row][column]);
            }
        }

        for (int row = 6; row <= 7; row++) {
            for (int column = 0; column <= 7; column++) {
                blackPieceSquares.add(board.getSquares()[row][column]);
            }
        }

        for (int row = 0; row <= 7; row++) {
            for (int column = 0; column <= 7; column++) {
                board.getSquares()[row][column].setOnClickListener(this);
            }
        }
    }


    public void onClick(View v) {
        Square selectedSquare = (Square) v;

        if ((selectedSquare.getPiece() != null && selectedSquare.getPiece().getPlayer() == currentPlayer) || currentlySelectedSquare != null) {

            if (currentlySelectedSquare == null) {

                if (selectedSquare.getPiece() != null) {
                    newSelection(selectedSquare);
                }

            } else if (selectedSquare.getPiece() == null) {

                if (!(currentlySelectedSquare.getPiece().possibleMoves(board.getSquares(), currentlySelectedSquare).contains(selectedSquare))) {

                    Piece pieceToMove = currentlySelectedSquare.getPiece();

                    if (pieceToMove instanceof Pawn && enPassantSquare != null
                            && currentlySelectedSquare.getRow() == enPassantSquare.getRow()
                            && (currentlySelectedSquare.getColumn() == enPassantSquare.getColumn() - 1
                            || currentlySelectedSquare.getColumn() == enPassantSquare.getColumn() + 1)
                            && ( (currentPlayer == Player.WHITE && selectedSquare.getRow() == enPassantSquare.getRow() + 1)
                            || (currentPlayer == Player.BLACK && selectedSquare.getRow() == enPassantSquare.getRow() - 1) ) ){

                        Piece pieceToBeCaptured = enPassantSquare.getPiece();

                        makeMove(selectedSquare, pieceToMove);

                        if ((getCurrentKingSquare().squareInDanger(board.getSquares(), currentPlayer))) {
                            revertIllegalMove(selectedSquare, pieceToMove, pieceToBeCaptured, false);

                        } else {
                            updatesAfterMove(selectedSquare, null, true, pieceToMove);
                        }

                    } else if (pieceToMove instanceof King) {

                        Square rookSquare = null;

                        if ((currentPlayer == Player.WHITE && selectedSquare.getRow() == 0)
                                || (currentPlayer == Player.BLACK && selectedSquare.getRow() == 7)) {
                            if (selectedSquare.getColumn() == 6) {
                                rookSquare = board.getSquares()[currentlySelectedSquare.getRow()][7];
                            } else if (selectedSquare.getColumn() == 2) {
                                rookSquare = board.getSquares()[currentlySelectedSquare.getRow()][0];
                            }
                        }

                        if (possibleCastling(currentlySelectedSquare, rookSquare)){
                            castling(rookSquare);
                            updatesAfterMove(selectedSquare, rookSquare, false, pieceToMove);
                        }

                    }

                } else {

                    boolean stateChange = false;
                    Piece pieceToMove = currentlySelectedSquare.getPiece();
                    Piece pieceToBeCaptured = selectedSquare.getPiece();

                    if (currentlySelectedSquare.getPiece().possibleMoves(board.getSquares(), currentlySelectedSquare).contains(selectedSquare)) {
                        stateChange = makeMove(selectedSquare, pieceToMove);
                    }

                    if ((getCurrentKingSquare().squareInDanger(board.getSquares(), currentPlayer))){
                        revertIllegalMove(selectedSquare, pieceToMove, pieceToBeCaptured, stateChange);

                    } else if (selectedSquare.getPiece() instanceof Pawn &&
                            ((currentPlayer == Player.WHITE && selectedSquare.getRow() == 7)
                                    || (currentPlayer == Player.BLACK && selectedSquare.getRow() == 0))) {
                        pawnPromotion(selectedSquare);

                    } else {
                        updatesAfterMove(selectedSquare, null, false, pieceToMove);
                    }

                }

            } else if (selectedSquare.getPiece() != null) {

                if (currentlySelectedSquare.getPiece().getPlayer() == selectedSquare.getPiece().getPlayer()) {
                    changeSelection(selectedSquare);

                } else if (currentlySelectedSquare.getPiece().possibleMoves(board.getSquares(), currentlySelectedSquare).contains(selectedSquare)) {
                    boolean stateChange = false;
                    Piece pieceToMove = currentlySelectedSquare.getPiece();
                    Piece pieceToBeCaptured = selectedSquare.getPiece();

                    if (currentlySelectedSquare.getPiece().possibleMoves(board.getSquares(), currentlySelectedSquare).contains(selectedSquare)) {
                        stateChange = makeMove(selectedSquare, pieceToMove);
                    }

                    if ((getCurrentKingSquare().squareInDanger(board.getSquares(), currentPlayer))){
                        revertIllegalMove(selectedSquare, pieceToMove, pieceToBeCaptured, stateChange);

                    } else if (selectedSquare.getPiece() instanceof Pawn &&
                            ((currentPlayer == Player.WHITE && selectedSquare.getRow() == 7)
                                    || (currentPlayer == Player.BLACK && selectedSquare.getRow() == 0))) {
                        pawnPromotion(selectedSquare);
                    } else {
                        updatesAfterMove(selectedSquare, null, false, pieceToMove);
                    }

                }
            }
        }
    }



    // Set of common activities to perform after each move.
    private void updatesAfterMove (Square selectedSquare, Square rookSquare, boolean enPassantCapture, Piece pieceToMove){

        // Update list of occupied squares according to relevant method.
        if (rookSquare == null && !enPassantCapture){
            squareUpdate(selectedSquare);
        } else if (rookSquare != null){
            squareUpdateCastling(rookSquare);
        } else {
            squareUpdateEnPassant(selectedSquare);
        }

        // Disable en passant capture by clearing enPassantSquare if last move was not a pawn move by two squares.
        if (!(pieceToMove instanceof Pawn
                && ((currentPlayer == Player.WHITE && selectedSquare.getRow() == currentlySelectedSquare.getRow() + 2)
                || (currentPlayer == Player.BLACK && selectedSquare.getRow() == currentlySelectedSquare.getRow() - 2)))) {
            clearEnPassantSquare();
        }

        currentlySelectedSquare = null;

        // If opponent's king is attacked after move: find out if it is check or checkmate and finish game in case of checkmate.
        if (getOppositeKingSquare().squareInDanger(board.getSquares(), getOppositePlayer())) {
            if (checkForCheck(selectedSquare)) {
                return;
            }
        }

        nextPlayer();
        checkForStalemate();

    }

    /*
    Method moving piece to selected destination,
    with additional code to support special cases:
    - en passant capture,
    - each pawn first move by 2 squares forward,
    - king/rook state change after move (to disable castling in the future).
     */

    private boolean makeMove(Square selectedSquare, Piece pieceToMove){
        selectedSquare.setPiece(pieceToMove);
        currentlySelectedSquare.removePiece();
        currentlySelectedSquare.clearAnimation();
        boolean stateChange = false;

        if (pieceToMove instanceof Pawn) {
            if (enPassantSquare != null
                    && currentlySelectedSquare.getRow() == enPassantSquare.getRow()
                    && (currentlySelectedSquare.getColumn() == enPassantSquare.getColumn() - 1
                    || currentlySelectedSquare.getColumn() == enPassantSquare.getColumn() + 1)
                    && ((currentPlayer == Player.WHITE && selectedSquare.getRow() == enPassantSquare.getRow() + 1)
                    || (currentPlayer == Player.BLACK && selectedSquare.getRow() == enPassantSquare.getRow() - 1))) {
                enPassantSquare.removePiece();
            } else if ((currentPlayer == Player.WHITE && selectedSquare.getRow() == currentlySelectedSquare.getRow() + 2)
                    || (currentPlayer == Player.BLACK && selectedSquare.getRow() == currentlySelectedSquare.getRow() - 2)) {
                enPassantSquare = selectedSquare;
            }
        }

        if (pieceToMove instanceof King) {
            updateKingSquare(selectedSquare);
            if (!((King) pieceToMove).isAlreadyMoved()){
                stateChange = true;
                ((King) pieceToMove).setAlreadyMoved(true);
            }
        }

        if (pieceToMove instanceof Rook && !((Rook) pieceToMove).isAlreadyMoved()) {
            stateChange = true;
            ((Rook) pieceToMove).setAlreadyMoved(true);
        }

        return stateChange;
    }

    // Method reverting all changes made in last move and informing user that it was illegal.
    // In program this method is run (if at all) directly after makeMove, so the user does not see the actual illegal move
    // and notices only toast information about it.
    private void revertIllegalMove(Square selectedSquare, Piece pieceToMove, Piece pieceToBeCaptured, boolean stateChange){

        if (pieceToBeCaptured != null) {
            selectedSquare.setPiece(pieceToBeCaptured);
        } else{
            selectedSquare.removePiece();
        }

        currentlySelectedSquare.setPiece(pieceToMove);

        if (pieceToMove instanceof Pawn) {
            if (enPassantSquare != null
                    && currentlySelectedSquare.getRow() == enPassantSquare.getRow()
                    && (currentlySelectedSquare.getColumn() == enPassantSquare.getColumn() - 1
                    || currentlySelectedSquare.getColumn() == enPassantSquare.getColumn() + 1)
                    && ((currentPlayer == Player.WHITE && selectedSquare.getRow() == enPassantSquare.getRow() + 1)
                    || (currentPlayer == Player.BLACK && selectedSquare.getRow() == enPassantSquare.getRow() - 1))) {
                enPassantSquare.setPiece(pieceToBeCaptured);
                selectedSquare.removePiece();
            } else if ((currentPlayer == Player.WHITE && selectedSquare.getRow() == currentlySelectedSquare.getRow() + 2)
                    || (currentPlayer == Player.BLACK && selectedSquare.getRow() == currentlySelectedSquare.getRow() - 2)) {
                clearEnPassantSquare();
            }
        }

        if (pieceToMove instanceof King) {
            updateKingSquare(currentlySelectedSquare);
            if (stateChange) {
                ((King) pieceToMove).setAlreadyMoved(false);
            }
        }

        if (pieceToMove instanceof Rook && stateChange) {
            ((Rook) pieceToMove).setAlreadyMoved(false);
        }

        currentlySelectedSquare = null;
        Toast.makeText(context, "Illegal move", Toast.LENGTH_SHORT).show();
    }


    // Method checking for check and checkmate.
    public boolean checkForCheck(Square selectedSquare) {
        boolean checkmate = true;

        // If the attacked king may flee to any available adjacent square (also by capturing adjacent opponent's piece)
        // and be safe after this move -> no checkmate
        Piece king = getOppositeKingSquare().getPiece();
        getOppositeKingSquare().removePiece();
        for (Square adjacentSquare : adjacentAvailableSquares(board.getSquares(), getOppositeKingSquare())) {
            if (! adjacentSquare.squareInDanger(board.getSquares(), getOppositePlayer())) {
                checkmate = false;
                break;
            }
        }
        getOppositeKingSquare().setPiece(king);

        // If the piece moved on selectedSquare (that often, but not always, is the attacking piece)
        // may be captured by the defendant's piece other than king (capture by king has already been checked above),
        // check if such capture leaves the king in safe position; if it is the case -> no checkmate
        if (checkmate) {
            if (selectedSquare.squareInDangerNoKing(board.getSquares(), currentPlayer)) {
                for (Square square : getOppositePieceSquares()) {
                    Piece tempPiece = square.getPiece();
                    if (tempPiece.possibleMoves(board.getSquares(), square).contains(selectedSquare)) {
                        Piece actualPiece = selectedSquare.getPiece();
                        square.removePiece();
                        selectedSquare.setPiece(tempPiece);
                        if (!getOppositeKingSquare().squareInDanger(board.getSquares(), getOppositePlayer())) {
                            checkmate = false;
                        }
                        selectedSquare.setPiece(actualPiece);
                        square.setPiece(tempPiece);
                        break;
                    }
                }
            }
        }

        // If king may be shielded from opponent's attacking piece by other friendly piece moving anywhere in between the king and the attacking piece
        // check if such shielding leaves the king in safe position; if it is the case -> no checkmate.
        if (checkmate) {
            for (Square squareInBetween : squaresInBetween(board.getSquares(), getOppositeKingSquare(), selectedSquare)) {
                ArrayList<Square> squaresToCheck = getOppositePieceSquares();
                squaresToCheck.remove(getOppositeKingSquare());
                for (Square square : squaresToCheck) {
                    Piece tempPiece = square.getPiece();
                    if (tempPiece.possibleMoves(board.getSquares(), square).contains(squareInBetween)) {
                        square.removePiece();
                        squareInBetween.setPiece(tempPiece);
                        if (!getOppositeKingSquare().squareInDanger(board.getSquares(), getOppositePlayer())) {
                            checkmate = false;
                        }
                        squareInBetween.removePiece();
                        square.setPiece(tempPiece);
                        break;
                    }
                }
            }
        }

        if (checkmate) {
            Toast.makeText(context, "Checkmate", Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(context, "Check", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    // Method checking for stalemate.
    public boolean checkForStalemate(){
        Square kingSquare;
        ArrayList<Square> pieceSquares;
        if (currentPlayer == Player.WHITE){
            kingSquare = whiteKingSquare;
            pieceSquares = whitePieceSquares;
        } else {
            kingSquare = blackKingSquare;
            pieceSquares = blackPieceSquares;
        }

        // If at least one square adjacent to king is available and not in danger -> no stalemate.
        for (Square adjacentSquare : adjacentAvailableSquares(board.getSquares(), kingSquare)){
            if (! adjacentSquare.squareInDanger(board.getSquares(), currentPlayer)){
                return false;
            }
        }

        // If king is constraint, check other remaining pieces in search for a legal move;
        // if such move can be found -> no stalemate.
        for (Square pieceSquare : pieceSquares){
            if (pieceSquare != kingSquare && pieceSquare.getPiece().possibleMoves(board.getSquares(), pieceSquare).size() > 0){
                Piece tempPiece = pieceSquare.getPiece();
                Piece tempPiece2 = null;
                for (Square potentialPosition : pieceSquare.getPiece().possibleMoves(board.getSquares(), pieceSquare)){
                    pieceSquare.removePiece();
                    if (potentialPosition.getPiece() != null) {
                        tempPiece2 = potentialPosition.getPiece();
                    }
                    potentialPosition.setPiece(tempPiece);
                    if (kingSquare.squareInDanger(board.getSquares(), currentPlayer)) {
                        pieceSquare.setPiece(tempPiece);
                        potentialPosition.removePiece();
                        if (tempPiece2 != null){
                            potentialPosition.setPiece(tempPiece2);
                        }
                    } else {
                        pieceSquare.setPiece(tempPiece);
                        potentialPosition.removePiece();
                        if (tempPiece2 != null) {
                            potentialPosition.setPiece(tempPiece2);
                        }
                        return false;
                    }
                }
            }
        }

        Toast.makeText(context, "Stalemate", Toast.LENGTH_LONG).show();

        return true;
    }

    // Method checking conditions that are necessary for castling.
    private boolean possibleCastling(Square kingSquare, Square rookSquare){

        // If rook's square is empty. -> castling impossible.
        if (rookSquare == null){
            return false;
        }

        King king = (King) kingSquare.getPiece();
        Rook rook = (Rook) rookSquare.getPiece();
        int kingRow = kingSquare.getRow();
        int kingColumn = kingSquare.getColumn();
        int rookColumn = rookSquare.getColumn();

        /*
        If either:
        - rook square contains a piece other than rook,
        - king has already moved,
        - rook has already moved,
        - king is in check,
        castling is impossible.
         */
        if (!(rookSquare.getPiece() instanceof Rook)
                || king.isAlreadyMoved()
                || rook.isAlreadyMoved()
                || kingSquare.squareInDanger(board.getSquares(), currentPlayer)){
            return false;
        }

        // If squares passed by king are in danger -> castling is impossible.
        HashSet<Square> squaresToCheck = new HashSet<>();
        if (kingColumn > rookColumn){
            squaresToCheck.add(board.getSquares()[kingRow][kingColumn - 1]);
            squaresToCheck.add(board.getSquares()[kingRow][kingColumn - 2]);
        } else{
            squaresToCheck.add(board.getSquares()[kingRow][kingColumn + 1]);
            squaresToCheck.add(board.getSquares()[kingRow][kingColumn + 2]);
        }

        for (Square squareToCheck : squaresToCheck){
            if (squareToCheck.squareInDanger(board.getSquares(), currentPlayer)){
                return false;
            }
        }

        return true;
    }

    // Perform castling - move the king two squares left/right (depending on the argument)
    // and set rook accordingly on adjacent square to the right/left of the king.
    private void castling(Square rookSquare){
        ((King) currentlySelectedSquare.getPiece()).setAlreadyMoved(true);
        ((Rook) rookSquare.getPiece()).setAlreadyMoved(true);

        int kingRow = currentlySelectedSquare.getRow();
        int kingColumn = currentlySelectedSquare.getColumn();
        int rookColumn = rookSquare.getColumn();

        if (rookColumn == 0){
            board.getSquares()[kingRow][kingColumn - 2].setPiece(currentlySelectedSquare.getPiece());
            board.getSquares()[kingRow][kingColumn - 1].setPiece(rookSquare.getPiece());
        } else if (rookColumn == 7){
            board.getSquares()[kingRow][kingColumn + 2].setPiece(currentlySelectedSquare.getPiece());
            board.getSquares()[kingRow][kingColumn + 1].setPiece(rookSquare.getPiece());
        }

        currentlySelectedSquare.removePiece();
        currentlySelectedSquare.clearAnimation();
        rookSquare.removePiece();

    }

    // Update list of squares after standard move.
    public void squareUpdate(Square selectedSquare){
        if (currentPlayer == Player.WHITE){
            whitePieceSquares.remove(currentlySelectedSquare);
            whitePieceSquares.add(selectedSquare);
            blackPieceSquares.remove(selectedSquare);
        } else if (currentPlayer == Player.BLACK){
            blackPieceSquares.remove(currentlySelectedSquare);
            blackPieceSquares.add(selectedSquare);
            whitePieceSquares.remove(selectedSquare);
        }
    }

    // Update list of squares after en passant capture.
    private void squareUpdateEnPassant(Square selectedSquare){
        if (currentPlayer == Player.WHITE){
            whitePieceSquares.remove(currentlySelectedSquare);
            whitePieceSquares.add(selectedSquare);
            blackPieceSquares.remove(enPassantSquare);
        } else if (currentPlayer == Player.BLACK){
            blackPieceSquares.remove(currentlySelectedSquare);
            blackPieceSquares.add(selectedSquare);
            whitePieceSquares.remove(enPassantSquare);
        }
    }

    // Update list of squares after castling.
    private void squareUpdateCastling(Square rookSquare){

        int kingRow = currentlySelectedSquare.getRow();
        int kingColumn = currentlySelectedSquare.getColumn();
        int rookColumn = rookSquare.getColumn();

        if (rookColumn == 0) {
            if (currentPlayer == Player.WHITE) {
                whitePieceSquares.remove(currentlySelectedSquare);
                whitePieceSquares.remove(rookSquare);
                whitePieceSquares.add(board.getSquares()[kingRow][kingColumn - 2]);
                whitePieceSquares.add(board.getSquares()[kingRow][kingColumn - 1]);
                whiteKingSquare = board.getSquares()[kingRow][kingColumn - 2];
            } else if (currentPlayer == Player.BLACK) {
                blackPieceSquares.remove(currentlySelectedSquare);
                blackPieceSquares.remove(rookSquare);
                blackPieceSquares.add(board.getSquares()[kingRow][kingColumn - 2]);
                blackPieceSquares.add(board.getSquares()[kingRow][kingColumn - 1]);
                blackKingSquare = board.getSquares()[kingRow][kingColumn - 2];
            }
        } else if (rookColumn == 7) {
            if (currentPlayer == Player.WHITE) {
                whitePieceSquares.remove(currentlySelectedSquare);
                whitePieceSquares.remove(rookSquare);
                whitePieceSquares.add(board.getSquares()[kingRow][kingColumn + 2]);
                whitePieceSquares.add(board.getSquares()[kingRow][kingColumn + 1]);
                whiteKingSquare = board.getSquares()[kingRow][kingColumn + 2];
            } else if (currentPlayer == Player.BLACK) {
                blackPieceSquares.remove(currentlySelectedSquare);
                blackPieceSquares.remove(rookSquare);
                blackPieceSquares.add(board.getSquares()[kingRow][kingColumn + 2]);
                blackPieceSquares.add(board.getSquares()[kingRow][kingColumn + 1]);
                blackKingSquare = board.getSquares()[kingRow][kingColumn + 2];
            }
        }

    }

    // Method showing dialog window with possible pawn promotion choices (action after choice described in PawnPromotionFragment).
    private void pawnPromotion(Square selectedSquare){
        pawnToPromoteSquare = selectedSquare;
        PawnPromotionFragment pawnPromotionFragment = new PawnPromotionFragment();
        pawnPromotionFragment.setCancelable(false);
        pawnPromotionFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "");
    }

    // Method returning all squares adjacent to given square that are empty or occupied by opponent's piece
    private HashSet<Square> adjacentAvailableSquares(Square[][] squares, Square square) {
        int row = square.getRow();
        int column = square.getColumn();
        HashSet<Square> result = new HashSet<>();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    int adjacentRow = row + i;
                    int adjacentColumn = column + j;
                    if (adjacentRow >= 0 && adjacentRow <= 7 && adjacentColumn >= 0 && adjacentColumn <= 7) {
                        Square squareToCheck = squares[adjacentRow][adjacentColumn];
                        if (squareToCheck.getPiece() == null
                                || squareToCheck.getPiece().getPlayer() != getOppositePlayer()) {
                            result.add(squareToCheck);
                        }
                    }
                }
            }
        }
        return result;
    }

    // Method returning all squares placed in between of two given squares
    // on a line that connects those two squares (horizontal, vertical or diagonal).
    private HashSet<Square> squaresInBetween(Square[][] squares, Square square1, Square square2) {

        HashSet<Square> squaresInBetween = new HashSet<>();

        int square1Row = square1.getRow();
        int square1Column = square1.getColumn();
        int square2Row = square2.getRow();
        int square2Column = square2.getColumn();

        // horizontal line
        if (square1Row == square2Row) {
            if (square1Column < square2Column) {
                for (int column = square1Column + 1; column < square2Column; column++) {
                    squaresInBetween.add(squares[square1Row][column]);
                }
            } else {
                for (int column = square1Column - 1; column > square2Column; column--) {
                    squaresInBetween.add(squares[square1Row][column]);
                }
            }
        }

        // vertical line
        if (square1Column == square2Column) {
            if (square1Row < square2Row) {
                for (int row = square1Row + 1; row < square2Row; row++) {
                    squaresInBetween.add(squares[row][square1Column]);
                }
            } else {
                for (int row = square1Row - 1; row > square2Row; row--) {
                    squaresInBetween.add(squares[row][square1Column]);
                }
            }
        }

        // diagonal lines
        if (!(square1Row == square2Row) && !(square1Column == square2Column) && Math.abs(square1Row - square2Row) == Math.abs(square1Column - square2Column)) {
            if (square1Row < square2Row) {
                if (square1Column < square2Column) {
                    for (int row = square1Row + 1, column = square1Column + 1; row < square2Row; row++, column++) {
                        squaresInBetween.add(squares[row][column]);
                    }
                } else {
                    for (int row = square1Row + 1, column = square1Column - 1; row < square2Row; row++, column--) {
                        squaresInBetween.add(squares[row][column]);
                    }
                }
            } else if (square1Column < square2Column) {
                for (int row = square1Row - 1, column = square1Column + 1; row > square2Row; row--, column++) {
                    squaresInBetween.add(squares[row][column]);
                }
            } else {
                for (int row = square1Row - 1, column = square1Column - 1; row > square2Row; row--, column--) {
                    squaresInBetween.add(squares[row][column]);
                }
            }
        }

        return squaresInBetween;

    }



    // Other methods used (self-explanatory).

    private void newSelection(Square newSelection){
        currentlySelectedSquare = newSelection;
        currentlySelectedSquare.startAnimation(selectedPieceAnimation);
    }

    private void changeSelection(Square newSelection){
        currentlySelectedSquare.clearAnimation();
        newSelection.startAnimation(selectedPieceAnimation);
        currentlySelectedSquare = newSelection;
    }

    private void updateKingSquare(Square selectedSquare) {
        if (currentPlayer == Player.WHITE) {
            whiteKingSquare = selectedSquare;
        } else if (currentPlayer == Player.BLACK) {
            blackKingSquare = selectedSquare;
        }
    }

    public void nextPlayer() {
        if (currentPlayer == Player.WHITE) {
            currentPlayer = Player.BLACK;
        } else {
            currentPlayer = Player.WHITE;
            moveNumber ++;
        }
    }

    public Player getOppositePlayer(){
        if (currentPlayer == Player.WHITE){
            return Player.BLACK;
        } else{
            return Player.WHITE;
        }
    }

    public Square getOppositeKingSquare(){
        if (currentPlayer == Player.WHITE){
            return blackKingSquare;
        } else{
            return whiteKingSquare;
        }
    }

    private Square getCurrentKingSquare(){
        if (currentPlayer == Player.WHITE){
            return whiteKingSquare;
        } else{
            return blackKingSquare;
        }
    }

    private ArrayList<Square> getOppositePieceSquares(){
        if (currentPlayer == Player.WHITE){
            return blackPieceSquares;
        } else{
            return whitePieceSquares;
        }
    }

    public void setPawnToPromoteSquare(Square pawnToPromoteSquare) {
        this.pawnToPromoteSquare = pawnToPromoteSquare;
    }

    public Square getPawnToPromoteSquare() {
        return pawnToPromoteSquare;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public void setCurrentlySelectedSquare(Square currentlySelectedSquare) {
        this.currentlySelectedSquare = currentlySelectedSquare;
    }

    private void clearEnPassantSquare() {
        if (enPassantSquare != null) {
            enPassantSquare = null;
        }
    }

}


