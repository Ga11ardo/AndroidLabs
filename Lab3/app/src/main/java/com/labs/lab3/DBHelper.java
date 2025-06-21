package com.labs.lab3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "requests.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE = "requests";
    public static final String COL_ID = "id";
    public static final String COL_TEXT = "text";
    public static final String COL_COLOR = "color";
    public static final String COL_DATETIME = "datetime";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TEXT + " TEXT,"
                + COL_COLOR + " INTEGER,"
                + COL_DATETIME + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long insertRequest(String text, int color, String datetime) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TEXT, text);
        cv.put(COL_COLOR, color);
        cv.put(COL_DATETIME, datetime);
        return db.insert(TABLE, null, cv);
    }

    public List<Request> getAll() {
        List<Request> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE, null, null, null, null, null, COL_ID + " DESC");
        while (c.moveToNext()) {
            Request r = new Request(
                    c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_TEXT)),
                    c.getInt(c.getColumnIndexOrThrow(COL_COLOR)),
                    c.getString(c.getColumnIndexOrThrow(COL_DATETIME))
            );
            list.add(r);
        }
        c.close();
        return list;
    }
}