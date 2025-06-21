package com.labs.lab4;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "media")
public class MediaEntry {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public String path;
    public boolean isVideo;
}