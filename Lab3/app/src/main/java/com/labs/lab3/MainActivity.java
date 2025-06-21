package com.labs.lab3;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DBHelper db;
    private InputFragment inputFrag;
    private OutputFragment outputFrag;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_main);
        db = new DBHelper(this);
        FragmentManager fm = getSupportFragmentManager();
        inputFrag = (InputFragment) fm.findFragmentById(R.id.inputFragment);
        outputFrag = (OutputFragment) fm.findFragmentById(R.id.outputFragment);

        inputFrag.setListener((text,color)->{
            String dt = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
            ).format(new Date());
            db.insertRequest(text, color, dt);
            outputFrag.updateOutput(text, color);
        });

        outputFrag.setCancelListener(()->{
            outputFrag.clearOutput();
            inputFrag.clearInput();
        });

        outputFrag.setOpenListener(()->{
            startActivity(new android.content.Intent(
                    MainActivity.this, SecondActivity.class));
        });
    }
}