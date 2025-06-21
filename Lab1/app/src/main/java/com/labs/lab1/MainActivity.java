package com.labs.lab1;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private RadioGroup colorGroup;
    private EditText inputEditText;
    private TextView outputTextView;
    private Button okButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colorGroup       = findViewById(R.id.colorGroup);
        inputEditText    = findViewById(R.id.inputEditText);
        outputTextView   = findViewById(R.id.outputTextView);
        okButton         = findViewById(R.id.okButton);
        cancelButton     = findViewById(R.id.cancelButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputEditText.getText().toString().trim();
                int selectedId = colorGroup.getCheckedRadioButtonId();

                if (text.isEmpty() || selectedId == -1) {
                    Toast.makeText(
                            MainActivity.this,
                            "Будь ласка, введіть текст та виберіть колір",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                int color = Color.BLACK;

                if (selectedId == R.id.radioRed) {
                    color = Color.RED;
                } else if (selectedId == R.id.radioGreen) {
                    color = Color.GREEN;
                } else if (selectedId == R.id.radioBlue) {
                    color = Color.BLUE;
                }


                outputTextView.setText(text);
                outputTextView.setTextColor(color);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputEditText.setText("");
                outputTextView.setText("");
                colorGroup.clearCheck();
            }
        });
    }
}
