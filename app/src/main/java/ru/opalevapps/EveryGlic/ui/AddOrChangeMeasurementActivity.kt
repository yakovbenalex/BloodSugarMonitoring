package ru.opalevapps.EveryGlic.ui

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.ContentValues
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ru.opalevapps.EveryGlic.MyUtill
import ru.opalevapps.EveryGlic.R
import ru.opalevapps.EveryGlic.db.DBHelper
import java.text.SimpleDateFormat
import java.util.*

class AddOrChangeMeasurementActivity : AppCompatActivity(), View.OnClickListener {
    // for date and time
    private var dateAndTime = Calendar.getInstance()
    private lateinit var now: Calendar
    private lateinit var simpleDateFormat: SimpleDateFormat
    private var DATE_FORMAT = "dd/MM/yy - HH:mm"

    // id record
    private var idRec = 0

    // choose add or update measurement
    private var updateRec = false

    // prefs vars
    private var prefsTimeFormat24h = false
    private var prefsUnitBloodSugarMmol = false

    // views
    private lateinit var btnChooseDate: Button
    private lateinit var btnChooseTime: Button
    private lateinit var btnSaveMeasurement: Button
    private lateinit var btnDeleteCurMeasurements: Button

    // TEST ---------------
    private lateinit var btnAddTestData: Button
    private lateinit var etBloodSugarMeasurement: EditText
    private lateinit var etComment: EditText
    private lateinit var tvDateAndTime: TextView
    private lateinit var tvUnitBloodSugar: TextView

    // SQLite database
    private lateinit var dbHelper: DBHelper

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_measurement)

        dbHelper = DBHelper(this)

        // get id record from Activity intent
        idRec = intent.getLongExtra("idRec", -1).toInt()

        // get shared preferences object
        val sharedPref = getSharedPreferences(PreferencesActivity.KEY_PREFS, MODE_PRIVATE)
        prefsTimeFormat24h =
            sharedPref.getBoolean(PreferencesActivity.KEY_PREFS_TIME_FORMAT_24H, true)
        prefsUnitBloodSugarMmol = sharedPref.getBoolean(
            PreferencesActivity.KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL,
            PreferencesActivity.UNIT_BLOOD_SUGAR_MMOL_DEFAULT
        )

        // date and time format display
        DATE_FORMAT =
            if (prefsTimeFormat24h) "dd/MM/yy - " + "HH:mm"
            else "dd/MM/yy - " + "h:mm a"
        simpleDateFormat = SimpleDateFormat(DATE_FORMAT)

        initViews()

        // if editing measurement
        if (idRec != -1) {
            // enable delete button
            btnDeleteCurMeasurements.isEnabled = true
            title = getString(R.string.edit_measurement)

            // load measurement
            loadRecords()
        } else {
            etBloodSugarMeasurement.requestFocus()
        }

        // display date and time
        setCaptionDateTime()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        MyUtill.createInfoItemInActionBar(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MyUtill.parseMenuItemInfo(this, item)
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnAddTestData -> {
                val testDataCount = 50

                // choose update or add measurement
                updateRec = false
                var i = 0
                while (i < testDataCount) {

                    // check date to more than current and reset if so
                    now = Calendar.getInstance()
                    val leftLimit = 3.5f
                    val rightLimit = 15.5f
                    var newMeasure =
                        leftLimit + (Math.random() * (rightLimit - leftLimit)).toFloat()
                    val newMeasureStr = newMeasure.toString()
                    newMeasure =
                        newMeasureStr.substring(0, newMeasureStr.indexOf(".") + 1).toFloat()
                    val dateLeftLimit = 1L
                    val dateRightLimit = 2L * 30 * 24 * 60 * 60
                    val dateOffset =
                        dateLeftLimit + (Math.random() * (dateRightLimit - dateLeftLimit)).toLong()
                    val date = dateAndTime.timeInMillis / 1000 - dateOffset
                    val comment = "Test data $i - $newMeasure"

                    // save measurement or update if updateRec is true
                    writeRecord(newMeasure, date, comment, updateRec)
                    i++
                }
                Toast.makeText(this, "$testDataCount test data added", Toast.LENGTH_SHORT).show()
            }
            R.id.btnChooseDate -> DatePickerDialog(
                this, datePickerDialog,
                dateAndTime[Calendar.YEAR],
                dateAndTime[Calendar.MONTH],
                dateAndTime[Calendar.DAY_OF_MONTH]
            )
                .show()
            R.id.btnChooseTime -> TimePickerDialog(
                this, timePickerDialog,
                dateAndTime[Calendar.HOUR_OF_DAY],
                dateAndTime[Calendar.MINUTE],
                prefsTimeFormat24h
            )
                .show()
            R.id.btnDeleteCurMeasurements -> {
                // Alert dialog to confirm delete all measurements
                val alertDelCurMes = AlertDialog.Builder(this)
                alertDelCurMes.setTitle(getString(R.string.delete_current_measurements))
                alertDelCurMes.setMessage(getString(R.string.these_changes_can_t_return))
                alertDelCurMes.setNegativeButton(getString(R.string.no)) { dialog: DialogInterface?, whichButton: Int -> }

                // confirm delete current measurement
                alertDelCurMes.setPositiveButton(getString(R.string.yes)) { dialog: DialogInterface?, whichButton: Int ->
                    // delete current measurement from database
                    val database = dbHelper.writableDatabase
                    database.delete(
                        DBHelper.TABLE_MEASUREMENTS,
                        DBHelper.KEY_ID + " = " + idRec, null
                    )
                    deleteRecord(idRec)
                    Toast.makeText(
                        this,
                        getString(R.string.the_current_measurement_has_been_deleted),
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
                alertDelCurMes.show()
            }
            R.id.btnSaveMeasurement -> {
                // choose update or add measurement
                updateRec = idRec != -1

                // check date to more than current and reset if so
                now = Calendar.getInstance()
                if (dateAndTime.timeInMillis > now.timeInMillis) {
                    Toast.makeText(
                        this@AddOrChangeMeasurementActivity, """
     ${getString(R.string.incorrect_date)}
     ${getString(R.string.date_cannot_be_greater_than_the_current)}
     """.trimIndent(), Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (isCorrectInputValues) {
                        val measurement = etBloodSugarMeasurement.text.toString().toFloat()
                        val date = dateAndTime.timeInMillis / 1000
                        val comment = etComment.text.toString()

                        // save measurement or update if updateRec is true
                        writeRecord(measurement, date, comment, updateRec)
                        Toast.makeText(
                            this,
                            getString(R.string.measurement_has_been_saved),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
            else -> {}
        }
    }

    // set date to caption
    private fun setCaptionDateTime() {
        tvDateAndTime.text = simpleDateFormat.format(dateAndTime.timeInMillis)
    }

    // listener for DatePickerDialog
    private var datePickerDialog = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        dateAndTime[year, monthOfYear] = dayOfMonth

        // checking on current date and reset date to current
        now = Calendar.getInstance()
        if (dateAndTime.timeInMillis > now.timeInMillis) {
            Toast.makeText(
                this@AddOrChangeMeasurementActivity, """
     ${getString(R.string.incorrect_date)}
     ${getString(R.string.date_cannot_be_greater_than_the_current)}
     ${getString(R.string.date_has_been_reset)}
     """.trimIndent(), Toast.LENGTH_SHORT
            ).show()

            // set current date
            dateAndTime[now.get(Calendar.YEAR), now.get(Calendar.MONTH)] =
                now.get(Calendar.DAY_OF_MONTH)
        } else {
            if (year < yearLimitLowerBound) {
                Toast.makeText(
                    this@AddOrChangeMeasurementActivity, """
     ${getString(R.string.incorrect_date)}
     ${String.format(getString(R.string.date_cannot_be_less_than__year), yearLimitLowerBound)}
     ${getString(R.string.date_has_been_reset)}
     """.trimIndent(), Toast.LENGTH_SHORT
                ).show()

                // set current date
                dateAndTime[now.get(Calendar.YEAR), now.get(Calendar.MONTH)] =
                    now.get(Calendar.DAY_OF_MONTH)
            }
        }
        setCaptionDateTime()
    }

    // listener for TimePickerDialog
    private var timePickerDialog = OnTimeSetListener { view, hourOfDay, minute ->
        now = Calendar.getInstance()
        dateAndTime[dateAndTime[Calendar.YEAR], dateAndTime[Calendar.MONTH], dateAndTime[Calendar.DAY_OF_MONTH], hourOfDay] =
            minute

        // checking on current date and reset date to current
        now = Calendar.getInstance()
        if (dateAndTime.timeInMillis > now.getTimeInMillis()) {
            Toast.makeText(
                this@AddOrChangeMeasurementActivity, """
     ${getString(R.string.incorrect_date)}
     ${getString(R.string.date_cannot_be_greater_than_the_current)}
     ${getString(R.string.date_has_been_reset)}
     """.trimIndent(), Toast.LENGTH_SHORT
            ).show()

            // set current date
            dateAndTime[now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), now.get(
                Calendar.HOUR_OF_DAY
            )] =
                now.get(Calendar.MINUTE)
        }
        setCaptionDateTime()
    }

    //writing measurement record
    private fun writeRecord(measurement: Float, date: Long, comment: String?, updateRec: Boolean) {
        val database = dbHelper.writableDatabase
        val contentValues = ContentValues()
        if (prefsUnitBloodSugarMmol) {
            contentValues.put(DBHelper.KEY_MEASUREMENT, measurement)
        } else {
            val tmpBloodLowSugar = MyUtill.roundUp(
                etBloodSugarMeasurement.text.toString().toFloat() / 18, 1
            ).toFloat()
            contentValues.put(DBHelper.KEY_MEASUREMENT, tmpBloodLowSugar)
        }
        contentValues.put(DBHelper.KEY_TIME_IN_SECONDS, date)
        contentValues.put(DBHelper.KEY_COMMENT, comment)
        if (updateRec) {
            database.update(
                DBHelper.TABLE_MEASUREMENTS, contentValues,
                DBHelper.KEY_ID + " = " + idRec, null
            )
        } else {
            database.insert(DBHelper.TABLE_MEASUREMENTS, null, contentValues)
        }
        database.close()
    }

    // delete measurement record by id
    private fun deleteRecord(idRec: Int) {
        val database = dbHelper.writableDatabase
        database.delete(
            DBHelper.TABLE_MEASUREMENTS, DBHelper.KEY_ID + " = " + idRec, null
        )
        database.close()
    }

    private fun loadRecords() {
        val database = dbHelper.writableDatabase
        val timeInMillis: Long
        val cursor = database.query(
            DBHelper.TABLE_MEASUREMENTS, null,
            DBHelper.KEY_ID + " = " + idRec, null, null, null, null
        )
        cursor.moveToFirst()

        // db columns indexes
        val idMeasurement = cursor.getColumnIndex(DBHelper.KEY_MEASUREMENT)
        val idTimeInSeconds = cursor.getColumnIndex(DBHelper.KEY_TIME_IN_SECONDS)
        val idComment = cursor.getColumnIndex(DBHelper.KEY_COMMENT)

        // filling fields
        if (prefsUnitBloodSugarMmol) etBloodSugarMeasurement.setText(
            cursor.getFloat(idMeasurement).toString()
        ) else etBloodSugarMeasurement.setText(
            ((cursor.getFloat(idMeasurement) * 18).toInt()).toString()
        )
        etComment.setText(cursor.getString(idComment))
        timeInMillis = getMillisInSeconds(cursor.getLong(idTimeInSeconds))

        //  Calendar time picker set
        dateAndTime.timeInMillis = timeInMillis
        /*if (prefsTimeFormat24h) {
                hour = dateAndTime.get(Calendar.HOUR_OF_DAY);
            } else {
                hour = dateAndTime.get(Calendar.HOUR);
            }*/
        /*try {
            //time picker set
            timePicker.setCurrentHour(dateAndTime.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(dateAndTime.get(Calendar.MINUTE));
        } catch (Exception e) {
            Log.d(TAG, "loadRecords: setCurrent");
        }*/
        cursor.close()
        database.close()
    }

    // set hints for editText
    private fun setEditTextsHints(prefsUnitBloodSugarMmol: Boolean) {
        if (prefsUnitBloodSugarMmol) {
            etBloodSugarMeasurement.hint = String.format(
                Locale.ENGLISH, getString(R.string.from_toFloat),
                bloodSugarLimitLow, bloodSugarLimitHigh
            )
        } else {
            etBloodSugarMeasurement.hint = String.format(
                getString(R.string.from_toDecimal),
                (bloodSugarLimitLow * 18).toInt(), (bloodSugarLimitHigh * 18).toInt()
            )
        }
    }// is empty

    // check on range input value and set focus on them
    // check fields for correctness
    private val isCorrectInputValues: Boolean
        get() {
            // is empty
            if (MyUtill.requiredFiledEmpty(etBloodSugarMeasurement)) {
                Toast.makeText(
                    this,
                    getString(R.string.sugar_measurement_field_must_be_filled),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }

            // check on range input value and set focus on them
            if (prefsUnitBloodSugarMmol) {
                if (!MyUtill.numberInRange(
                        etBloodSugarMeasurement.text.toString().toFloat(),
                        bloodSugarLimitLow, bloodSugarLimitHigh
                    )
                ) {
                    etBloodSugarMeasurement.requestFocus()
                    MyUtill.clearET(etBloodSugarMeasurement)
                    Toast.makeText(this, getString(R.string.incorrect_value), Toast.LENGTH_SHORT)
                        .show()
                    return false
                }
            } else {
                if (!MyUtill.numberInRange(
                        etBloodSugarMeasurement.text.toString().toFloat(),
                        (bloodSugarLimitLow * 18),
                        (bloodSugarLimitHigh * 18)
                    )
                ) {
                    etBloodSugarMeasurement.requestFocus()
                    MyUtill.clearET(etBloodSugarMeasurement)
                    Toast.makeText(this, getString(R.string.incorrect_value), Toast.LENGTH_SHORT)
                        .show()
                    return false
                }
            }
            return true
        }

    // get time in millis
    private fun getMillisInSeconds(timeInSeconds: Long): Long {
        return timeInSeconds * 1000
    }

    // initialize views on screen and their listening
    private fun initViews() {
        // find views on screen by id
        btnChooseDate = findViewById(R.id.btnChooseDate)
        btnChooseTime = findViewById(R.id.btnChooseTime)
        btnSaveMeasurement = findViewById(R.id.btnSaveMeasurement)
        btnDeleteCurMeasurements = findViewById(R.id.btnDeleteCurMeasurements)

        // TEST -------
        btnAddTestData = findViewById(R.id.btnAddTestData)
        etBloodSugarMeasurement = findViewById(R.id.etBloodSugarMeasurementEdit)
        etComment = findViewById(R.id.etCommentEdit)
        tvDateAndTime = findViewById(R.id.tvDateAndTime)
        tvUnitBloodSugar = findViewById(R.id.tvUnitBloodSugar)


        // set hint for editText
        setEditTextsHints(prefsUnitBloodSugarMmol)

        // set views properties and other
//        timePicker.setIs24HourView(true);
        if (prefsUnitBloodSugarMmol) {
            tvUnitBloodSugar.setText(R.string.mmol_l)
        } else {
            tvUnitBloodSugar.setText(R.string.mg_dl)
        }

        // set the listeners for views
        btnChooseDate.setOnClickListener(this)
        btnChooseTime.setOnClickListener(this)
        btnSaveMeasurement.setOnClickListener(this)
        btnDeleteCurMeasurements.setOnClickListener(this)

        // TEST -------
        btnAddTestData.setOnClickListener(this)

        // event on text change in editTexts
        etBloodSugarMeasurement.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                var s = charSequence.toString()

                // set one point accuracy for editText value
                if (s.contains(".")) {
                    s = MyUtill.getStringNumberWithAccuracy(s, 1, '.', false)
                    if (s.length != charSequence.toString().length) {
                        etBloodSugarMeasurement.setText(s)
                        etBloodSugarMeasurement.setSelection(s.length)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    companion object {
        // limit to back starts on 1970 (this is enough)
        private const val yearLimitLowerBound = 1970

        // Sugar range limit
        private const val bloodSugarLimitLow = 0.3f
        private const val bloodSugarLimitHigh = 55.5f
    }
}