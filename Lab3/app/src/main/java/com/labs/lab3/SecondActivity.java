package com.labs.lab3;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;

import java.util.List;

public class SecondActivity extends AppCompatActivity {
    private DBHelper db;

    @Override protected void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.activity_second);
        db = new DBHelper(this);

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        List<Request> list = db.getAll();
        rv.setAdapter(new RequestAdapter(list));

        Button back = findViewById(R.id.backButton);
        back.setOnClickListener(v-> finish());
    }
}
