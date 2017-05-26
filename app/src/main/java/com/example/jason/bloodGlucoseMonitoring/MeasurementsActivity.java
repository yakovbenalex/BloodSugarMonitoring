package com.example.jason.bloodGlucoseMonitoring;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import static com.example.jason.bloodGlucoseMonitoring.DBHelper.KEY_TIME_IN_SECONDS;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_BLOOD_HIGH_SUGAR;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_BLOOD_LOW_SUGAR;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_TIME_FORMAT_24H;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.bloodHighSugarDefault;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.bloodLowSugarDefault;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.timeFormat24hDefault;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.unitBloodSugarMmolDefault;

public class MeasurementsActivity extends AppCompatActivity {

    ListView lvMeasurementsAll;
    int lvIndexPos = 0;

    // variables for preferences
    float prefsBloodLowSugar;
    float prefsBloodHighSugar;
    boolean prefsTimeFormat24h;
    boolean prefsUnitBloodSugarMmol;

    //SQLite database
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurements);

        lvMeasurementsAll = (ListView) findViewById(R.id.lvMeasurementsAll);

        lvMeasurementsAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MeasurementsActivity.this, AddMeasurementActivity.class);
                intent.putExtra("idRec", id);
                startActivity(intent);
            }
        });
        /*
        lvMeasurementsAll.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MeasurementsActivity.this, "456", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        */

        // get settings object
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);

        // get saved value for diabetes
        prefsUnitBloodSugarMmol = sharedPref.getBoolean(KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL, unitBloodSugarMmolDefault);
        prefsBloodLowSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_LOW_SUGAR, bloodLowSugarDefault);
        prefsBloodHighSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_HIGH_SUGAR, bloodHighSugarDefault);
        prefsTimeFormat24h = sharedPref.getBoolean(KEY_PREFS_TIME_FORMAT_24H, timeFormat24hDefault);

        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadRecords();
    }

    @Override
    protected void onPause() {
        super.onPause();
        lvIndexPos = lvMeasurementsAll.getFirstVisiblePosition();
    }

    @Override
    protected void onResume() {
        // set list view last pos
        if (lvMeasurementsAll != null) {
            if (lvMeasurementsAll.getCount() > lvIndexPos)
                lvMeasurementsAll.setSelectionFromTop(lvIndexPos, 0);
            else
                lvMeasurementsAll.setSelectionFromTop(0, 0);
        }
        super.onResume();
    }

    // load records from DB
    private void loadRecords() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<ItemRecords> data = new ArrayList<>();

        int id;
        float measurement;
        long timeInSeconds;
        String comment;

        Cursor cursor = database.query(DBHelper.TABLE_MEASUREMENTS, null, null, null, null, null,
                DBHelper.KEY_TIME_IN_SECONDS + " DESC");

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int idMeasurement = cursor.getColumnIndex(DBHelper.KEY_MEASUREMENT);
            int idTimeInSeconds = cursor.getColumnIndex(KEY_TIME_IN_SECONDS);
            int idComment = cursor.getColumnIndex(DBHelper.KEY_COMMENT);
            do {
                id = cursor.getInt(idIndex);
                measurement = cursor.getFloat(idMeasurement);
                timeInSeconds = cursor.getLong(idTimeInSeconds);
                comment = cursor.getString(idComment);
                data.add(new ItemRecords(id, measurement, timeInSeconds, comment));
            } while (cursor.moveToNext());
        } //else { //No Records }

        cursor.close();
        database.close();

        lvMeasurementsAll.setAdapter(new ItemRecordsAdapter(this, data,
                prefsBloodLowSugar, prefsBloodHighSugar, prefsUnitBloodSugarMmol, prefsTimeFormat24h));
    }

}
