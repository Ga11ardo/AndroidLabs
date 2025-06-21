package com.labs.lab2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OutputFragment extends Fragment {
    private static final String ARG_TEXT  = "arg_text";
    private static final String ARG_COLOR = "arg_color";

    interface OnCancelListener {
        void onCancel();
    }

    private OnCancelListener listener;

    public static OutputFragment newInstance(String text, int color) {
        OutputFragment frag = new OutputFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putInt(ARG_COLOR, color);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        if (context instanceof OnCancelListener) {
            listener = (OnCancelListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCancelListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.fragment_output, container, false);
        TextView tv = v.findViewById(R.id.outputTextView);
        Button cancelButton = v.findViewById(R.id.cancelButton);

        Bundle args = getArguments();
        if (args != null) {
            tv.setText(args.getString(ARG_TEXT));
            tv.setTextColor(args.getInt(ARG_COLOR));
        }

        cancelButton.setOnClickListener(view -> {
            listener.onCancel();
        });

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void updateOutput(String text, int color) {
        if (getView() != null) {
            TextView tv = getView().findViewById(R.id.outputTextView);
            tv.setText(text);
            tv.setTextColor(color);
        }
    }

    public void clearOutput() {
        if (getView() != null) {
            TextView tv = getView().findViewById(R.id.outputTextView);
            tv.setText("");
            tv.setTextColor(Color.BLACK);
        }
    }
}

