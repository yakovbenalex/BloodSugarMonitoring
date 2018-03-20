package com.example.jason.EveryGlic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "bloodGlucoseMonitoring";

    //KEYS shared
    static final String KEY_ID = "_id";
    static final String KEY_TIME_IN_SECONDS = "dateAndTime";

    //table measurements
    static final String TABLE_MEASUREMENTS = "measurements";
    //KEYS for table measurements
    static final String KEY_MEASUREMENT = "measurement";
    static final String KEY_COMMENT = "comment";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_MEASUREMENTS + "("
                + KEY_ID + " integer primary key, "
                + KEY_MEASUREMENT + " real, "
                + KEY_TIME_IN_SECONDS + " integer, "
                + KEY_COMMENT + " text " + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_MEASUREMENTS);

        onCreate(db);
    }
}