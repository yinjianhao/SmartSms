package com.me.smartsms.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GroupOpenHelper extends SQLiteOpenHelper {

    private static GroupOpenHelper instance;
    private final static int DB_GROUP_VERSION = 1;

    private GroupOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static GroupOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new GroupOpenHelper(context, "group.db", null, DB_GROUP_VERSION);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table thread_group(" +
                "_id integer primary key autoincrement," +
                "group_id integer," +
                "thread_id integer" +
                ")");

        db.execSQL("create table groups(" +
                "_id integer primary key autoincrement," +
                "name varchar," +
                "create_date integer," +
                "thread_count integer" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
