package com.example.jason.EveryGlic;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.example.jason.EveryGlic.DBHelper.KEY_MEASUREMENT;
import static com.example.jason.EveryGlic.DBHelper.KEY_TIME_IN_SECONDS;
import static com.example.jason.EveryGlic.DBHelper.TABLE_MEASUREMENTS;
import static com.example.jason.EveryGlic.MyWorks.createInfoItemInActionBar;
import static com.example.jason.EveryGlic.MyWorks.parseMenuItemInfo;
import static com.example.jason.EveryGlic.PreferencesActivity.BEGINNING_WEEK_DEFAULT;
import static com.example.jason.EveryGlic.PreferencesActivity.BLOOD_HIGH_SUGAR_DEFAULT;
import static com.example.jason.EveryGlic.PreferencesActivity.BLOOD_LOW_SUGAR_DEFAULT;
import static com.example.jason.EveryGlic.PreferencesActivity.KEY_PREFS;
import static com.example.jason.EveryGlic.PreferencesActivity.KEY_PREFS_BEGINNING_WEEK;
import static com.example.jason.EveryGlic.PreferencesActivity.KEY_PREFS_BLOOD_HIGH_SUGAR;
import static com.example.jason.EveryGlic.PreferencesActivity.KEY_PREFS_BLOOD_LOW_SUGAR;
import static com.example.jason.EveryGlic.PreferencesActivity.KEY_PREFS_DIABETES_1TYPE;
import static com.example.jason.EveryGlic.PreferencesActivity.UNIT_BLOOD_SUGAR_MMOL_DEFAULT;

public class StatisticsActivity extends AppCompatActivity {

    private static final String TAG = "myLog";

    // statistics vars
    long startLastWeekInSec;
    long startCurWeekInSec;
    long startLastMonthInSec;
    long startCurMonthInSec;

    // select query vars
    private static final String KEY_COUNT = "COUNT";
    private static final String KEY_AVG = "AVG";
    private static final String KEY_MIN = "MIN";
    private static final String KEY_MAX = "MAX";
    private static final boolean KEY_LOW_SUGAR = true;
    private static final boolean KEY_HIGH_SUGAR = false;

    // variables for preferences
    boolean prefsUnitBloodSugarMmol;
    float prefsBloodLowSugar;
    float prefsBloodHighSugar;
    int prefsBeginningWeek;

    // For date and time
    String sugarFormat;

    Calendar now;

    // views
    TextView tvAllTimeCount;
    TextView tvAllTimeCountLow;
    TextView tvAllTimeCountHigh;
    TextView tvAllTimeAvg;
    TextView tvAllTimeMin;
    TextView tvAllTimeMax;

    TextView tvCurWeekCount;
    TextView tvCurWeekCountLow;
    TextView tvCurWeekCountHigh;
    TextView tvCurWeekAvg;
    TextView tvCurWeekMin;
    TextView tvCurWeekMax;

    TextView tvCurMonthCount;
    TextView tvCurMonthCountLow;
    TextView tvCurMonthCountHigh;
    TextView tvCurMonthAvg;
    TextView tvCurMonthMin;
    TextView tvCurMonthMax;

    TextView tvLastWeekCount;
    TextView tvLastWeekCountLow;
    TextView tvLastWeekCountHigh;
    TextView tvLastWeekAvg;
    TextView tvLastWeekMin;
    TextView tvLastWeekMax;

    TextView tvLastMonthCount;
    TextView tvLastMonthCountLow;
    TextView tvLastMonthCountHigh;
    TextView tvLastMonthAvg;
    TextView tvLastMonthMin;
    TextView tvLastMonthMax;

    // SQLite database
    DBHelper dbHelper;
    String DATE_FORMAT = "dd/MM EEE- ";
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // find views on screen by id
        tvAllTimeCount = findViewById(R.id.tvCountAllTime);
        tvAllTimeCountLow = findViewById(R.id.tvLastAllTimeCountLow);
        tvAllTimeCountHigh = findViewById(R.id.tvLastAllTimeCountHigh);
        tvAllTimeAvg = findViewById(R.id.tvAllTimeAvg);
        tvAllTimeMin = findViewById(R.id.tvAllTimeMin);
        tvAllTimeMax = findViewById(R.id.tvAllTimeMax);

        tvCurWeekCount = findViewById(R.id.tvCurWeekCount);
        tvCurWeekCountLow = findViewById(R.id.tvCurWeekCountLow);
        tvCurWeekCountHigh = findViewById(R.id.tvCurWeekCountHigh);
        tvCurWeekAvg = findViewById(R.id.tvCurWeekAvg);
        tvCurWeekMin = findViewById(R.id.tvCurWeekMin);
        tvCurWeekMax = findViewById(R.id.tvCurWeekMax);

        tvCurMonthCount = findViewById(R.id.tvCountCurMonth);
        tvCurMonthCountLow = findViewById(R.id.tvCurMonthCountLow);
        tvCurMonthCountHigh = findViewById(R.id.tvCurMonthCountHigh);
        tvCurMonthAvg = findViewById(R.id.tvCurMonthAvg);
        tvCurMonthMin = findViewById(R.id.tvCurMonthMin);
        tvCurMonthMax = findViewById(R.id.tvCurMonthMax);

        tvLastWeekCount = findViewById(R.id.tvCountLastWeek);
        tvLastWeekCountLow = findViewById(R.id.tvLastWeekCountLow);
        tvLastWeekCountHigh = findViewById(R.id.tvLastWeekCountHigh);
        tvLastWeekAvg = findViewById(R.id.tvLastWeekAvg);
        tvLastWeekMin = findViewById(R.id.tvLastWeekMin);
        tvLastWeekMax = findViewById(R.id.tvLastWeekMax);

        tvLastMonthCount = findViewById(R.id.tvCountLastMonth);
        tvLastMonthCountLow = findViewById(R.id.tvLastMonthCountLow);
        tvLastMonthCountHigh = findViewById(R.id.tvLastMonthCountHigh);
        tvLastMonthAvg = findViewById(R.id.tvLastMonthAvg);
        tvLastMonthMin = findViewById(R.id.tvLastMonthMin);
        tvLastMonthMax = findViewById(R.id.tvLastMonthMax);

        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadPreferences();

        if (prefsUnitBloodSugarMmol) sugarFormat = "%1$.1f";
        else sugarFormat = "%1$.1f";

        // get starts last week in seconds
        resetNowDate();
        now.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) - 7);
        startLastWeekInSec = now.getTimeInMillis() / 1000;

        // get starts current week in seconds
        resetNowDate();
        now.set(Calendar.DAY_OF_WEEK, prefsBeginningWeek);
        if (prefsBeginningWeek < 2) now.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) - 7);
        startCurWeekInSec = now.getTimeInMillis() / 1000;

        // get starts current month in seconds
        resetNowDate();
        now.set(Calendar.DAY_OF_MONTH, 1);
        startCurMonthInSec = now.getTimeInMillis() / 1000;

        // get starts last month in seconds
        resetNowDate();
        now.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) - 30);
        startLastMonthInSec = now.getTimeInMillis() / 1000;

        Log.d(TAG, "StatisticsActivity, onResume: " + sdf.format(startCurWeekInSec * 1000) + prefsBeginningWeek);
        loadStatistics();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        createInfoItemInActionBar(menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        createInfoItemInActionBar(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        parseMenuItemInfo(this, item);
        return super.onOptionsItemSelected(item);
    }

    // load preferences values
    public void resetNowDate() {
        now = Calendar.getInstance();
        // set starts of day  week in seconds
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
    }

    // load preferences values
    public void loadPreferences() {
        // get preferences object
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);

        // get saved value
        prefsUnitBloodSugarMmol = sharedPref.getBoolean(KEY_PREFS_DIABETES_1TYPE,
                UNIT_BLOOD_SUGAR_MMOL_DEFAULT);

        prefsBloodLowSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_LOW_SUGAR, BLOOD_LOW_SUGAR_DEFAULT);
        prefsBloodHighSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_HIGH_SUGAR, BLOOD_HIGH_SUGAR_DEFAULT);
        prefsBeginningWeek = sharedPref.getInt(KEY_PREFS_BEGINNING_WEEK, BEGINNING_WEEK_DEFAULT);
    }

    // get String query
    public String getStrQuery(String func) {
        return "SELECT " + func + "(" + KEY_MEASUREMENT + ") FROM " + TABLE_MEASUREMENTS;
    }

    // get String query from a given time
    public String getStrQuery(String func, float boundarySugar, boolean lowSugar) {
        String equalitySign;
        if (lowSugar) equalitySign = " < ";
        else equalitySign = " > ";
        return "SELECT " + func + "(" + KEY_MEASUREMENT + ") FROM " + TABLE_MEASUREMENTS
                + " WHERE " + KEY_MEASUREMENT + equalitySign + String.valueOf(boundarySugar);
    }

    // get String query from a given time
    public String getStrQuery(String func, long startTimeInSec) {
        return "SELECT " + func + "(" + KEY_MEASUREMENT + ") FROM " + TABLE_MEASUREMENTS
                + " WHERE " + KEY_TIME_IN_SECONDS + " > " + String.valueOf(startTimeInSec);
    }

    // get String query from a given time
    public String getStrQuery(String func, long startTimeInSec, float boundarySugar, boolean lowSugar) {
        String equalitySign;
        if (lowSugar) equalitySign = " < ";
        else equalitySign = " > ";
        return "SELECT " + func + "(" + KEY_MEASUREMENT + ") FROM " + TABLE_MEASUREMENTS
                + " WHERE " + KEY_TIME_IN_SECONDS + " > " + startTimeInSec
                + " AND " + KEY_MEASUREMENT + equalitySign + String.valueOf(boundarySugar);
    }

    // load statistics
    private void loadStatistics() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // statistics vars ---------------------start
        // all times sugars
        int allTimeCount;
        int allTimeCountLow;
        int allTimeCountHigh;
        float allTimeSugarAvg;
        float allTimeSugarMin;
        float allTimeSugarMax;

        // last weeks sugars
        int сurWeekCount;
        int сurWeekCountLow;
        int сurWeekCountHigh;
        float curWeekSugarAvg;
        float curWeekSugarMin;
        float curWeekSugarMax;

        // last months sugars
        int сurMonthCount;
        int сurMonthCountLow;
        int сurMonthCountHigh;
        float curMonthSugarAvg;
        float curMonthSugarMin;
        float curMonthSugarMax;

        // last weeks sugars
        int lastWeekCount;
        int lastWeekCountLow;
        int lastWeekCountHigh;
        float lastWeekSugarAvg;
        float lastWeekSugarMin;
        float lastWeekSugarMax;

        // last months sugars
        int lastMonthCount;
        int lastMonthCountLow;
        int lastMonthCountHigh;
        float lastMonthSugarAvg;
        float lastMonthSugarMin;
        float lastMonthSugarMax;
        // statistics vars ---------------------end

        // get all records count
        Cursor cursor = database.rawQuery(getStrQuery(KEY_COUNT), null);
        cursor.moveToFirst();
        allTimeCount = cursor.getInt(0);

        // set Info to textViews
        tvAllTimeCount.setText(String.valueOf(allTimeCount));

        // if measurement doesn't exist then don't try get statistics data
        if (allTimeCount > 0) {
            // work with count
            // current week
            cursor = database.rawQuery(getStrQuery(KEY_COUNT, startCurWeekInSec), null);
            cursor.moveToFirst();
            сurWeekCount = cursor.getInt(0);

            // current month
            cursor = database.rawQuery(getStrQuery(KEY_COUNT, startCurMonthInSec), null);
            cursor.moveToFirst();
            сurMonthCount = cursor.getInt(0);

            // last week
            cursor = database.rawQuery(getStrQuery(KEY_COUNT, startLastWeekInSec), null);
            cursor.moveToFirst();
            lastWeekCount = cursor.getInt(0);

            // last month
            cursor = database.rawQuery(getStrQuery(KEY_COUNT, startLastMonthInSec), null);
            cursor.moveToFirst();
            lastMonthCount = cursor.getInt(0);

            // set Info to textViews
            tvCurWeekCount.setText(String.valueOf(сurWeekCount));
            tvCurMonthCount.setText(String.valueOf(сurMonthCount));
            tvLastWeekCount.setText(String.valueOf(lastWeekCount));
            tvLastMonthCount.setText(String.valueOf(lastMonthCount));

            // work with last week sugars
            if (lastWeekCount > 0) {
                // average
                cursor = database.rawQuery(getStrQuery(KEY_AVG, startLastWeekInSec), null);
                cursor.moveToFirst();
                lastWeekSugarAvg = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // minimum
                cursor = database.rawQuery(getStrQuery(KEY_MIN, startLastWeekInSec), null);
                cursor.moveToFirst();
                lastWeekSugarMin = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // maximum
                cursor = database.rawQuery(getStrQuery(KEY_MAX, startLastWeekInSec), null);
                cursor.moveToFirst();
                lastWeekSugarMax = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // set Info to textViews
                tvLastWeekAvg.setText(String.format(Locale.ENGLISH, sugarFormat, lastWeekSugarAvg));
                tvLastWeekMin.setText(String.format(Locale.ENGLISH, sugarFormat, lastWeekSugarMin));
                tvLastWeekMax.setText(String.format(Locale.ENGLISH, sugarFormat, lastWeekSugarMax));
            }

            // work with last month sugars
            if (lastMonthCount > 0) {
                // average
                cursor = database.rawQuery(getStrQuery(KEY_AVG, startLastMonthInSec), null);
                cursor.moveToFirst();
                lastMonthSugarAvg = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // minimum
                cursor = database.rawQuery(getStrQuery(KEY_MIN, startLastMonthInSec), null);
                cursor.moveToFirst();
                lastMonthSugarMin = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // maximum
                cursor = database.rawQuery(getStrQuery(KEY_MAX, startLastMonthInSec), null);
                cursor.moveToFirst();
                lastMonthSugarMax = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // set Info to textViews
                tvLastMonthAvg.setText(String.format(Locale.ENGLISH, sugarFormat, lastMonthSugarAvg));
                tvLastMonthMin.setText(String.format(Locale.ENGLISH, sugarFormat, lastMonthSugarMin));
                tvLastMonthMax.setText(String.format(Locale.ENGLISH, sugarFormat, lastMonthSugarMax));
            }

            // current week sugars work
            if (сurWeekCount > 0) {
                // average
                cursor = database.rawQuery(getStrQuery(KEY_AVG, startCurMonthInSec), null);
                cursor.moveToFirst();
                curWeekSugarAvg = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // minimum
                cursor = database.rawQuery(getStrQuery(KEY_MIN, startCurWeekInSec), null);
                cursor.moveToFirst();
                curWeekSugarMin = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // maximum
                cursor = database.rawQuery(getStrQuery(KEY_MAX, startCurWeekInSec), null);
                cursor.moveToFirst();
                curWeekSugarMax = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // set Info to textViews
                tvCurWeekAvg.setText(String.format(Locale.ENGLISH, sugarFormat, curWeekSugarAvg));
                tvCurWeekMin.setText(String.format(Locale.ENGLISH, sugarFormat, curWeekSugarMin));
                tvCurWeekMax.setText(String.format(Locale.ENGLISH, sugarFormat, curWeekSugarMax));
            }

            //  current month sugars work
            if (сurMonthCount > 0) {
                // average
                cursor = database.rawQuery(getStrQuery(KEY_AVG, startCurMonthInSec), null);
                cursor.moveToFirst();
                curMonthSugarAvg = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // minimum
                cursor = database.rawQuery(getStrQuery(KEY_MIN, startCurMonthInSec), null);
                cursor.moveToFirst();
                curMonthSugarMin = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // maximum
                cursor = database.rawQuery(getStrQuery(KEY_MAX, startCurMonthInSec), null);
                cursor.moveToFirst();
                curMonthSugarMax = prefsUnitBloodSugarMmol ? cursor.getFloat(0) : cursor.getFloat(0) * 18;

                // set Info to textViews
                tvCurMonthAvg.setText(String.format(Locale.ENGLISH, sugarFormat, curMonthSugarAvg));
                tvCurMonthMin.setText(String.format(Locale.ENGLISH, sugarFormat, curMonthSugarMin));
                tvCurMonthMax.setText(String.format(Locale.ENGLISH, sugarFormat, curMonthSugarMax));
            }

            // all time sugars work
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
            tvAllTimeAvg.setText(String.format(Locale.ENGLISH, sugarFormat, allTimeSugarAvg));
            tvAllTimeMin.setText(String.format(Locale.ENGLISH, sugarFormat, allTimeSugarMin));
            tvAllTimeMax.setText(String.format(Locale.ENGLISH, sugarFormat, allTimeSugarMax));


            // count of low and high sugars for all time
            cursor = database.rawQuery(getStrQuery(KEY_COUNT, prefsBloodLowSugar, KEY_LOW_SUGAR), null);
            cursor.moveToFirst();
            allTimeCountLow = cursor.getInt(0);

            cursor = database.rawQuery(getStrQuery(KEY_COUNT, prefsBloodHighSugar, KEY_HIGH_SUGAR), null);
            cursor.moveToFirst();
            allTimeCountHigh = cursor.getInt(0);

            // set Info to textViews
            tvAllTimeCountLow.setText(String.valueOf(allTimeCountLow));
            tvAllTimeCountHigh.setText(String.valueOf(allTimeCountHigh));

            //  count of low sugars work
            if (allTimeCountLow > 0) {
                // current month low sugars
                cursor = database.rawQuery(getStrQuery(KEY_COUNT, startCurMonthInSec, prefsBloodLowSugar, KEY_LOW_SUGAR), null);
                cursor.moveToFirst();
                сurMonthCountLow = cursor.getInt(0);

                // last month low sugars
                cursor = database.rawQuery(getStrQuery(KEY_COUNT, startLastMonthInSec, prefsBloodLowSugar, KEY_LOW_SUGAR), null);
                cursor.moveToFirst();
                lastMonthCountLow = cursor.getInt(0);

                // current week low sugars
                if (сurMonthCountLow > 0) {
                    cursor = database.rawQuery(getStrQuery(KEY_COUNT, startCurWeekInSec, prefsBloodLowSugar, KEY_LOW_SUGAR), null);
                    cursor.moveToFirst();
                    сurWeekCountLow = cursor.getInt(0);
                } else сurWeekCountLow = 0;

                // last week low sugars
                if (lastMonthCountLow > 0) {
                    cursor = database.rawQuery(getStrQuery(KEY_COUNT, startLastWeekInSec, prefsBloodLowSugar, KEY_LOW_SUGAR), null);
                    cursor.moveToFirst();
                    lastWeekCountLow = cursor.getInt(0);
                } else lastWeekCountLow = 0;

                // set count of low sugars Info to textViews
                tvCurWeekCountLow.setText(String.valueOf(сurWeekCountLow));
                tvLastWeekCountLow.setText(String.valueOf(lastWeekCountLow));

            } else {
                сurMonthCountLow = 0;
                lastMonthCountLow = 0;
            }

            //  count of high sugars work
            if (allTimeCountHigh > 0) {
                // current month high sugars
                cursor = database.rawQuery(getStrQuery(KEY_COUNT, startCurMonthInSec, prefsBloodHighSugar, KEY_HIGH_SUGAR), null);
                cursor.moveToFirst();
                сurMonthCountHigh = cursor.getInt(0);

                // last month high sugars
                cursor = database.rawQuery(getStrQuery(KEY_COUNT, startLastMonthInSec, prefsBloodHighSugar, KEY_HIGH_SUGAR), null);
                cursor.moveToFirst();
                lastMonthCountHigh = cursor.getInt(0);

                // current week high sugars
                if (сurMonthCountHigh > 0) {
                    cursor = database.rawQuery(getStrQuery(KEY_COUNT, startCurWeekInSec, prefsBloodHighSugar, KEY_HIGH_SUGAR), null);
                    cursor.moveToFirst();
                    сurWeekCountHigh = cursor.getInt(0);
                } else сurWeekCountHigh = 0;

                // last week high sugars
                if (lastMonthCountHigh > 0) {
                    cursor = database.rawQuery(getStrQuery(KEY_COUNT, startLastWeekInSec, prefsBloodHighSugar, KEY_HIGH_SUGAR), null);
                    cursor.moveToFirst();
                    lastWeekCountHigh = cursor.getInt(0);
                } else lastWeekCountHigh = 0;

                // set count of high sugars Info to textViews
                tvCurWeekCountHigh.setText(String.valueOf(сurWeekCountHigh));
                tvLastWeekCountHigh.setText(String.valueOf(lastWeekCountHigh));

            } else {
                сurMonthCountHigh = 0;
                lastMonthCountHigh = 0;
            }

            // set count of low and high sugars Info to textViews
            tvCurMonthCountLow.setText(String.valueOf(сurMonthCountLow));
            tvLastMonthCountLow.setText(String.valueOf(lastMonthCountLow));

            tvCurMonthCountHigh.setText(String.valueOf(сurMonthCountHigh));
            tvLastMonthCountHigh.setText(String.valueOf(lastMonthCountHigh));
        }
        cursor.close();
        database.close();
    }
}