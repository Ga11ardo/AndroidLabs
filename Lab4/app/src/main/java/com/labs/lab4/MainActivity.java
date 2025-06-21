package com.labs.lab4;

import android.app.AlertDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private MediaDao dao;
    private List<MediaEntry> mediaList;
    private MediaAdapter adapter;
    private RecyclerView recycler;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<String[]> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            this::onFilePicked
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(getApplicationContext());
        dao = db.mediaDao();

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        mediaList = new ArrayList<>();
        adapter = new MediaAdapter(this, mediaList, dao, executor);
        recycler.setAdapter(adapter);

        loadMediaFromDb();

        findViewById(R.id.btnLoad).setOnClickListener(v -> pickFile());
        findViewById(R.id.btnDownload).setOnClickListener(v -> downloadUrl());
    }

    private void loadMediaFromDb() {
        executor.execute(() -> {
            List<MediaEntry> loadedMedia = dao.getAll();
            runOnUiThread(() -> {
                mediaList.clear();
                mediaList.addAll(loadedMedia);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void pickFile() {
        filePickerLauncher.launch(new String[]{"audio/*", "video/*"});
    }

    private void onFilePicked(Uri uri) {
        if (uri == null) return;

        executor.execute(() -> {
            String name = getFileNameFromUri(uri);
            if (name == null) name = "unknown_file_" + System.currentTimeMillis();

            try {
                File f = StorageUtils.copyToInternal(this, uri, name);
                MediaEntry e = new MediaEntry();
                e.name = f.getName();
                e.path = f.getAbsolutePath();
                String lowerCaseName = e.name.toLowerCase();
                e.isVideo = lowerCaseName.matches(".*\\.(mp4|avi|mkv|3gp|webm)");

                e.id = dao.insert(e);

                runOnUiThread(() -> {
                    mediaList.add(e);
                    adapter.notifyItemInserted(mediaList.size() - 1);
                });
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
            }
        }
        return fileName;
    }

    private void downloadUrl() {
        final EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Викачати URL")
                .setView(input)
                .setPositiveButton("OK", (d, w) -> {
                    String url = input.getText().toString();
                    executor.execute(() -> {
                        try {
                            File f = StorageUtils.downloadUrl(this, url);
                            MediaEntry e = new MediaEntry();
                            e.name = f.getName();
                            e.path = f.getAbsolutePath();
                            e.isVideo = e.name.toLowerCase().matches(".*\\.(mp4|avi|mkv|3gp|webm)");
                            e.id = dao.insert(e);
                            runOnUiThread(() -> {
                                mediaList.add(e);
                                adapter.notifyItemInserted(mediaList.size() - 1);
                            });
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.releasePlayer();
        }
        executor.shutdown();
    }
}