package com.labs.lab2;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity
        implements InputFragment.OnInputListener,
        OutputFragment.OnCancelListener {

    private OutputFragment outputFragment;
    private InputFragment inputFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Знаходимо обидва фрагменти
        inputFragment = (InputFragment) getSupportFragmentManager()
                .findFragmentById(R.id.inputFragment);

        outputFragment = (OutputFragment) getSupportFragmentManager()
                .findFragmentById(R.id.outputFragment);
    }

    @Override
    public void onInputSubmitted(String text, int color) {
        if (outputFragment != null) {
            outputFragment.updateOutput(text, color);
        }
    }

    @Override
    public void onCancel() {
        if (outputFragment != null) {
            outputFragment.clearOutput();
        }
        if (inputFragment != null) {
            inputFragment.clearInput();
        }
    }
}