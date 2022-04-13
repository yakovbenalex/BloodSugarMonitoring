package ru.opalevapps.EveryGlic.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.opalevapps.EveryGlic.MyUtill
import ru.opalevapps.EveryGlic.R
import ru.opalevapps.EveryGlic.db.DBHelper
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : AppCompatActivity() {
    // statistics vars
    private var startLastWeekInSec: Long = 0
    private var startCurWeekInSec: Long = 0
    private var startLastMonthInSec: Long = 0
    private var startCurMonthInSec: Long = 0

    // variables for preferences
    private var prefsUnitBloodSugarMmol = false
    private var prefsBloodLowSugar = 0f
    private var prefsBloodHighSugar = 0f
    private var prefsBeginningWeek = 0

    // For date and time
    private var sugarFormat: String? = null
    private var now: Calendar? = null

    // views
    private var tvAllTimeCount: TextView? = null
    private var tvAllTimeCountLow: TextView? = null
    private var tvAllTimeCountHigh: TextView? = null
    private var tvAllTimeAvg: TextView? = null
    private var tvAllTimeMin: TextView? = null
    private var tvAllTimeMax: TextView? = null
    private var tvCurWeekCount: TextView? = null
    private var tvCurWeekCountLow: TextView? = null
    private var tvCurWeekCountHigh: TextView? = null
    private var tvCurWeekAvg: TextView? = null
    private var tvCurWeekMin: TextView? = null
    private var tvCurWeekMax: TextView? = null
    private var tvCurMonthCount: TextView? = null
    private var tvCurMonthCountLow: TextView? = null
    private var tvCurMonthCountHigh: TextView? = null
    private var tvCurMonthAvg: TextView? = null
    private var tvCurMonthMin: TextView? = null
    private var tvCurMonthMax: TextView? = null
    private var tvLastWeekCount: TextView? = null
    private var tvLastWeekCountLow: TextView? = null
    private var tvLastWeekCountHigh: TextView? = null
    private var tvLastWeekAvg: TextView? = null
    private var tvLastWeekMin: TextView? = null
    private var tvLastWeekMax: TextView? = null
    private var tvLastMonthCount: TextView? = null
    private var tvLastMonthCountLow: TextView? = null
    private var tvLastMonthCountHigh: TextView? = null
    private var tvLastMonthAvg: TextView? = null
    private var tvLastMonthMin: TextView? = null
    private var tvLastMonthMax: TextView? = null

    // SQLite database
    private var dbHelper: DBHelper? = null

    // display date format
    private var DATE_FORMAT = "dd/MM EEE- "
    private var simpleDateFormat = SimpleDateFormat(DATE_FORMAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        initViews()
        dbHelper = DBHelper(this)
    }

    override fun onResume() {
        super.onResume()
        loadPreferences()

        // set display sugar format
        sugarFormat = if (prefsUnitBloodSugarMmol) "%1$.1f" else "%1$.0f"

        /*if (prefsUnitBloodSugarMmol) sugarFormat = "%1$.1f";
        else sugarFormat = "%1$.0f";*/

        // get starts last week in seconds
        resetNowDate()
        now!![Calendar.DAY_OF_YEAR] = now!![Calendar.DAY_OF_YEAR] - 7
        startLastWeekInSec = now!!.timeInMillis / 1000

        // get starts current week in seconds
        resetNowDate()
        now!![Calendar.DAY_OF_WEEK] = prefsBeginningWeek
        if (prefsBeginningWeek < 2) now!![Calendar.DAY_OF_YEAR] = now!![Calendar.DAY_OF_YEAR] - 7
        startCurWeekInSec = now!!.timeInMillis / 1000

        // get starts current month in seconds
        resetNowDate()
        now!![Calendar.DAY_OF_MONTH] = 1
        startCurMonthInSec = now!!.timeInMillis / 1000

        // get starts last month in seconds
        resetNowDate()
        now!![Calendar.DAY_OF_YEAR] = now!![Calendar.DAY_OF_YEAR] - 30
        startLastMonthInSec = now!!.timeInMillis / 1000
        Log.d(
            TAG,
            "StatisticsActivity, onResume: " + simpleDateFormat.format(startCurWeekInSec * 1000) + prefsBeginningWeek
        )
        loadStatistics()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        MyUtill.createInfoItemInActionBar(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MyUtill.parseMenuItemInfo(this, item)
        return super.onOptionsItemSelected(item)
    }

    // load preferences values
    fun resetNowDate() {
        now = Calendar.getInstance()
        // set starts of day  week in seconds
        now?.set(Calendar.HOUR_OF_DAY, 0)
        now?.set(Calendar.MINUTE, 0)
        now?.set(Calendar.SECOND, 0)
    }

    // load preferences values
    fun loadPreferences() {
        // get preferences object
        val sharedPref = getSharedPreferences(PreferencesActivity.KEY_PREFS, MODE_PRIVATE)

        // get saved value
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
        prefsBeginningWeek = sharedPref.getInt(
            PreferencesActivity.KEY_PREFS_BEGINNING_WEEK,
            PreferencesActivity.BEGINNING_WEEK_DEFAULT
        )
    }

    // get String query
    fun getStrQuery(func: String): String {
        return "SELECT " + func + "(" + DBHelper.KEY_MEASUREMENT + ") FROM " + DBHelper.TABLE_MEASUREMENTS
    }

    // get String query from a given time
    fun getStrQuery(func: String, boundarySugar: Float, lowSugar: Boolean): String {
        val equalitySign: String
        equalitySign = if (lowSugar) " < " else " > "
        return ("SELECT " + func + "(" + DBHelper.KEY_MEASUREMENT + ") FROM " + DBHelper.TABLE_MEASUREMENTS
                + " WHERE " + DBHelper.KEY_MEASUREMENT + equalitySign + boundarySugar)
    }

    // get String query from a given time
    fun getStrQuery(func: String, startTimeInSec: Long): String {
        return ("SELECT " + func + "(" + DBHelper.KEY_MEASUREMENT + ") FROM " + DBHelper.TABLE_MEASUREMENTS
                + " WHERE " + DBHelper.KEY_TIME_IN_SECONDS + " > " + startTimeInSec)
    }

    // get String query from a given time
    fun getStrQuery(
        func: String,
        startTimeInSec: Long,
        boundarySugar: Float,
        lowSugar: Boolean
    ): String {
        val equalitySign: String
        equalitySign = if (lowSugar) " < " else " > "
        return ("SELECT " + func + "(" + DBHelper.KEY_MEASUREMENT + ") FROM " + DBHelper.TABLE_MEASUREMENTS
                + " WHERE " + DBHelper.KEY_TIME_IN_SECONDS + " > " + startTimeInSec
                + " AND " + DBHelper.KEY_MEASUREMENT + equalitySign + boundarySugar)
    }

    // load statistics
    private fun loadStatistics() {
        val database = dbHelper!!.writableDatabase

        // statistics vars ---------------------start
        // all times sugars
        val allTimeCount: Int
        val allTimeCountLow: Int
        val allTimeCountHigh: Int
        var allTimeSugarAvg: Float
        var allTimeSugarMin: Float
        var allTimeSugarMax: Float

        // last weeks sugars
        val curWeekCount: Int
        val curWeekCountLow: Int
        val curWeekCountHigh: Int
        var curWeekSugarAvg: Float
        var curWeekSugarMin: Float
        var curWeekSugarMax: Float

        // last months sugars
        val curMonthCount: Int
        val curMonthCountLow: Int
        val curMonthCountHigh: Int
        var curMonthSugarAvg: Float
        var curMonthSugarMin: Float
        var curMonthSugarMax: Float

        // last weeks sugars
        val lastWeekCount: Int
        val lastWeekCountLow: Int
        val lastWeekCountHigh: Int
        var lastWeekSugarAvg: Float
        var lastWeekSugarMin: Float
        var lastWeekSugarMax: Float

        // last months sugars
        val lastMonthCount: Int
        val lastMonthCountLow: Int
        val lastMonthCountHigh: Int
        var lastMonthSugarAvg: Float
        var lastMonthSugarMin: Float
        var lastMonthSugarMax: Float
        // statistics vars ---------------------end

        // get all records count
        var cursor = database.rawQuery(getStrQuery(KEY_COUNT), null)
        cursor.moveToFirst()
        allTimeCount = cursor.getInt(0)

        // set Info to textViews
        tvAllTimeCount!!.text = allTimeCount.toString()

        // if measurement doesn't exist then don't try get statistics data
        if (allTimeCount > 0) {
            // work with count
            // current week
            cursor = database.rawQuery(getStrQuery(KEY_COUNT, startCurWeekInSec), null)
            cursor.moveToFirst()
            curWeekCount = cursor.getInt(0)
            cursor.close()

            // current month
            cursor = database.rawQuery(getStrQuery(KEY_COUNT, startCurMonthInSec), null)
            cursor.moveToFirst()
            curMonthCount = cursor.getInt(0)
            cursor.close()

            // last week
            cursor = database.rawQuery(getStrQuery(KEY_COUNT, startLastWeekInSec), null)
            cursor.moveToFirst()
            lastWeekCount = cursor.getInt(0)
            cursor.close()

            // last month
            cursor = database.rawQuery(getStrQuery(KEY_COUNT, startLastMonthInSec), null)
            cursor.moveToFirst()
            lastMonthCount = cursor.getInt(0)
            cursor.close()

            // set Info to textViews
            tvCurWeekCount!!.text = curWeekCount.toString()
            tvCurMonthCount!!.text = curMonthCount.toString()
            tvLastWeekCount!!.text = lastWeekCount.toString()
            tvLastMonthCount!!.text = lastMonthCount.toString()

            // work with last week sugars
            if (lastWeekCount > 0) {
                // average
                cursor = database.rawQuery(getStrQuery(KEY_AVG, startLastWeekInSec), null)
                cursor.moveToFirst()
                lastWeekSugarAvg = cursor.getFloat(0)
                cursor.close()

                // minimum
                cursor = database.rawQuery(getStrQuery(KEY_MIN, startLastWeekInSec), null)
                cursor.moveToFirst()
                lastWeekSugarMin = cursor.getFloat(0)
                cursor.close()

                // maximum
                cursor = database.rawQuery(getStrQuery(KEY_MAX, startLastWeekInSec), null)
                cursor.moveToFirst()
                lastWeekSugarMax = cursor.getFloat(0)
                cursor.close()

                // to display measurement in selected in preferences unit of sugar measurement
                if (!prefsUnitBloodSugarMmol) {
                    lastWeekSugarAvg *= 18f
                    lastWeekSugarMin *= 18f
                    lastWeekSugarMax *= 18f
                }

                // set Info to textViews
                tvLastWeekAvg!!.text =
                    String.format(Locale.ENGLISH, sugarFormat!!, lastWeekSugarAvg)
                tvLastWeekMin!!.text =
                    String.format(Locale.ENGLISH, sugarFormat!!, lastWeekSugarMin)
                tvLastWeekMax!!.text =
                    String.format(Locale.ENGLISH, sugarFormat!!, lastWeekSugarMax)
            }

            // work with last month sugars
            if (lastMonthCount > 0) {
                // average
                cursor = database.rawQuery(getStrQuery(KEY_AVG, startLastMonthInSec), null)
                cursor.moveToFirst()
                lastMonthSugarAvg = cursor.getFloat(0)
                cursor.close()

                // minimum
                cursor = database.rawQuery(getStrQuery(KEY_MIN, startLastMonthInSec), null)
                cursor.moveToFirst()
                lastMonthSugarMin = cursor.getFloat(0)
                cursor.close()

                // maximum
                cursor = database.rawQuery(getStrQuery(KEY_MAX, startLastMonthInSec), null)
                cursor.moveToFirst()
                lastMonthSugarMax = cursor.getFloat(0)
                cursor.close()

                // to display measurement in selected in preferences unit of sugar measurement
                if (!prefsUnitBloodSugarMmol) {
                    lastMonthSugarAvg *= 18f
                    lastMonthSugarMin *= 18f
                    lastMonthSugarMax *= 18f
                }

                // set Info to textViews
                tvLastMonthAvg!!.text =
                    String.format(Locale.ENGLISH, sugarFormat!!, lastMonthSugarAvg)
                tvLastMonthMin!!.text =
                    String.format(Locale.ENGLISH, sugarFormat!!, lastMonthSugarMin)
                tvLastMonthMax!!.text =
                    String.format(Locale.ENGLISH, sugarFormat!!, lastMonthSugarMax)
            }

            // current week sugars work
            if (curWeekCount > 0) {
                // average
                cursor = database.rawQuery(getStrQuery(KEY_AVG, startCurMonthInSec), null)
                cursor.moveToFirst()
                curWeekSugarAvg = cursor.getFloat(0)
                cursor.close()

                // minimum
                cursor = database.rawQuery(getStrQuery(KEY_MIN, startCurWeekInSec), null)
                cursor.moveToFirst()
                curWeekSugarMin = cursor.getFloat(0)
                cursor.close()

                // maximum
                cursor = database.rawQuery(getStrQuery(KEY_MAX, startCurWeekInSec), null)
                cursor.moveToFirst()
                curWeekSugarMax = cursor.getFloat(0)
                cursor.close()

                // to display measurement in selected in preferences unit of sugar measurement
                if (!prefsUnitBloodSugarMmol) {
                    curWeekSugarAvg *= 18f
                    curWeekSugarMin *= 18f
                    curWeekSugarMax *= 18f
                }

                // set Info to textViews
                tvCurWeekAvg!!.text =
                    String.format(Locale.ENGLISH, sugarFormat!!, curWeekSugarAvg)
                tvCurWeekMin!!.text = String.format(Locale.ENGLISH, sugarFormat!!, curWeekSugarMin)
                tvCurWeekMax!!.text =
                    String.format(Locale.ENGLISH, sugarFormat!!, curWeekSugarMax)
            }

            //  current month sugars work
            if (curMonthCount > 0) {
                // average
                cursor = database.rawQuery(getStrQuery(KEY_AVG, startCurMonthInSec), null)
                cursor.moveToFirst()
                curMonthSugarAvg = cursor.getFloat(0)
                cursor.close()

                // minimum
                cursor = database.rawQuery(getStrQuery(KEY_MIN, startCurMonthInSec), null)
                cursor.moveToFirst()
                curMonthSugarMin = cursor.getFloat(0)
                cursor.close()

                // maximum
                cursor = database.rawQuery(getStrQuery(KEY_MAX, startCurMonthInSec), null)
                cursor.moveToFirst()
                curMonthSugarMax = cursor.getFloat(0)
                cursor.close()

                // to display measurement in selected in preferences unit of sugar measurement
                if (!prefsUnitBloodSugarMmol) {
                    curMonthSugarAvg *= 18f
                    curMonthSugarMin *= 18f
                    curMonthSugarMax *= 18f
                }

                // set Info to textViews
                tvCurMonthAvg!!.text =
                    String.format(Locale.ENGLISH, sugarFormat!!, curMonthSugarAvg)
                tvCurMonthMin!!.text =
                    String.format(Locale.ENGLISH, sugarFormat!!, curMonthSugarMin)
                tvCurMonthMax!!.text =
                    String.format(Locale.ENGLISH, sugarFormat!!, curMonthSugarMax)
            }

            // all time sugars work
            // average
            cursor = database.rawQuery(getStrQuery(KEY_AVG), null)
            cursor.moveToFirst()
            allTimeSugarAvg = cursor.getFloat(0)
            cursor.close()

            // minimum
            cursor = database.rawQuery(getStrQuery(KEY_MIN), null)
            cursor.moveToFirst()
            allTimeSugarMin = cursor.getFloat(0)
            cursor.close()

            // maximum
            cursor = database.rawQuery(getStrQuery(KEY_MAX), null)
            cursor.moveToFirst()
            allTimeSugarMax = cursor.getFloat(0)
            cursor.close()

            // to display measurement in selected in preferences unit of sugar measurement
            if (!prefsUnitBloodSugarMmol) {
                allTimeSugarAvg *= 18f
                allTimeSugarMin *= 18f
                allTimeSugarMax *= 18f
            }

            // set Info to textViews
            tvAllTimeAvg!!.text =
                String.format(Locale.ENGLISH, sugarFormat!!, allTimeSugarAvg)
            tvAllTimeMin!!.text = String.format(Locale.ENGLISH, sugarFormat!!, allTimeSugarMin)
            tvAllTimeMax!!.text =
                String.format(Locale.ENGLISH, sugarFormat!!, allTimeSugarMax)


            // count of low and high sugars for all time
            cursor =
                database.rawQuery(getStrQuery(KEY_COUNT, prefsBloodLowSugar, KEY_LOW_SUGAR), null)
            cursor.moveToFirst()
            allTimeCountLow = cursor.getInt(0)
            cursor.close()

            cursor =
                database.rawQuery(getStrQuery(KEY_COUNT, prefsBloodHighSugar, KEY_HIGH_SUGAR), null)
            cursor.moveToFirst()
            allTimeCountHigh = cursor.getInt(0)
            cursor.close()

            // set Info to textViews
            tvAllTimeCountLow!!.text = allTimeCountLow.toString()
            tvAllTimeCountHigh!!.text = allTimeCountHigh.toString()

            //  count of low sugars work
            if (allTimeCountLow > 0) {
                // current month low sugars
                cursor = database.rawQuery(
                    getStrQuery(
                        KEY_COUNT,
                        startCurMonthInSec,
                        prefsBloodLowSugar,
                        KEY_LOW_SUGAR
                    ), null
                )
                cursor.moveToFirst()
                curMonthCountLow = cursor.getInt(0)
                cursor.close()

                // last month low sugars
                cursor = database.rawQuery(
                    getStrQuery(
                        KEY_COUNT,
                        startLastMonthInSec,
                        prefsBloodLowSugar,
                        KEY_LOW_SUGAR
                    ), null
                )
                cursor.moveToFirst()
                lastMonthCountLow = cursor.getInt(0)
                cursor.close()

                // current week low sugars
                if (curMonthCountLow > 0) {
                    cursor = database.rawQuery(
                        getStrQuery(
                            KEY_COUNT,
                            startCurWeekInSec,
                            prefsBloodLowSugar,
                            KEY_LOW_SUGAR
                        ), null
                    )
                    cursor.moveToFirst()
                    curWeekCountLow = cursor.getInt(0)
                } else curWeekCountLow = 0

                // last week low sugars
                if (lastMonthCountLow > 0) {
                    cursor = database.rawQuery(
                        getStrQuery(
                            KEY_COUNT,
                            startLastWeekInSec,
                            prefsBloodLowSugar,
                            KEY_LOW_SUGAR
                        ), null
                    )
                    cursor.moveToFirst()
                    lastWeekCountLow = cursor.getInt(0)
                } else lastWeekCountLow = 0

                // set count of low sugars Info to textViews
                tvCurWeekCountLow!!.text = curWeekCountLow.toString()
                tvLastWeekCountLow!!.text = lastWeekCountLow.toString()
            } else {
                curMonthCountLow = 0
                lastMonthCountLow = 0
            }

            //  count of high sugars work
            if (allTimeCountHigh > 0) {
                // current month high sugars
                cursor = database.rawQuery(
                    getStrQuery(
                        KEY_COUNT,
                        startCurMonthInSec,
                        prefsBloodHighSugar,
                        KEY_HIGH_SUGAR
                    ), null
                )
                cursor.moveToFirst()
                curMonthCountHigh = cursor.getInt(0)
                cursor.close()

                // last month high sugars
                cursor = database.rawQuery(
                    getStrQuery(
                        KEY_COUNT,
                        startLastMonthInSec,
                        prefsBloodHighSugar,
                        KEY_HIGH_SUGAR
                    ), null
                )
                cursor.moveToFirst()
                lastMonthCountHigh = cursor.getInt(0)
                cursor.close()

                // current week high sugars
                if (curMonthCountHigh > 0) {
                    cursor = database.rawQuery(
                        getStrQuery(
                            KEY_COUNT,
                            startCurWeekInSec,
                            prefsBloodHighSugar,
                            KEY_HIGH_SUGAR
                        ), null
                    )
                    cursor.moveToFirst()
                    curWeekCountHigh = cursor.getInt(0)
                    cursor.close()
                } else curWeekCountHigh = 0

                // last week high sugars
                if (lastMonthCountHigh > 0) {
                    cursor = database.rawQuery(
                        getStrQuery(
                            KEY_COUNT,
                            startLastWeekInSec,
                            prefsBloodHighSugar,
                            KEY_HIGH_SUGAR
                        ), null
                    )
                    cursor.moveToFirst()
                    lastWeekCountHigh = cursor.getInt(0)
                    cursor.close()
                } else lastWeekCountHigh = 0

                // set count of high sugars Info to textViews
                tvCurWeekCountHigh!!.text = curWeekCountHigh.toString()
                tvLastWeekCountHigh!!.text = lastWeekCountHigh.toString()
            } else {
                curMonthCountHigh = 0
                lastMonthCountHigh = 0
            }

            // set count of low and high sugars Info to textViews
            tvCurMonthCountLow!!.text = curMonthCountLow.toString()
            tvLastMonthCountLow!!.text = lastMonthCountLow.toString()
            tvCurMonthCountHigh!!.text = curMonthCountHigh.toString()
            tvLastMonthCountHigh!!.text = lastMonthCountHigh.toString()
        }
        cursor.close()
        database.close()
    }

    // initialize views on screen and their listening
    fun initViews() {
        // find views on screen by id
        tvAllTimeCount = findViewById(R.id.tvCountAllTime)
        tvAllTimeCountLow = findViewById(R.id.tvLastAllTimeCountLow)
        tvAllTimeCountHigh = findViewById(R.id.tvLastAllTimeCountHigh)
        tvAllTimeAvg = findViewById(R.id.tvAllTimeAvg)
        tvAllTimeMin = findViewById(R.id.tvAllTimeMin)
        tvAllTimeMax = findViewById(R.id.tvAllTimeMax)
        tvCurWeekCount = findViewById(R.id.tvCurWeekCount)
        tvCurWeekCountLow = findViewById(R.id.tvCurWeekCountLow)
        tvCurWeekCountHigh = findViewById(R.id.tvCurWeekCountHigh)
        tvCurWeekAvg = findViewById(R.id.tvCurWeekAvg)
        tvCurWeekMin = findViewById(R.id.tvCurWeekMin)
        tvCurWeekMax = findViewById(R.id.tvCurWeekMax)
        tvCurMonthCount = findViewById(R.id.tvCountCurMonth)
        tvCurMonthCountLow = findViewById(R.id.tvCurMonthCountLow)
        tvCurMonthCountHigh = findViewById(R.id.tvCurMonthCountHigh)
        tvCurMonthAvg = findViewById(R.id.tvCurMonthAvg)
        tvCurMonthMin = findViewById(R.id.tvCurMonthMin)
        tvCurMonthMax = findViewById(R.id.tvCurMonthMax)
        tvLastWeekCount = findViewById(R.id.tvCountLastWeek)
        tvLastWeekCountLow = findViewById(R.id.tvLastWeekCountLow)
        tvLastWeekCountHigh = findViewById(R.id.tvLastWeekCountHigh)
        tvLastWeekAvg = findViewById(R.id.tvLastWeekAvg)
        tvLastWeekMin = findViewById(R.id.tvLastWeekMin)
        tvLastWeekMax = findViewById(R.id.tvLastWeekMax)
        tvLastMonthCount = findViewById(R.id.tvCountLastMonth)
        tvLastMonthCountLow = findViewById(R.id.tvLastMonthCountLow)
        tvLastMonthCountHigh = findViewById(R.id.tvLastMonthCountHigh)
        tvLastMonthAvg = findViewById(R.id.tvLastMonthAvg)
        tvLastMonthMin = findViewById(R.id.tvLastMonthMin)
        tvLastMonthMax = findViewById(R.id.tvLastMonthMax)
    }

    companion object {
        private const val TAG = "myLog"

        // select query vars
        private const val KEY_COUNT = "COUNT"
        private const val KEY_AVG = "AVG"
        private const val KEY_MIN = "MIN"
        private const val KEY_MAX = "MAX"
        private const val KEY_LOW_SUGAR = true
        private const val KEY_HIGH_SUGAR = false
    }
}