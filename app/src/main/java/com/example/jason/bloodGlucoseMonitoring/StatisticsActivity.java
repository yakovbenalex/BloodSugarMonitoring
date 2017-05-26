package com.example.jason.bloodGlucoseMonitoring;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import static com.example.jason.bloodGlucoseMonitoring.DBHelper.KEY_MEASUREMENT;
import static com.example.jason.bloodGlucoseMonitoring.DBHelper.KEY_TIME_IN_SECONDS;
import static com.example.jason.bloodGlucoseMonitoring.DBHelper.TABLE_MEASUREMENTS;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_DIABETES_1TYPE;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.unitBloodSugarMmolDefault;

public class StatisticsActivity extends AppCompatActivity {

    public static final int weekInSec = 7 * 24 * 3600; // 7 days
    public static final int monthWithoutWeekInSec = 23 * 24 * 3600; // 30 - 7 days

    // statistics vars
    long startWeekInSec;
    long startMonthInSec;

    // select query vars
    private static final String KEY_COUNT = "COUNT";
    private static final String KEY_AVG = "AVG";
    private static final String KEY_MIN = "MIN";
    private static final String KEY_MAX = "MAX";


    private boolean prefsUnitBloodSugarMmol;
    String sugarFormat;

    Calendar now;

    // views
    TextView tvCountForAllTime;
    TextView tvCountForWeek;
    TextView tvCountForMonth;

    TextView tvForWeekAvg;
    TextView tvForWeekMin;
    TextView tvForWeekMax;

    TextView tvForMonthAvg;
    TextView tvForMonthMin;
    TextView tvForMonthMax;

    TextView tvForAllTimeAvg;
    TextView tvForAllTimeMin;
    TextView tvForAllTimeMax;

    // SQLite database
    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        tvCountForAllTime = (TextView) findViewById(R.id.tvCountForAllTime);
        tvCountForWeek = (TextView) findViewById(R.id.tvCountForWeek);
        tvCountForMonth = (TextView) findViewById(R.id.tvCountForMonth);

        tvForWeekAvg = (TextView) findViewById(R.id.tvForWeekAvg);
        tvForWeekMin = (TextView) findViewById(R.id.tvForWeekMin);
        tvForWeekMax = (TextView) findViewById(R.id.tvForWeekMax);

        tvForMonthAvg = (TextView) findViewById(R.id.tvForMonthAvg);
        tvForMonthMin = (TextView) findViewById(R.id.tvForMonthMin);
        tvForMonthMax = (TextView) findViewById(R.id.tvForMonthMax);

        tvForAllTimeAvg = (TextView) findViewById(R.id.tvForAllTimeAvg);
        tvForAllTimeMin = (TextView) findViewById(R.id.tvForAllTimeMin);
        tvForAllTimeMax = (TextView) findViewById(R.id.tvForAllTimeMax);

        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadPreferences();
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
        prefsUnitBloodSugarMmol = sharedPref.getBoolean(KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL,
                unitBloodSugarMmolDefault);

        if (prefsUnitBloodSugarMmol) sugarFormat = "%1$.1f";
        else sugarFormat = "%1$.1f";

        now = Calendar.getInstance();

        // set starts of day  week in seconds
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);

        // get starts week in seconds
        now.setTimeInMillis(now.getTimeInMillis() - weekInSec * 1000);
        startWeekInSec = now.getTimeInMillis() / 1000;

        // get starts month in seconds
        now.setTimeInMillis(now.getTimeInMillis() - monthWithoutWeekInSec * 1000);
        startMonthInSec = now.getTimeInMillis() / 1000;
        /*
        // FUTURE - current week and month sugars
        now.set(Calendar.DAY_OF_WEEK, 2);
        startWeekInSec = now.getTimeInMillis() / 1000;
        now.set(Calendar.DAY_OF_MONTH, 1);
        startMonthInSec = now.getTimeInMillis() / 1000;*/

        loadStatistics();
    }

    // load preferences values
    public void loadPreferences() {
        // get preferences object
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);

        // get saved value
        prefsUnitBloodSugarMmol = sharedPref.getBoolean(KEY_PREFS_DIABETES_1TYPE,
                unitBloodSugarMmolDefault);
    }

    // get String query
    public String getStrQuery(String func) {
        return "SELECT " + func + "(" + KEY_MEASUREMENT + ") FROM " + TABLE_MEASUREMENTS;
    }

    // get String query from a given time
    public String getStrQuery(String func, long startTimeInSec) {
        return "SELECT " + func + "(" + KEY_MEASUREMENT + ") FROM " + TABLE_MEASUREMENTS
                + " WHERE " + KEY_TIME_IN_SECONDS + " > " + startTimeInSec;
    }

    // load statistics
    private void loadStatistics() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // statistics vars ---------------------start
        //  --counts
        int countAllTime;
        int countWeek;
        int countMonth;

        // weeks sugars
        float weekSugarAvg;
        float weekSugarMin;
        float weekSugarMax;

        // months sugars
        float monthSugarAvg;
        float monthSugarMin;
        float monthSugarMax;

        // all times sugars
        float allTimeSugarAvg;//
        float allTimeSugarMin;//
        float allTimeSugarMax;//
        // statistics vars ---------------------end

        // get all records count
        Cursor cursor = database.rawQuery(getStrQuery(KEY_COUNT), null);
        cursor.moveToFirst();
        countAllTime = cursor.getInt(0);

        // set Info to textViews
        tvCountForAllTime.setText(String.valueOf(countAllTime));

        if (countAllTime > 0) {

            // work with count
            // average
            cursor = database.rawQuery(getStrQuery(KEY_COUNT, startWeekInSec), null);
            cursor.moveToFirst();
            countWeek = prefsUnitBloodSugarMmol ? cursor.getInt(0) : cursor.getInt(0) * 18;

            // minimum
            cursor = database.rawQuery(getStrQuery(KEY_COUNT, startMonthInSec), null);
            cursor.moveToFirst();
            countMonth = prefsUnitBloodSugarMmol ? cursor.getInt(0) : cursor.getInt(0) * 18;

            // set Info to textViews
            tvCountForWeek.setText(String.valueOf(countWeek));
            tvCountForMonth.setText(String.valueOf(countMonth));

            // work with week sugars
            if (countWeek > 0) {
                // average
                cursor = database.rawQuery(getStrQuery(KEY_AVG, startWeekInSec), null);
                cursor.moveToFirst();
                weekSugarAvg = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // minimum
                cursor = database.rawQuery(getStrQuery(KEY_MIN, startWeekInSec), null);
                cursor.moveToFirst();
                weekSugarMin = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // maximum
                cursor = database.rawQuery(getStrQuery(KEY_MAX, startWeekInSec), null);
                cursor.moveToFirst();
                weekSugarMax = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // set Info to textViews
                tvForWeekAvg.setText(String.format(Locale.ENGLISH, sugarFormat, weekSugarAvg));
                tvForWeekMin.setText(String.format(Locale.ENGLISH, sugarFormat, weekSugarMin));
                tvForWeekMax.setText(String.format(Locale.ENGLISH, sugarFormat, weekSugarMax));
            }

            // work with month sugars
            if (countWeek > 0) {
                // average
                cursor = database.rawQuery(getStrQuery(KEY_AVG, startMonthInSec), null);
                cursor.moveToFirst();
                monthSugarAvg = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // minimum
                cursor = database.rawQuery(getStrQuery(KEY_MIN, startMonthInSec), null);
                cursor.moveToFirst();
                monthSugarMin = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // maximum
                cursor = database.rawQuery(getStrQuery(KEY_MAX, startMonthInSec), null);
                cursor.moveToFirst();
                monthSugarMax = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // set Info to textViews
                tvForMonthAvg.setText(String.format(Locale.ENGLISH, sugarFormat, monthSugarAvg));
                tvForMonthMin.setText(String.format(Locale.ENGLISH, sugarFormat, monthSugarMin));
                tvForMonthMax.setText(String.format(Locale.ENGLISH, sugarFormat, monthSugarMax));
            }

            // work with all time sugars
            // average
            cursor = database.rawQuery(getStrQuery(KEY_AVG), null);
            cursor.moveToFirst();
            allTimeSugarAvg = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

            // minimum
            cursor = database.rawQuery(getStrQuery(KEY_MIN), null);
            cursor.moveToFirst();
            allTimeSugarMin = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

            // maximum
            cursor = database.rawQuery(getStrQuery(KEY_MAX), null);
            cursor.moveToFirst();
            allTimeSugarMax = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

            // set Info to textViews
            tvForAllTimeAvg.setText(String.format(Locale.ENGLISH, sugarFormat, allTimeSugarAvg));
            tvForAllTimeMin.setText(String.format(Locale.ENGLISH, sugarFormat, allTimeSugarMin));
            tvForAllTimeMax.setText(String.format(Locale.ENGLISH, sugarFormat, allTimeSugarMax));
        }
        cursor.close();
        database.close();
    }
}
