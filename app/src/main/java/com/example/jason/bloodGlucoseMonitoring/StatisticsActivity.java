package com.example.jason.bloodGlucoseMonitoring;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class StatisticsActivity extends AppCompatActivity {

    public static final int weekInSec = 7*24*3600;


    //
    TextView tvCountOfMeasurements;

    // SQLite database
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        tvCountOfMeasurements = (TextView) findViewById(R.id.tvCountOfMeasurements);


        dbHelper = new DBHelper(this);

        loadStatistics();
    }

    private void loadStatistics() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int recordsCount;

        /*
        int id;
        float measurement;
        long timeInMillis; // *1000 milliseconds
        String comment;
        */

        Cursor cursor = database.query(DBHelper.TABLE_MEASUREMENTS, null, null, null, null, null, null);//KEY_TIME_IN_SECONDS + " ASC"
        recordsCount = cursor.getCount();

        /*if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int idMeasurement = cursor.getColumnIndex(DBHelper.KEY_MEASUREMENT);
            int idTimeInMillis = cursor.getColumnIndex(KEY_TIME_IN_SECONDS);
            int idComment = cursor.getColumnIndex(DBHelper.KEY_COMMENT);
            do {
                id = cursor.getInt(idIndex);
                measurement = cursor.getFloat(idMeasurement);
                timeInMillis = cursor.getLong(idTimeInMillis);
                comment = cursor.getString(idComment);
                data.add(new ItemRecords(measurement, timeInMillis, comment));
            } while (cursor.moveToNext());
        } //else { //No Records }*/

        tvCountOfMeasurements.setText(String.valueOf(recordsCount));

        cursor.close();
    }
}
