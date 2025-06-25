package com.labs.lab6;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public boolean isCompleted;
    public long completionTimestamp; // Store completion time for stats

    public Task(String title) {
        this.title = title;
        this.isCompleted = false;
        this.completionTimestamp = 0;
    }
}
