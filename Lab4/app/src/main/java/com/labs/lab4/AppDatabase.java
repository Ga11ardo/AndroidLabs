package com.labs.lab4;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {MediaEntry.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MediaDao mediaDao();
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context ctx) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    ctx.getApplicationContext(),
                    AppDatabase.class, "media_db"
            ).build();
        }
        return INSTANCE;
    }
}
