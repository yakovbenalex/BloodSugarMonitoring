package ru.opalevapps.EveryGlic.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "bloodGlucoseMonitoring";

    //KEYS shared
    public static final String KEY_ID = "_id";
    public static final String KEY_TIME_IN_SECONDS = "dateAndTime";

    //table measurements
    public static final String TABLE_MEASUREMENTS = "measurements";
    //KEYS for table measurements
    public static final String KEY_MEASUREMENT = "measurement";
    public static final String KEY_COMMENT = "comment";

    public DBHelper(Context context) {
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