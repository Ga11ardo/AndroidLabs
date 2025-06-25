package com.labs.lab6;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Мій список завдань");

        RecyclerView rvCurrentTasks = findViewById(R.id.rv_current_tasks);
        rvCurrentTasks.setLayoutManager(new LinearLayoutManager(this));
        final TaskAdapter currentTaskAdapter = new TaskAdapter();
        rvCurrentTasks.setAdapter(currentTaskAdapter);

        RecyclerView rvCompletedTasks = findViewById(R.id.rv_completed_tasks);
        rvCompletedTasks.setLayoutManager(new LinearLayoutManager(this));
        final CompletedTaskAdapter completedTaskAdapter = new CompletedTaskAdapter();
        rvCompletedTasks.setAdapter(completedTaskAdapter);

        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
        taskViewModel.getCurrentTasks().observe(this, currentTaskAdapter::setTasks);
        taskViewModel.getCompletedTasks().observe(this, completedTaskAdapter::setTasks);

        FloatingActionButton fab = findViewById(R.id.fab_add_task);
        fab.setOnClickListener(v -> showTaskDialog(null));

        currentTaskAdapter.setOnTaskClickListener(new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskChecked(Task task) {
                task.isCompleted = true;
                task.completionTimestamp = System.currentTimeMillis();
                taskViewModel.update(task);
            }

            @Override
            public void onTaskLongClicked(Task task) {
                showOptionsDialog(task);
            }
        });

        completedTaskAdapter.setOnCompletedTaskClickListener(task -> {
            new AlertDialog.Builder(this)
                    .setTitle("Видалити завдання")
                    .setMessage("Ви впевнені, що хочете видалити це завдання?")
                    .setPositiveButton("Так", (dialog, which) -> taskViewModel.delete(task))
                    .setNegativeButton("Ні", null)
                    .show();
        });
    }

    private void showTaskDialog(final Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        final EditText etTaskTitle = dialogView.findViewById(R.id.et_task_title);
        if (task != null) {
            builder.setTitle("Редагувати завдання");
            etTaskTitle.setText(task.title);
        } else {
            builder.setTitle("Нове завдання");
        }

        builder.setPositiveButton(task == null ? "Додати" : "Зберегти", (dialog, which) -> {
            String title = etTaskTitle.getText().toString().trim();
            if (!title.isEmpty()) {
                if (task == null) {
                    taskViewModel.insert(new Task(title));
                } else {
                    task.title = title;
                    taskViewModel.update(task);
                }
            }
        });
        builder.setNegativeButton("Скасувати", (dialog, which) -> dialog.cancel());
        builder.create().show();
    }

    private void showOptionsDialog(final Task task) {
        final CharSequence[] options = {"Редагувати", "Видалити"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Оберіть дію");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Редагувати")) {
                showTaskDialog(task);
            } else if (options[item].equals("Видалити")) {
                taskViewModel.delete(task);
            }
        });
        builder.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_statistics) {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}