package com.example.chess;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.chess.pieces.Bishop;
import com.example.chess.pieces.King;
import com.example.chess.pieces.Knight;
import com.example.chess.pieces.Pawn;
import com.example.chess.pieces.Queen;
import com.example.chess.pieces.Rook;

import java.io.Serializable;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class ChessBoard implements Serializable {

    private Square[][] squares;

    public Square[][] getSquares() {
        return squares;
    }

    public ChessBoard(final int rows, final int columns, final ConstraintLayout layout, final int screenOrientation, Context context) {
        this.squares = new Square[rows][columns];

        final ConstraintSet set = new ConstraintSet();
        final int[][] buttonIds = new int[rows][columns];
        final float[][] chainWeights = new float[rows][columns];
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
                squares[i-1][j-1] = new Square(context, i-1, j-1);
                chainWeights[i - 1][j - 1] = 1;
            }
        }

        /*
        Specification of basic parameters for squares, such as:
        - id,
        - coordinates,
        - font size,
        - background,
        - possible other, e.g. text.
         */

        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
                Square square = squares[i-1][j-1];
                if ((i + j) % 2 == 0) {
                    square.setBackgroundColor(Color.LTGRAY);
                } else {
                    square.setBackgroundColor(Color.WHITE);
                }
                square.setAutoSizeTextTypeUniformWithConfiguration(
                        8, 36, 1, TypedValue.COMPLEX_UNIT_DIP);
                square.setPadding(1, 1, 1, 1);
                if (rows <= 9 && columns <= 9) {
                    square.setId(10 * i + j);
                    buttonIds[i - 1][j - 1] = 10 * i + j;
                } else {
                    square.setId(100 * i + j);
                    buttonIds[i - 1][j - 1] = 100 * i + j;
                }
            }
        }


// Specification of dimensions, margins and chains of view elements - with the listener
// when program starts the dimensions are not known (layout.getWidth() and layout.getHeight() return 0).

        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int widthPixels = layout.getWidth();
                int heightPixels = layout.getHeight();
                int columnWidth;

                if (screenOrientation == ORIENTATION_PORTRAIT) {
                    if (columns >= rows) {
                        columnWidth = widthPixels / columns;
                    } else {
                        columnWidth = widthPixels / rows;
                    }
                } else {
                    if (columns >= rows) {
                        columnWidth = heightPixels / columns;
                    } else {
                        columnWidth = heightPixels / rows;
                    }
                }

                for (int i = 1; i <= rows; i++) {
                    for (int j = 1; j <= columns; j++) {
                        Square button = squares[i - 1][j - 1];
                        layout.addView(button);
                    }
                }

                for (int i = 1; i <= rows; i++) {
                    set.createHorizontalChain(ConstraintSet.PARENT_ID, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT,
                            buttonIds[i - 1], chainWeights[i - 1], ConstraintSet.CHAIN_PACKED);
                }

                for (int j = 1; j <= columns; j++) {
                    int[] columnButtonIds = new int[rows];
                    float[] columnChainWeights = new float[rows];
                    for (int i = rows; i >= 1; i--) {
                        columnButtonIds[i - 1] = buttonIds[rows - i][j - 1];
                        columnChainWeights[i - 1] = chainWeights[i - 1][j - 1];
                    }
                    set.createVerticalChain(ConstraintSet.PARENT_ID, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,
                            columnButtonIds, columnChainWeights, ConstraintSet.CHAIN_PACKED);
                }

                set.applyTo(layout);

                for (int i = 1; i <= rows; i++) {
                    for (int j = 1; j <= columns; j++) {
                        Button button = squares[i - 1][j - 1];
                        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) button.getLayoutParams();
                        params.width = columnWidth;
                        params.dimensionRatio = "1";
                        button.setLayoutParams(params);

                    }
                }
            }
        });

        initialSetting();

    }


    // Method setting initial position of pieces on the board.
    private void initialSetting(){
        squares[0][0].setPiece(new Rook(Player.WHITE, false));
        squares[0][1].setPiece(new Knight(Player.WHITE));
        squares[0][2].setPiece(new Bishop(Player.WHITE));
        squares[0][3].setPiece(new Queen(Player.WHITE));
        squares[0][4].setPiece(new King(Player.WHITE, false));
        squares[0][5].setPiece(new Bishop(Player.WHITE));
        squares[0][6].setPiece(new Knight(Player.WHITE));
        squares[0][7].setPiece(new Rook(Player.WHITE, false));

        for (int a = 0; a <= 7; a++){
            squares[1][a].setPiece(new Pawn(Player.WHITE));
        }

        for (int a = 0; a <= 7; a++){
            squares[6][a].setPiece(new Pawn(Player.BLACK));
        }

        squares[7][0].setPiece(new Rook(Player.BLACK, false));
        squares[7][1].setPiece(new Knight(Player.BLACK));
        squares[7][2].setPiece(new Bishop(Player.BLACK));
        squares[7][3].setPiece(new Queen(Player.BLACK));
        squares[7][4].setPiece(new King(Player.BLACK, false));
        squares[7][5].setPiece(new Bishop(Player.BLACK));
        squares[7][6].setPiece(new Knight(Player.BLACK));
        squares[7][7].setPiece(new Rook(Player.BLACK, false));
    }


}
