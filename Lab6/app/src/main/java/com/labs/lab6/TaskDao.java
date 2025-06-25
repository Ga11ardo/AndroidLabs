package com.labs.lab6;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY id DESC")
    LiveData<List<Task>> getCurrentTasks();

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY completionTimestamp DESC")
    LiveData<List<Task>> getCompletedTasks();

    @Query("SELECT * FROM tasks WHERE isCompleted = 1")
    List<Task> getAllCompletedTasksForStats();
}