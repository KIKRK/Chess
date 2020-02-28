package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements Serializable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        if (display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270) {
            getSupportActionBar().hide();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment boardFragment = fragmentManager.findFragmentByTag("BOARD_FRAGMENT");

        if (boardFragment == null) {
            boardFragment = new BoardFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.boardFragmentHolder, boardFragment, "BOARD_FRAGMENT");
            fragmentTransaction.commit();
        }

        Log.e("B", boardFragment.toString());

    }

}
