package com.example.chess;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.chess.pieces.Bishop;
import com.example.chess.pieces.Knight;
import com.example.chess.pieces.Piece;
import com.example.chess.pieces.Queen;
import com.example.chess.pieces.Rook;

public class PawnPromotionFragment extends DialogFragment implements View.OnClickListener{

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        BoardFragment boardFragment = (BoardFragment) getActivity().getSupportFragmentManager().findFragmentByTag("BOARD_FRAGMENT");
        GameOfChess gameOfChess = boardFragment.getGameOfChess();
        Player currentPlayer = gameOfChess.getCurrentPlayer();

        FragmentActivity fragmentActivity = getActivity();
        LayoutInflater inflater = fragmentActivity.getLayoutInflater();
        View promotionView = inflater.inflate(R.layout.pawn_promotion, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);

        AppCompatButton queenButton = promotionView.findViewById(R.id.queenButton);
        AppCompatButton rookButton = promotionView.findViewById(R.id.rookButton);
        AppCompatButton knightButton = promotionView.findViewById(R.id.knightButton);
        AppCompatButton bishopButton = promotionView.findViewById(R.id.bishopButton);

        if (currentPlayer == Player.WHITE){
            queenButton.setText("\u2655");
            rookButton.setText("\u2656");
            knightButton.setText("\u2658");
            bishopButton.setText("\u2657");
        } else if (currentPlayer == Player.BLACK){
            queenButton.setText("\u265B");
            rookButton.setText("\u265C");
            knightButton.setText("\u265E");
            bishopButton.setText("\u265D");
        }

        AppCompatButton[] buttons = new AppCompatButton[]{queenButton, rookButton, knightButton, bishopButton};

        for (AppCompatButton button : buttons){
            button.setAutoSizeTextTypeUniformWithConfiguration(8, 36, 1, TypedValue.COMPLEX_UNIT_DIP);
            button.setOnClickListener(this);
        }

        builder.setView(promotionView);

        return builder.create();

    }

    @Override
    public void onStart() {

        super.onStart();
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        WindowManager.LayoutParams windowParams = window.getAttributes();
        int screenOrientation = getResources().getConfiguration().orientation;
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT){
            window.setGravity(Gravity.TOP);
            windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(windowParams);
        } else if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE){
            window.setGravity(Gravity.END);
            windowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            windowParams.width = (int) (size.x * 0.25);
            window.setAttributes(windowParams);
        }
    }

    @Override
    public void onClick(View v) {

        BoardFragment boardFragment = (BoardFragment) getActivity().getSupportFragmentManager().findFragmentByTag("BOARD_FRAGMENT");
        GameOfChess gameOfChess = boardFragment.getGameOfChess();
        Player currentPlayer = gameOfChess.getCurrentPlayer();
        Square selectedButton = gameOfChess.getPawnToPromoteSquare();

        Piece selectedPiece = null;

        switch (v.getId()){
            case R.id.queenButton:
                selectedPiece = new Queen(currentPlayer);
                break;
            case R.id.rookButton:
                selectedPiece = new Rook(currentPlayer, true);
                break;
            case R.id.knightButton:
                selectedPiece = new Knight(currentPlayer);
                break;
            case R.id.bishopButton:
                selectedPiece = new Bishop(currentPlayer);
                break;
        }

        // Ensure game continuation in case that game state is recreated after save performed while dialog window is on screen.
        gameOfChess.getBoard().getSquares()[selectedButton.getRow()][selectedButton.getColumn()].setPiece(selectedPiece);

        gameOfChess.setCurrentlySelectedSquare(null);
        gameOfChess.setPawnToPromoteSquare(null);
        gameOfChess.squareUpdate(selectedButton);

        // Force dialog window to close if checkmate occurs directly after pawn promotion.
        if (gameOfChess.getOppositeKingSquare().squareInDanger(gameOfChess.getBoard().getSquares(), gameOfChess.getOppositePlayer())) {
            if (gameOfChess.checkForCheck(selectedButton)) {
                dismiss();
                return;
            }
        }

        gameOfChess.nextPlayer();
        gameOfChess.checkForStalemate();

        dismiss();

    }


}
