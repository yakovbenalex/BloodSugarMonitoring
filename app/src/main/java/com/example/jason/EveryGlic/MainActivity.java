package com.example.jason.EveryGlic;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import static com.example.jason.EveryGlic.DBHelper.KEY_TIME_IN_SECONDS;
import static com.example.jason.EveryGlic.MyWorks.parseMenuItemMain;
import static com.example.jason.EveryGlic.PreferencesActivity.BLOOD_HIGH_SUGAR_DEFAULT;
import static com.example.jason.EveryGlic.PreferencesActivity.BLOOD_LOW_SUGAR_DEFAULT;
import static com.example.jason.EveryGlic.PreferencesActivity.KEY_PREFS;
import static com.example.jason.EveryGlic.PreferencesActivity.KEY_PREFS_BLOOD_HIGH_SUGAR;
import static com.example.jason.EveryGlic.PreferencesActivity.KEY_PREFS_BLOOD_LOW_SUGAR;
import static com.example.jason.EveryGlic.PreferencesActivity.KEY_PREFS_FIRST_RUN_AGREEMENT;
import static com.example.jason.EveryGlic.PreferencesActivity.KEY_PREFS_TIME_FORMAT_24H;
import static com.example.jason.EveryGlic.PreferencesActivity.KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL;
import static com.example.jason.EveryGlic.PreferencesActivity.TIME_FORMAT_24H_DEFAULT;
import static com.example.jason.EveryGlic.PreferencesActivity.UNIT_BLOOD_SUGAR_MMOL_DEFAULT;


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
    private static final String TAG = "myLog";

    ActionBar tb;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        btnCalculatorCarbs = findViewById(R.id.btnCalculatorCarbs);
        btnAddMeasurement = findViewById(R.id.btnAddMeasurement);
        btnMeasurements = findViewById(R.id.btnMeasurements);
        btnStatistics = findViewById(R.id.btnStatistics);
        btnInfo = findViewById(R.id.btnInfo);

        lvMeasurements3Last = findViewById(R.id.lvMeasurements3Last);

        // listeners for views
        btnAddMeasurement.setOnClickListener(this);
        btnStatistics.setOnClickListener(this);
        btnMeasurements.setOnClickListener(this);
        btnCalculatorCarbs.setOnClickListener(this);
        btnInfo.setOnClickListener(this);

        lvMeasurements3Last.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentAddMeasurementActivity = new Intent(MainActivity.this, AddOrChangeMeasurementActivity.class);
                intentAddMeasurementActivity.putExtra("idRec", id);
                startActivity(intentAddMeasurementActivity);
            }
        });

        dbHelper = new DBHelper(this);
        dbHelper.getDatabaseName().isEmpty();
    }

    @Override
    protected void onResume() {
        loadPreferences();
        load3LastRecords();
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCalculatorCarbs:
                Intent intentCalculatorCarbsActivity = new Intent(this, CalculatorCarbsActivity.class);
                startActivity(intentCalculatorCarbsActivity);
                break;

            case R.id.btnAddMeasurement:
                Intent intentAddMeasurementActivity = new Intent(this, AddOrChangeMeasurementActivity.class);
                startActivity(intentAddMeasurementActivity);
                /*Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://mail.ru"));
                startActivity(intent);*/
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
        /*switch (item.getItemId()) {
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
                // custom dialog
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.activity_info_about);
                dialog.setTitle(R.string.about);
                dialog.show();
                break;

            default:
                break;
        }*/
        Log.d(TAG, "onOptionsItemSelected: " + getApplicationContext());
        parseMenuItemMain(MainActivity.this, "MainActivity", item);
        return super.onOptionsItemSelected(item);
    }

    // load 3 last records for MainActivity
    private void load3LastRecords() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ArrayList<ItemRecords> data = new ArrayList<>();

        // variables to filling them from database
        int id;
        float measurement;
        long timeInSeconds;
        String comment;

        Cursor cursor = database.query(DBHelper.TABLE_MEASUREMENTS,
                null, null, null, null, null,
                DBHelper.KEY_TIME_IN_SECONDS + " DESC", "3");

        // if available at least one records, get data from database
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
                // add data to item for data of listView adapter
                data.add(new ItemRecords(id, measurement, timeInSeconds, comment));
            } while (cursor.moveToNext());
        } //else { //No Records }

        cursor.close();
        database.close();

        lvMeasurements3Last.setAdapter(new ItemRecordsAdapter(this, data,
                prefsBloodLowSugar, prefsBloodHighSugar, prefsUnitBloodSugarMmol, prefsTimeFormat24h));
    }

    // load settings from shared preferences
    public void loadPreferences() {
        // get settings object
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);

        // get saved value for diabetes
        prefsUnitBloodSugarMmol = sharedPref.getBoolean(KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL, UNIT_BLOOD_SUGAR_MMOL_DEFAULT);
        prefsBloodLowSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_LOW_SUGAR, BLOOD_LOW_SUGAR_DEFAULT);
        prefsBloodHighSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_HIGH_SUGAR, BLOOD_HIGH_SUGAR_DEFAULT);
        prefsTimeFormat24h = sharedPref.getBoolean(KEY_PREFS_TIME_FORMAT_24H, TIME_FORMAT_24H_DEFAULT);
    }

    @Override
    public void onBackPressed() {
        openQuitDialog();
    }

    // open exit confirmation dialog
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