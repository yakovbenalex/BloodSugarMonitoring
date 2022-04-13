package ru.opalevapps.EveryGlic.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import ru.opalevapps.EveryGlic.MyUtill
import ru.opalevapps.EveryGlic.R
import ru.opalevapps.EveryGlic.db.DBHelper
import ru.opalevapps.EveryGlic.db.ItemRecords
import ru.opalevapps.EveryGlic.db.ItemRecordsAdapter
import ru.opalevapps.EveryGlic.ui.AddOrChangeMeasurementActivity
import ru.opalevapps.EveryGlic.ui.CalculatorCarbsActivity
import ru.opalevapps.EveryGlic.ui.HelpfullInfoActivity
import ru.opalevapps.EveryGlic.ui.StatisticsActivity
import ru.opalevapps.EveryGlic.ui.WelcomeScreenActivity

// implements View.OnClickListener
class MainActivity : AppCompatActivity(), View.OnClickListener {
    // variables for preferences
    private var prefsBloodLowSugar = 0f
    private var prefsBloodHighSugar = 0f
    private var prefsUnitBloodSugarMmol = false
    private var prefsTimeFormat24h = false
    private var firstRun = false

    // other vars
    private var lastRecordsCount = 7

    // views
    private lateinit var btnAddMeasurement: Button
    private lateinit var btnStatistics: Button
    private lateinit var btnMeasurements: Button
    private lateinit var btnCalculatorCarbs: Button
    private lateinit var btnInfo: Button
    private lateinit var lvMeasurements3Last: ListView
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // get SharedPreferences settings object
        val sharedPref = getSharedPreferences(PreferencesActivity.KEY_PREFS, MODE_PRIVATE)
        val prefEditor = sharedPref.edit()
        firstRun = sharedPref.getBoolean(PreferencesActivity.KEY_PREFS_FIRST_RUN_AGREEMENT, true)

        // check for first run app
        if (firstRun) {
            var intent = Intent(this@MainActivity, PreferencesActivity::class.java)
            startActivity(intent)
            intent = Intent(this@MainActivity, WelcomeScreenActivity::class.java)
            startActivity(intent)
        }
        setContentView(R.layout.activity_main)

        // save change of preferences
        prefEditor.apply()
        initViews()
        dbHelper = DBHelper(this)
        dbHelper.databaseName.isEmpty()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        MyUtill.createInfoItemInActionBar(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MyUtill.parseMenuItemMain(this, item)
        MyUtill.parseMenuItemInfo(this, item)
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnAddMeasurement -> {
                val intentAddMeasurementActivity =
                    Intent(this, AddOrChangeMeasurementActivity::class.java)
                startActivity(intentAddMeasurementActivity)
            }
            R.id.btnCalculatorCarbs -> {
                val intentCalculatorCarbsActivity =
                    Intent(this, CalculatorCarbsActivity::class.java)
                startActivity(intentCalculatorCarbsActivity)
            }
            R.id.btnMeasurements -> {
                val intentMeasurementsActivity = Intent(this, MeasurementsActivity::class.java)
                startActivity(intentMeasurementsActivity)
            }
            R.id.btnStatistics -> {
                val intentStatisticsActivity = Intent(this, StatisticsActivity::class.java)
                startActivity(intentStatisticsActivity)
            }
            R.id.btnInfo -> {
                val intentInfoActivity = Intent(this, HelpfullInfoActivity::class.java)
                startActivity(intentInfoActivity)
            }
            else -> {}
        }
    }

    override fun onResume() {
        loadPreferences()
        load3LastRecords()
        super.onResume()
    }

    // load 3 last records for MainActivity
    private fun load3LastRecords() {
        val database = dbHelper.readableDatabase
        val data = ArrayList<ItemRecords>()

        // variables to filling them from database
        var id: Int
        var measurement: Float
        var timeInSeconds: Long
        var comment: String
        val cursor = database.query(
            DBHelper.TABLE_MEASUREMENTS,
            null, null, null, null, null,
            DBHelper.KEY_TIME_IN_SECONDS + " DESC", Integer.toString(lastRecordsCount)
        )

        // if available at least one records, get data from database
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
                // add data to item for data of listView adapter
                data.add(ItemRecords(id, measurement, timeInSeconds, comment))
            } while (cursor.moveToNext())
        } //else { //No Records }
        cursor.close()
        database.close()
        lvMeasurements3Last.adapter = ItemRecordsAdapter(
            this, data,
            prefsBloodLowSugar, prefsBloodHighSugar, prefsUnitBloodSugarMmol, prefsTimeFormat24h
        )
    }

    // load settings from shared preferences
    private fun loadPreferences() {
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
    }

    override fun onBackPressed() {
        openQuitDialog()
    }

    // open exit confirmation dialog
    private fun openQuitDialog() {
        val quitDialog = AlertDialog.Builder(this@MainActivity)
        quitDialog.setTitle(R.string.exit_are_you_sure)
        quitDialog.setPositiveButton(R.string.yes) { dialog: DialogInterface?, which: Int -> finish() }
        quitDialog.setNegativeButton(R.string.cancel) { dialog: DialogInterface?, which: Int -> }
        quitDialog.setCancelable(false)
        quitDialog.show()
    }

    // initialize views on screen and their listening
    private fun initViews() {
        // find view on screen by id
        btnCalculatorCarbs = findViewById(R.id.btnCalculatorCarbs)
        btnAddMeasurement = findViewById(R.id.btnAddMeasurement)
        btnMeasurements = findViewById(R.id.btnMeasurements)
        btnStatistics = findViewById(R.id.btnStatistics)
        btnInfo = findViewById(R.id.btnInfo)
        lvMeasurements3Last = findViewById(R.id.lvMeasurements3Last)

        // listeners for views
        btnAddMeasurement.setOnClickListener(this)
        btnStatistics.setOnClickListener(this)
        btnMeasurements.setOnClickListener(this)
        btnCalculatorCarbs.setOnClickListener(this)
        btnInfo.setOnClickListener(this)
        lvMeasurements3Last.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            val intentAddMeasurementActivity =
                Intent(this, AddOrChangeMeasurementActivity::class.java)
            intentAddMeasurementActivity.putExtra("idRec", id)
            startActivity(intentAddMeasurementActivity)
        }
    }

    companion object {
        private const val TAG = "myLog"
    }
}