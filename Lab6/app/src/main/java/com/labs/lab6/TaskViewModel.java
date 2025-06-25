package com.labs.lab6;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskViewModel extends AndroidViewModel {
    private final TaskDao taskDao;
    private final ExecutorService executorService;
    private final LiveData<List<Task>> currentTasks;
    private final LiveData<List<Task>> completedTasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        taskDao = db.taskDao();
        executorService = Executors.newSingleThreadExecutor();
        currentTasks = taskDao.getCurrentTasks();
        completedTasks = taskDao.getCompletedTasks();
    }

    public LiveData<List<Task>> getCurrentTasks() {
        return currentTasks;
    }

    public LiveData<List<Task>> getCompletedTasks() {
        return completedTasks;
    }

    public void insert(Task task) {
        executorService.execute(() -> taskDao.insert(task));
    }

    public void update(Task task) {
        executorService.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        executorService.execute(() -> taskDao.delete(task));
    }

    public List<Task> getAllCompletedTasksForStats() {
        return taskDao.getAllCompletedTasksForStats();
    }
}