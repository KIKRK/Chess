package com.example.chess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
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

        if (savedInstanceState != null){
            fragmentManager.getFragment(savedInstanceState, "SAVED_FRAGMENT");
        } else {
            Fragment boardFragment = new BoardFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.boardFragmentHolder, boardFragment, "BOARD_FRAGMENT");
            fragmentTransaction.commit();
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment boardFragment = fragmentManager.findFragmentById(R.id.boardFragmentHolder);
        fragmentManager.putFragment(outState, "SAVED_FRAGMENT", boardFragment);

    }

}
