package com.example.jason.bloodGlucoseMonitoring;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.jason.bloodGlucoseMonitoring.DBHelper.KEY_TIME_IN_SECONDS;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_BLOOD_HIGH_SUGAR;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_BLOOD_LOW_SUGAR;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_FIRST_RUN_AGREEMENT;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_TIME_FORMAT_24H;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.bloodHighSugarDefault;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.bloodLowSugarDefault;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.timeFormat24hDefault;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.unitBloodSugarMmolDefault;


// implements View.OnClickListener
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // variables for preferences
    float prefsBloodLowSugar;
    float prefsBloodHighSugar;
    boolean prefsUnitBloodSugarMmol;
    boolean prefsTimeFormat24h;
    boolean firstRun;

    // views
    Button btnAddMeasurement;
    Button btnStatistics;
    Button btnMeasurements;
    Button btnCalculatorCarbs;
    Button btnInfo;

    ListView lvMeasurements3Last;

    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
        firstRun = sharedPref.getBoolean(KEY_PREFS_FIRST_RUN_AGREEMENT, true);

        if (firstRun) {
            Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
            startActivity(intent);
        }

        // find view on screen by id
        btnCalculatorCarbs = (Button) findViewById(R.id.btnCalculatorCarbs);
        btnAddMeasurement = (Button) findViewById(R.id.btnAddMeasurement);
        btnMeasurements = (Button) findViewById(R.id.btnMeasurements);
        btnStatistics = (Button) findViewById(R.id.btnStatistics);
        btnInfo = (Button) findViewById(R.id.btnInfo);

        lvMeasurements3Last = (ListView) findViewById(R.id.lvMeasurements3Last);

        // listeners for views
        btnAddMeasurement.setOnClickListener(this);
        btnStatistics.setOnClickListener(this);
        btnMeasurements.setOnClickListener(this);
        btnCalculatorCarbs.setOnClickListener(this);
        btnInfo.setOnClickListener(this);

        lvMeasurements3Last.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentAddMeasurementActivity = new Intent(MainActivity.this, AddMeasurementActivity.class);
                intentAddMeasurementActivity.putExtra("idRec", id);
                startActivity(intentAddMeasurementActivity);
            }
        });

        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onResume() {
        loadPreferences();
        load3LastRecords();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intentPreferencesActivity = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivity(intentPreferencesActivity);
                break;

            case R.id.action_help:
                Toast.makeText(this, "Help is not available", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, AgreementActivity.class);
//                startActivity(intent);
                break;

            case R.id.action_agreement:
                Intent intentAgreementActivity = new Intent(MainActivity.this, AgreementActivity.class);
                startActivity(intentAgreementActivity);
                break;

            case R.id.action_about:
                Intent intentAboutActivity = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intentAboutActivity);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCalculatorCarbs:
                Intent intentCalculatorCarbsActivity = new Intent(this, CalculatorCarbsActivity.class);
                startActivity(intentCalculatorCarbsActivity);
                break;

            case R.id.btnAddMeasurement:
                Intent intentAddMeasurementActivity = new Intent(this, AddMeasurementActivity.class);
                startActivity(intentAddMeasurementActivity);
                break;

            case R.id.btnMeasurements:
                Intent intentMeasurementsActivity = new Intent(this, MeasurementsActivity.class);
                startActivity(intentMeasurementsActivity);
                break;

            case R.id.btnStatistics:
                Intent intentStatisticsActivity = new Intent(this, StatisticsActivity.class);
                startActivity(intentStatisticsActivity);
                break;

            case R.id.btnInfo:
                Intent intentInfoActivity = new Intent(this, InfoActivity.class);
                startActivity(intentInfoActivity);
                break;

            default:
                break;
        }
    }

    private void load3LastRecords() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<ItemRecords> data = new ArrayList<>();

        int id;
        float measurement;
        long timeInSeconds;
        String comment;

        Cursor cursor = database.query(DBHelper.TABLE_MEASUREMENTS, null, null, null, null, null,
                DBHelper.KEY_TIME_IN_SECONDS + " DESC", "3");

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

        lvMeasurements3Last.setAdapter(new ItemRecordsAdapter(this, data,
                prefsBloodLowSugar, prefsBloodHighSugar, prefsUnitBloodSugarMmol, prefsTimeFormat24h));
    }


    public void loadPreferences() {
        // get settings object
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);

        // get saved value for diabetes
        prefsUnitBloodSugarMmol = sharedPref.getBoolean(KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL, unitBloodSugarMmolDefault);
        prefsBloodLowSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_LOW_SUGAR, bloodLowSugarDefault);
        prefsBloodHighSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_HIGH_SUGAR, bloodHighSugarDefault);
        prefsTimeFormat24h = sharedPref.getBoolean(KEY_PREFS_TIME_FORMAT_24H, timeFormat24hDefault);
    }

    @Override
    public void onBackPressed() {
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(MainActivity.this);
        quitDialog.setTitle(R.string.exit_are_you_sure);
        quitDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        quitDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel
            }
        });

        quitDialog.show();
    }
}