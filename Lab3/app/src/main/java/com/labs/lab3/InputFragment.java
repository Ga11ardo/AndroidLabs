package com.labs.lab3;

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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InputFragment extends Fragment {
    public interface OnSubmit { void onSubmit(String text,int color); }
    private OnSubmit listener;
    private RadioGroup colorGroup;
    private EditText inputEdit;
    private Button ok;

    public void setListener(OnSubmit l){ listener = l; }

    @Nullable @Override public View onCreateView(
            LayoutInflater inf, ViewGroup c, Bundle b
    ){
        View v = inf.inflate(R.layout.fragment_input, c,false);
        colorGroup = v.findViewById(R.id.colorGroup);
        inputEdit = v.findViewById(R.id.inputEditText);
        ok = v.findViewById(R.id.okButton);
        ok.setOnClickListener(view->{
            String t = inputEdit.getText().toString().trim();
            int id = colorGroup.getCheckedRadioButtonId();
            if(TextUtils.isEmpty(t)||id==-1){
                Toast.makeText(getContext(),
                        "Введіть текст і виберіть колір",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            int color = Color.BLACK;
            if(id==R.id.radioRed) color=Color.RED;
            else if(id==R.id.radioGreen) color=Color.GREEN;
            else if(id==R.id.radioBlue) color=Color.BLUE;
            if(listener!=null) listener.onSubmit(t,color);
        });
        return v;
    }

    public void clearInput(){
        if(getView()==null) return;
        inputEdit.setText("");
        colorGroup.clearCheck();
    }
}
