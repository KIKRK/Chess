package com.example.chess;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.Serializable;


public class BoardFragment extends Fragment implements View.OnClickListener, Serializable {

    private GameOfChess gameOfChess;

    public GameOfChess getGameOfChess() {
        return gameOfChess;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        gameOfChess = new GameOfChess(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.board, container, false);

        if (savedInstanceState != null){
            gameOfChess.loadSavedState(view, getResources(), savedInstanceState);
        } else {
            gameOfChess.initialSetting(view, getResources());
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        gameOfChess.saveState(outState);
    }

    @Override
    public void onClick(View v) {
        gameOfChess.onClick(v);
    }


}
