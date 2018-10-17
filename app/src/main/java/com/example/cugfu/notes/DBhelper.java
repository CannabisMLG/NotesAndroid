package com.example.cugfu.notes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DBhelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;


    public DBhelper(Context context) {
        super(context, "films", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table films(`_id` integer primary key autoincrement," +
                "`name` text, `kpRate` text, `ch` integer, `myRate` text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists films");

        onCreate(db);
    }
}