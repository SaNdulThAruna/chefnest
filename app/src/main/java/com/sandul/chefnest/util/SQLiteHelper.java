package com.sandul.chefnest.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {
    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE user (" +
                "email TEXT PRIMARY KEY," +
                "first_name TEXT," +
                "last_name TEXT," +
                "mobile TEXT," +
                "line1 TEXT," + // Add the line1 column
                "line2 TEXT," +
                "city INTEGER," +
                "postalcode INTEGER" +
                ")";
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE user ADD COLUMN line1 TEXT");
        }
    }
}
