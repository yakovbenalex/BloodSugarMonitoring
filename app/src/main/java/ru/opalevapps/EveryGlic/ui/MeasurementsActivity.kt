package ru.opalevapps.EveryGlic.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import ru.opalevapps.EveryGlic.MyUtill
import ru.opalevapps.EveryGlic.R
import ru.opalevapps.EveryGlic.db.DBHelper
import ru.opalevapps.EveryGlic.db.ItemRecords
import ru.opalevapps.EveryGlic.db.ItemRecordsAdapter
import ru.opalevapps.EveryGlic.ui.AddOrChangeMeasurementActivity

class MeasurementsActivity : AppCompatActivity() {
    // views
    private lateinit var lvMeasurementsAll: ListView

    // temporary variables
    private var lvIndexPos = 0

    // variables for preferences
    private var prefsBloodLowSugar = 0f
    private var prefsBloodHighSugar = 0f
    private var prefsTimeFormat24h = false
    private var prefsUnitBloodSugarMmol = false

    //SQLite database
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurements)
        initViews()

        // get settings object
        val sharedPref = getSharedPreferences(PreferencesActivity.KEY_PREFS, MODE_PRIVATE)

        // get saved value for diabetes
        prefsUnitBloodSugarMmol = sharedPref.getBoolean(
            PreferencesActivity.KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL,
            PreferencesActivity.UNIT_BLOOD_SUGAR_MMOL_DEFAULT
        )
        prefsBloodLowSugar = sharedPref.getFloat(
            PreferencesActivity.KEY_PREFS_BLOOD_LOW_SUGAR,
            PreferencesActivity.BLOOD_LOW_SUGAR_DEFAULT
        )
        prefsBloodHighSugar = sharedPref.getFloat(
            PreferencesActivity.KEY_PREFS_BLOOD_HIGH_SUGAR,
            PreferencesActivity.BLOOD_HIGH_SUGAR_DEFAULT
        )
        prefsTimeFormat24h = sharedPref.getBoolean(
            PreferencesActivity.KEY_PREFS_TIME_FORMAT_24H,
            PreferencesActivity.TIME_FORMAT_24H_DEFAULT
        )
        dbHelper = DBHelper(this)
    }

    override fun onStart() {
        super.onStart()
        loadRecords()
    }

    override fun onPause() {
        super.onPause()
        lvIndexPos = lvMeasurementsAll.firstVisiblePosition
    }

    override fun onResume() {
        // set list view last pos
        if (lvMeasurementsAll.count > lvIndexPos)
            lvMeasurementsAll.setSelectionFromTop(lvIndexPos, 0)
        else
            lvMeasurementsAll.setSelectionFromTop(0, 0)
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        MyUtill.createInfoItemInActionBar(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MyUtill.parseMenuItemInfo(this, item)
        return super.onOptionsItemSelected(item)
    }

    // load records from DB
    private fun loadRecords() {
        val database = dbHelper.readableDatabase
        val data = ArrayList<ItemRecords>()
        var id: Int
        var measurement: Float
        var timeInSeconds: Long
        var comment: String
        val cursor = database.query(
            DBHelper.TABLE_MEASUREMENTS, null, null, null, null, null,
            DBHelper.KEY_TIME_IN_SECONDS + " DESC"
        )
        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(DBHelper.KEY_ID)
            val idMeasurement = cursor.getColumnIndex(DBHelper.KEY_MEASUREMENT)
            val idTimeInSeconds = cursor.getColumnIndex(DBHelper.KEY_TIME_IN_SECONDS)
            val idComment = cursor.getColumnIndex(DBHelper.KEY_COMMENT)
            do {
                id = cursor.getInt(idIndex)
                measurement = cursor.getFloat(idMeasurement)
                timeInSeconds = cursor.getLong(idTimeInSeconds)
                comment = cursor.getString(idComment)
                data.add(ItemRecords(id, measurement, timeInSeconds, comment))
            } while (cursor.moveToNext())
        } //else { //No Records }
        cursor.close()
        database.close()
        lvMeasurementsAll.adapter = ItemRecordsAdapter(
            this, data,
            prefsBloodLowSugar, prefsBloodHighSugar, prefsUnitBloodSugarMmol, prefsTimeFormat24h
        )
    }

    // initialize views on screen and their listening
    private fun initViews() {
        lvMeasurementsAll = findViewById(R.id.lvMeasurementsAll)
        lvMeasurementsAll.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val intent =
                Intent(this, AddOrChangeMeasurementActivity::class.java)
            intent.putExtra("idRec", id)
            startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "myLog"
    }
}