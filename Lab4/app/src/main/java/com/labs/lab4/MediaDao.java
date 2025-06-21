package com.labs.lab4;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MediaDao {
    @Query("SELECT * FROM media")
    List<MediaEntry> getAll();

    @Insert
    long insert(MediaEntry entry);

    @Delete
    void delete(MediaEntry entry);
}
