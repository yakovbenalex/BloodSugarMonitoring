package ru.opalevapps.EveryGlic;

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

import java.util.ArrayList;

import static ru.opalevapps.EveryGlic.DBHelper.KEY_TIME_IN_SECONDS;
import static ru.opalevapps.EveryGlic.PreferencesActivity.BLOOD_HIGH_SUGAR_DEFAULT;
import static ru.opalevapps.EveryGlic.PreferencesActivity.BLOOD_LOW_SUGAR_DEFAULT;
import static ru.opalevapps.EveryGlic.PreferencesActivity.KEY_PREFS;
import static ru.opalevapps.EveryGlic.PreferencesActivity.KEY_PREFS_BLOOD_HIGH_SUGAR;
import static ru.opalevapps.EveryGlic.PreferencesActivity.KEY_PREFS_BLOOD_LOW_SUGAR;
import static ru.opalevapps.EveryGlic.PreferencesActivity.KEY_PREFS_FIRST_RUN_AGREEMENT;
import static ru.opalevapps.EveryGlic.PreferencesActivity.KEY_PREFS_TIME_FORMAT_24H;
import static ru.opalevapps.EveryGlic.PreferencesActivity.KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL;
import static ru.opalevapps.EveryGlic.PreferencesActivity.TIME_FORMAT_24H_DEFAULT;
import static ru.opalevapps.EveryGlic.PreferencesActivity.UNIT_BLOOD_SUGAR_MMOL_DEFAULT;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get SharedPreferences settings object
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();

        firstRun = sharedPref.getBoolean(KEY_PREFS_FIRST_RUN_AGREEMENT, true);

        // check for first run app
        if (firstRun) {
            Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
            startActivity(intent);
            intent = new Intent(MainActivity.this, WelcomeScreenActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_main);

        // save change of preferences
        prefEditor.apply();

        initViews();

        dbHelper = new DBHelper(this);
        dbHelper.getDatabaseName().isEmpty();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MyWorks.createInfoItemInActionBar(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyWorks.parseMenuItemMain(this, item);
        MyWorks.parseMenuItemInfo(this, item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddMeasurement:
                Intent intentAddMeasurementActivity = new Intent(this, AddOrChangeMeasurementActivity.class);
                startActivity(intentAddMeasurementActivity);
                break;

            case R.id.btnCalculatorCarbs:
                Intent intentCalculatorCarbsActivity = new Intent(this, CalculatorCarbsActivity.class);
                startActivity(intentCalculatorCarbsActivity);
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
    protected void onResume() {
        loadPreferences();
        load3LastRecords();
        super.onResume();
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
        quitDialog.setCancelable(false);

        quitDialog.show();
    }

    // initialize views on screen and their listening
    public void initViews() {
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
    }
}