package com.labs.lab2;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InputFragment extends Fragment {
    interface OnInputListener {
        void onInputSubmitted(String text, int color);
    }

    private OnInputListener listener;
    private RadioGroup colorGroup;
    private EditText inputEditText;
    private Button okButton;

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        if (context instanceof OnInputListener) {
            listener = (OnInputListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInputListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.fragment_input, container, false);
        colorGroup     = v.findViewById(R.id.colorGroup);
        inputEditText  = v.findViewById(R.id.inputEditText);
        okButton       = v.findViewById(R.id.okButton);

        okButton.setOnClickListener(view -> {
            String text = inputEditText.getText().toString().trim();
            int selectedId = colorGroup.getCheckedRadioButtonId();

            if (TextUtils.isEmpty(text) || selectedId == -1) {
                Toast.makeText(getContext(),
                        "Будь ласка, введіть текст та виберіть колір",
                        Toast.LENGTH_SHORT).show();
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

            // Передаємо назад у Activity
            listener.onInputSubmitted(text, color);
        });

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void clearInput() {
        if (getView() != null) {
            EditText editText = getView().findViewById(R.id.inputEditText);
            RadioGroup colorGroup = getView().findViewById(R.id.colorGroup);
            editText.setText("");
            colorGroup.clearCheck();
        }
    }

}

