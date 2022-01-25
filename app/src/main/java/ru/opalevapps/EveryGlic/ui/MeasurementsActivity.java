package ru.opalevapps.EveryGlic.ui;

import static ru.opalevapps.EveryGlic.MyWorks.createInfoItemInActionBar;
import static ru.opalevapps.EveryGlic.MyWorks.parseMenuItemInfo;
import static ru.opalevapps.EveryGlic.db.DBHelper.KEY_TIME_IN_SECONDS;
import static ru.opalevapps.EveryGlic.ui.PreferencesActivity.BLOOD_HIGH_SUGAR_DEFAULT;
import static ru.opalevapps.EveryGlic.ui.PreferencesActivity.BLOOD_LOW_SUGAR_DEFAULT;
import static ru.opalevapps.EveryGlic.ui.PreferencesActivity.KEY_PREFS;
import static ru.opalevapps.EveryGlic.ui.PreferencesActivity.KEY_PREFS_BLOOD_HIGH_SUGAR;
import static ru.opalevapps.EveryGlic.ui.PreferencesActivity.KEY_PREFS_BLOOD_LOW_SUGAR;
import static ru.opalevapps.EveryGlic.ui.PreferencesActivity.KEY_PREFS_TIME_FORMAT_24H;
import static ru.opalevapps.EveryGlic.ui.PreferencesActivity.KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL;
import static ru.opalevapps.EveryGlic.ui.PreferencesActivity.TIME_FORMAT_24H_DEFAULT;
import static ru.opalevapps.EveryGlic.ui.PreferencesActivity.UNIT_BLOOD_SUGAR_MMOL_DEFAULT;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import ru.opalevapps.EveryGlic.R;
import ru.opalevapps.EveryGlic.db.DBHelper;
import ru.opalevapps.EveryGlic.db.ItemRecords;
import ru.opalevapps.EveryGlic.db.ItemRecordsAdapter;

public class MeasurementsActivity extends AppCompatActivity {
    private static final String TAG = "myLog";

    // views
    ListView lvMeasurementsAll;

    // temporary variables
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

        initViews();

        // get settings object
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);

        // get saved value for diabetes
        prefsUnitBloodSugarMmol = sharedPref.getBoolean(KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL, UNIT_BLOOD_SUGAR_MMOL_DEFAULT);
        prefsBloodLowSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_LOW_SUGAR, BLOOD_LOW_SUGAR_DEFAULT);
        prefsBloodHighSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_HIGH_SUGAR, BLOOD_HIGH_SUGAR_DEFAULT);
        prefsTimeFormat24h = sharedPref.getBoolean(KEY_PREFS_TIME_FORMAT_24H, TIME_FORMAT_24H_DEFAULT);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        createInfoItemInActionBar(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        parseMenuItemInfo(this, item);
        return super.onOptionsItemSelected(item);
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
                KEY_TIME_IN_SECONDS + " DESC");

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

    // initialize views on screen and their listening
    public void initViews() {
        lvMeasurementsAll = findViewById(R.id.lvMeasurementsAll);

        lvMeasurementsAll.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MeasurementsActivity.this, AddOrChangeMeasurementActivity.class);
            intent.putExtra("idRec", id);
            startActivity(intent);
        });
    }
}