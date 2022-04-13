package ru.opalevapps.EveryGlic.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "create table $TABLE_MEASUREMENTS("
                    + "$KEY_ID integer primary key, "
                    + "$KEY_MEASUREMENT real, "
                    + "$KEY_TIME_IN_SECONDS integer, "
                    + "$KEY_COMMENT text )"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists $TABLE_MEASUREMENTS")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "bloodGlucoseMonitoring"

        //KEYS shared
        const val KEY_ID = "_id"
        const val KEY_TIME_IN_SECONDS = "dateAndTime"

        //table measurements
        const val TABLE_MEASUREMENTS = "measurements"

        //KEYS for table measurements
        const val KEY_MEASUREMENT = "measurement"
        const val KEY_COMMENT = "comment"
    }
}