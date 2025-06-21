package com.labs.lab3;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OutputFragment extends Fragment {
    public interface OnCancel{ void onCancel(); }
    public interface OnOpen { void onOpen(); }

    private OnCancel cancelListener;
    private OnOpen openListener;
    private TextView outText;
    private Button cancelBtn, openBtn;

    public void setCancelListener(OnCancel l){ cancelListener = l; }
    public void setOpenListener(OnOpen l){ openListener = l; }

    @Nullable @Override public View onCreateView(
            LayoutInflater inf, ViewGroup c, Bundle b
    ){
        View v=inf.inflate(R.layout.fragment_output,c,false);
        outText = v.findViewById(R.id.outputTextView);
        cancelBtn = v.findViewById(R.id.cancelButton);
        openBtn = v.findViewById(R.id.openButton);

        cancelBtn.setOnClickListener(view->{ if(cancelListener!=null) cancelListener.onCancel(); });
        openBtn.setOnClickListener(view->{ if(openListener!=null) openListener.onOpen(); });
        return v;
    }

    public void updateOutput(String t,int color){
        outText.setText(t);
        outText.setTextColor(color);
    }
    public void clearOutput(){
        outText.setText("");
        outText.setTextColor(Color.BLACK);
    }
}
