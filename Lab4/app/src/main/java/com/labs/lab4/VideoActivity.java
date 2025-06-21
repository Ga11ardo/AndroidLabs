package com.labs.lab4;

import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s){
        super.onCreate(s); setContentView(R.layout.activity_video);
        VideoView vv=findViewById(R.id.videoView);
        String path=getIntent().getStringExtra("path");
        vv.setVideoPath(path);
        vv.setMediaController(new MediaController(this));
        vv.start();
    }
}
