package ru.opalevapps.EveryGlic.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ru.opalevapps.EveryGlic.MyUtill
import ru.opalevapps.EveryGlic.R
import ru.opalevapps.EveryGlic.db.DBHelper
import ru.opalevapps.EveryGlic.ui.AgreementActivity
import java.util.*

class PreferencesActivity : AppCompatActivity(), View.OnClickListener {
    // variables for preferences
    private var prefsBloodLowSugar = 0f
    private var prefsBloodHighSugar = 0f
    private var prefsAmountCarbsInBreadUnit = 0f
    private var prefsDiabetes1Type = false
    private var prefsUnitBloodSugarMmol = false
    private var prefsTimeFormat24h = false
    private var prefsBeginningWeek = 0
    private var firstRun = false
    private var isChangeSetting = false
    private var tmpBloodLowSugar = 0f
    private var tmpBloodHighSugar = 0f

    // views declare
    private lateinit var btnSavePreferences: Button
    private lateinit var btnResetToDefault: Button
    private lateinit var btnDeleteAllMeasurements: Button
    private lateinit var btnBackupDB: Button
    private lateinit var btnRestoreDB: Button
    private lateinit var rbDiabetesType1: RadioButton
    private lateinit var rbDiabetesType2: RadioButton
    private lateinit var rbUnitOfBloodSugarMmolL: RadioButton
    private lateinit var rbUnitOfBloodSugarMgdL: RadioButton
    private lateinit var rbTimeFormat12h: RadioButton
    private lateinit var rbTimeFormat24h: RadioButton
    private lateinit var rbBeginningWeekSat: RadioButton
    private lateinit var rbBeginningWeekSun: RadioButton
    private lateinit var rbBeginningWeekMon: RadioButton
    private lateinit var rgDiabetesType: RadioGroup
    private lateinit var rgUnitOfBloodSugar: RadioGroup
    private lateinit var rgTimeFormat: RadioGroup
    private lateinit var rgBeginningWeek: RadioGroup
    private lateinit var etBloodLowSugar: EditText
    private lateinit var etBloodHighSugar: EditText
    private lateinit var etAmountCarb: EditText
    private lateinit var vBtnDelAllMeasTop: View
    private lateinit var vBtnDelAllMeasBottom: View

    // SQLite database
    private lateinit var dbHelper: DBHelper

    //-------------------------------------DECLARE BLOCK END--------------------------------------//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        // create database object
        dbHelper = DBHelper(this)

        // initializing starts variables
        isChangeSetting = false

        // get preferences values
        loadPreferences()
        initViews()

        // set preferences values
        etAmountCarb.setText(prefsAmountCarbsInBreadUnit.toString())
        if (prefsDiabetes1Type) rbDiabetesType1.isChecked =
            true else rbDiabetesType2.isChecked = true
        if (prefsUnitBloodSugarMmol) {
            // set editTexts input type to decimal number
            etBloodLowSugar.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            etBloodHighSugar.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            rbUnitOfBloodSugarMmolL.isChecked = true
            etBloodLowSugar.setText(prefsBloodLowSugar.toString())
            etBloodHighSugar.setText(prefsBloodHighSugar.toString())
        } else {
            // set editTexts input type to integer number
            etBloodLowSugar.inputType = InputType.TYPE_CLASS_NUMBER
            etBloodHighSugar.inputType = InputType.TYPE_CLASS_NUMBER
            rbUnitOfBloodSugarMgdL.isChecked = true
            etBloodLowSugar.setText(((prefsBloodLowSugar * 18).toInt()).toString())
            etBloodHighSugar.setText(((prefsBloodHighSugar * 18).toInt()).toString())
        }
        if (prefsTimeFormat24h) rbTimeFormat24h.isChecked =
            true else rbTimeFormat12h.isChecked = true
        when (prefsBeginningWeek) {
            2 -> rbBeginningWeekMon.isChecked = true
            1 -> rbBeginningWeekSun.isChecked = true
            0 -> rbBeginningWeekSat.isChecked = true
            else -> {}
        }


        // get firstRunAgreement value
        val sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE)
        firstRun = sharedPref.getBoolean(KEY_PREFS_FIRST_RUN_AGREEMENT, true)

        // check for first run app
        if (firstRun) {
            /*// hide button delete all measurements // perhaps it don't need
            btnDeleteAllMeasurements.setVisibility(View.INVISIBLE);
            vBtnDelAllMeasTop.setVisibility(View.INVISIBLE);
            vBtnDelAllMeasBottom.setVisibility(View.INVISIBLE);*/
            preferencesChanged(true, true)

            //start Agreement Activity
            val intent = Intent(this@PreferencesActivity, AgreementActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        MyUtill.createInfoItemInActionBar(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        MyUtill.parseMenuItemInfo(this, item)
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnDeleteAllMeasurements -> {
                val database = dbHelper.writableDatabase
                // get all records count
                val cursor = database.rawQuery(
                    "SELECT COUNT (" + DBHelper.KEY_MEASUREMENT + ") FROM " + DBHelper.TABLE_MEASUREMENTS,
                    null
                )
                cursor.moveToFirst()
                val allMeasurementsCount = cursor.getInt(0)
                cursor.close()

                if (allMeasurementsCount > 0) {
                    // Alert dialog to confirm delete all measurements
                    val alert = AlertDialog.Builder(this)
                    alert.setTitle(getString(R.string.delete_all_measurements))
                    alert.setMessage(getString(R.string.these_changes_can_t_return))
                    alert.setNegativeButton(
                        getString(R.string.no)
                    ) { dialog: DialogInterface?, whichButton: Int -> }
                    alert.setPositiveButton(getString(R.string.yes)) { dialog: DialogInterface?, whichButton: Int ->
                        val database1 = dbHelper.writableDatabase
                        database1.delete(DBHelper.TABLE_MEASUREMENTS, null, null)
                        database1.close()
                        Toast.makeText(
                            this@PreferencesActivity, getString(
                                R.string.all_measurements_has_been_deleted
                            ),
                            Toast.LENGTH_LONG
                        ).show()
                        finish() //exit from preferences
                    }
                    alert.show()
                } else {
                    // no measurements
                    Toast.makeText(this, R.string.no_measurements, Toast.LENGTH_LONG).show()
                }
            }
            R.id.btnResetToDefault -> resetPreferencesToDefault()
            R.id.btnSavePreferences -> if (isCorrectInputValues) {
                savePreferences()
                Toast.makeText(this, R.string.settings_have_been_saved, Toast.LENGTH_SHORT).show()
                finish()
            }
            R.id.btnBackupDB, R.id.btnRestoreDB -> Toast.makeText(
                this,
                R.string.feature_option,
                Toast.LENGTH_SHORT
            ).show()
            else -> {}
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onBackPressed() {
        backToMenu(
            isChangeSetting, """
     ${getString(R.string.please_press_again_to_back_to_menu)}
     ${getString(R.string.changes_will_not_saved)}
     """.trimIndent()
        )
    }

    // back to menu on double press to back button in a short time, else show specified message
    private fun backToMenu(checked: Boolean, message: String?) {
        if (checked) {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed()
                finish()
            } else {
                Toast.makeText(baseContext, message, Toast.LENGTH_LONG).show()
            }
            back_pressed = System.currentTimeMillis()
        } else {
            super.onBackPressed()
            finish()
        }
    }

    private fun loadPreferences() {
        // get settings object
        val sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE)

        // get saved value for diabetes
        prefsDiabetes1Type =
            sharedPref.getBoolean(KEY_PREFS_DIABETES_1TYPE, DIABETES_1_TYPE_DEFAULT)
        prefsUnitBloodSugarMmol = sharedPref.getBoolean(
            KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL,
            UNIT_BLOOD_SUGAR_MMOL_DEFAULT
        )
        prefsBloodLowSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_LOW_SUGAR, BLOOD_LOW_SUGAR_DEFAULT)
        prefsBloodHighSugar =
            sharedPref.getFloat(KEY_PREFS_BLOOD_HIGH_SUGAR, BLOOD_HIGH_SUGAR_DEFAULT)
        prefsAmountCarbsInBreadUnit = sharedPref.getFloat(
            KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT,
            AMOUNT_CARBS_IN_BREAD_UNIT_DEFAULT
        )

        // get saved value for function of interface
        prefsTimeFormat24h =
            sharedPref.getBoolean(KEY_PREFS_TIME_FORMAT_24H, TIME_FORMAT_24H_DEFAULT)
        prefsBeginningWeek = sharedPref.getInt(KEY_PREFS_BEGINNING_WEEK, BEGINNING_WEEK_DEFAULT)
    }

    // save preferences in sharedPreferences
    private fun savePreferences() {
        // get settings object
        val sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE)
        val prefEditor = sharedPref.edit()

        // put preferences values
        prefEditor.putBoolean(KEY_PREFS_DIABETES_1TYPE, rbDiabetesType1.isChecked)
        if (rbUnitOfBloodSugarMmolL.isChecked) {
            prefEditor.putBoolean(KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL, true)
            prefEditor.putFloat(
                KEY_PREFS_BLOOD_LOW_SUGAR,
                etBloodLowSugar.text.toString().toFloat()
            )
            prefEditor.putFloat(
                KEY_PREFS_BLOOD_HIGH_SUGAR,
                etBloodHighSugar.text.toString().toFloat()
            )
        } else {
            tmpBloodLowSugar =
                MyUtill.roundUp(etBloodLowSugar.text.toString().toFloat() / 18, 1).toFloat()
            tmpBloodHighSugar =
                MyUtill.roundUp(etBloodHighSugar.text.toString().toFloat() / 18, 1).toFloat()
            prefEditor.putBoolean(KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL, false)
            prefEditor.putFloat(KEY_PREFS_BLOOD_LOW_SUGAR, tmpBloodLowSugar)
            prefEditor.putFloat(KEY_PREFS_BLOOD_HIGH_SUGAR, tmpBloodHighSugar)
        }
        prefEditor.putFloat(
            KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT,
            etAmountCarb.text.toString().toFloat()
        )
        prefEditor.putBoolean(KEY_PREFS_TIME_FORMAT_24H, rbTimeFormat24h.isChecked)
        when (rgBeginningWeek.checkedRadioButtonId) {
            R.id.rbBeginningWeekSat -> prefEditor.putInt(
                KEY_PREFS_BEGINNING_WEEK, 0
            )
            R.id.rbBeginningWeekSun -> prefEditor.putInt(KEY_PREFS_BEGINNING_WEEK, 1)
            R.id.rbBeginningWeekMon -> prefEditor.putInt(KEY_PREFS_BEGINNING_WEEK, 2)
            else -> {}
        }

        // save change of preferences
        prefEditor.apply()

        // set flag of changing settings to false and disabled button savePreferences
        preferencesChanged(false, false)
    }

    // reset preferences to default but not save
    private fun resetPreferencesToDefault() {
        // set default preferences for views
        etAmountCarb.setText(AMOUNT_CARBS_IN_BREAD_UNIT_DEFAULT.toString())
        if (DIABETES_1_TYPE_DEFAULT) rbDiabetesType1.isChecked =
            true else rbDiabetesType2.isChecked = true
        if (rbUnitOfBloodSugarMmolL.isChecked) {
            etBloodLowSugar.setText("$BLOOD_LOW_SUGAR_DEFAULT")
            etBloodHighSugar.setText("$BLOOD_HIGH_SUGAR_DEFAULT")
        } else {
            etBloodLowSugar.setText("${(BLOOD_LOW_SUGAR_DEFAULT * 18).toInt()}")
            etBloodHighSugar.setText("${(BLOOD_HIGH_SUGAR_DEFAULT * 18).toInt()}")
        }
        if (TIME_FORMAT_24H_DEFAULT) rbTimeFormat24h.isChecked =
            true else rbTimeFormat12h.isChecked = true
        when (BEGINNING_WEEK_DEFAULT) {
            2 -> rbBeginningWeekMon.isChecked = true
            1 -> rbBeginningWeekSun.isChecked = true
            0 -> rbBeginningWeekSat.isChecked = true
            else -> {}
        }

        // set flag of changing settings to true and enabled button savePreferences
        preferencesChanged(true, true)
    }

    // changing flag of changed preferences
    fun preferencesChanged(changedSetting: Boolean, setEnabledBtnSavePreferences: Boolean) {
        // set flag of changing settings
        isChangeSetting = changedSetting
        // set enabled button save preferences
        btnSavePreferences.isEnabled = setEnabledBtnSavePreferences
    }

    // set hints for editTexts
    private fun setEditTextsHints(unitBloodSugarMmol: Boolean) {
        if (unitBloodSugarMmol) {
            etBloodLowSugar.hint = String.format(
                Locale.ENGLISH, getString(R.string.from_toFloat),
                BLOOD_LOW_SUGAR_LOWER_BOUND, BLOOD_LOW_SUGAR_UPPER_BOUND
            )
            etBloodHighSugar.hint = String.format(
                Locale.ENGLISH, getString(R.string.from_toFloat),
                BLOOD_HIGH_SUGAR_LOWER_BOUND, BLOOD_HIGH_SUGAR_UPPER_BOUND
            )
        } else {
            etBloodLowSugar.hint = String.format(
                getString(R.string.from_toDecimal),
                (BLOOD_LOW_SUGAR_LOWER_BOUND * 18).toInt(),
                (BLOOD_LOW_SUGAR_UPPER_BOUND * 18).toInt()
            )
            etBloodHighSugar.hint = String.format(
                getString(R.string.from_toDecimal),
                (BLOOD_HIGH_SUGAR_LOWER_BOUND * 18).toInt(),
                (BLOOD_HIGH_SUGAR_UPPER_BOUND * 18).toInt()
            )
        }
    }

    // set focus and additional option: show message, clear editText if needs
    private fun setFocusWithMessage(
        et: EditText,
        clearEditText: Boolean,
        showMessage: Boolean,
        message: String?
    ) {
        et.requestFocus()
        if (clearEditText) {
            et.setText("")
        }
        if (showMessage) Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // request focus for empty required fields and return true if so
    private fun requiredFiledEmpty(et: EditText): Boolean {
        if (MyUtill.isEmpty(et)) {
            Toast.makeText(
                this,
                getString(R.string.all_required_fields_must_be_filled),
                Toast.LENGTH_SHORT
            ).show()
            et.requestFocus()
            return true
        }
        return false
    }// is empty

    // check on range input value and set focus on them
    // check fields for correctness
    private val isCorrectInputValues: Boolean
        get() {
            // is empty
            if (requiredFiledEmpty(etBloodLowSugar)) return false
            if (requiredFiledEmpty(etBloodHighSugar)) return false
            if (requiredFiledEmpty(etAmountCarb)) return false

            // check on range input value and set focus on them
            if (rbUnitOfBloodSugarMmolL.isChecked) {
                if (!MyUtill.numberInRange(
                        etBloodLowSugar.text.toString().toFloat(),
                        BLOOD_LOW_SUGAR_LOWER_BOUND, BLOOD_LOW_SUGAR_UPPER_BOUND
                    )
                ) {
                    setFocusWithMessage(
                        etBloodLowSugar,
                        true,
                        true,
                        getString(R.string.incorrect_value)
                    )
                    return false
                }
                if (!MyUtill.numberInRange(
                        etBloodHighSugar.text.toString().toFloat(),
                        BLOOD_HIGH_SUGAR_LOWER_BOUND, BLOOD_HIGH_SUGAR_UPPER_BOUND
                    )
                ) {
                    setFocusWithMessage(
                        etBloodHighSugar,
                        true,
                        true,
                        getString(R.string.incorrect_value)
                    )
                    return false
                }
            } else {
                if (!MyUtill.numberInRange(
                        etBloodLowSugar.text.toString().toFloat(),
                        BLOOD_LOW_SUGAR_LOWER_BOUND * 18,
                        BLOOD_LOW_SUGAR_UPPER_BOUND * 18
                    )
                ) {
                    setFocusWithMessage(
                        etBloodLowSugar,
                        true,
                        true,
                        getString(R.string.incorrect_value)
                    )
                    return false
                }
                if (!MyUtill.numberInRange(
                        etBloodHighSugar.text.toString().toFloat(),
                        BLOOD_HIGH_SUGAR_LOWER_BOUND * 18,
                        BLOOD_HIGH_SUGAR_UPPER_BOUND * 18
                    )
                ) {
                    setFocusWithMessage(
                        etBloodHighSugar,
                        true,
                        true,
                        getString(R.string.incorrect_value)
                    )
                    return false
                }
            }
            if (!MyUtill.numberInRange(
                    etAmountCarb.text.toString().toFloat(),
                    AMOUNT_CARBS_IN_BREAD_UNIT_LOWER_BOUND, AMOUNT_CARBS_IN_BREAD_UNIT_UPPER_BOUND
                )
            ) {
                setFocusWithMessage(etAmountCarb, true, true, getString(R.string.incorrect_value))
                return false
            }
            return true
        }

    // initialize views on screen and their listening
    private fun initViews() {
        // find views on screen by id
        btnSavePreferences = findViewById(R.id.btnSavePreferences)
        btnResetToDefault = findViewById(R.id.btnResetToDefault)
        btnDeleteAllMeasurements = findViewById(R.id.btnDeleteAllMeasurements)
        btnBackupDB = findViewById(R.id.btnBackupDB)
        btnRestoreDB = findViewById(R.id.btnRestoreDB)
        rbDiabetesType1 = findViewById(R.id.rbDiabetesType1)
        rbDiabetesType2 = findViewById(R.id.rbDiabetesType2)
        rbUnitOfBloodSugarMmolL = findViewById(R.id.rbUnitOfBloodSugarMmolL)
        rbUnitOfBloodSugarMgdL = findViewById(R.id.rbUnitOfBloodSugarMgdL)
        rbTimeFormat12h = findViewById(R.id.rbTimeFormat12h)
        rbTimeFormat24h = findViewById(R.id.rbTimeFormat24h)
        rbBeginningWeekSat = findViewById(R.id.rbBeginningWeekSat)
        rbBeginningWeekSun = findViewById(R.id.rbBeginningWeekSun)
        rbBeginningWeekMon = findViewById(R.id.rbBeginningWeekMon)
        rgDiabetesType = findViewById(R.id.rgDiabetesType)
        rgUnitOfBloodSugar = findViewById(R.id.rgUnitOfBloodSugar)
        rgTimeFormat = findViewById(R.id.rgTimeFormat)
        rgBeginningWeek = findViewById(R.id.rgBeginningWeek)
        etBloodLowSugar = findViewById(R.id.etBloodLowSugar)
        etBloodHighSugar = findViewById(R.id.etBloodHighSugar)
        etAmountCarb = findViewById(R.id.etAmountCarbInBreadUnit)
        vBtnDelAllMeasTop = findViewById(R.id.vBtnDelAllMeasTop)
        vBtnDelAllMeasBottom = findViewById(R.id.vBtnDelAllMeasBottom)

        // listeners for views
        btnSavePreferences.setOnClickListener(this)
        btnResetToDefault.setOnClickListener(this)
        btnDeleteAllMeasurements.setOnClickListener(this)
        btnBackupDB.setOnClickListener(this)
        btnRestoreDB.setOnClickListener(this)
        rbDiabetesType1.setOnClickListener(this)
        rbDiabetesType2.setOnClickListener(this)
        rbUnitOfBloodSugarMmolL.setOnClickListener(this)
        rbUnitOfBloodSugarMgdL.setOnClickListener(this)
        rbTimeFormat12h.setOnClickListener(this)
        rbTimeFormat24h.setOnClickListener(this)

        // set hint for editText
        setEditTextsHints(prefsUnitBloodSugarMmol)
        etAmountCarb.setHint(
            String.format(
                Locale.ENGLISH, getString(R.string.from_toFloat),
                AMOUNT_CARBS_IN_BREAD_UNIT_LOWER_BOUND, AMOUNT_CARBS_IN_BREAD_UNIT_UPPER_BOUND
            )
        )

        //skip place

        // event on text change in editTexts
        etBloodLowSugar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                preferencesChanged(true, true)

                // set one point accuracy for editText value
                if (rbUnitOfBloodSugarMmolL.isChecked) {
                    val str =
                        MyUtill.getStringNumberWithAccuracy(charSequence.toString(), 1, '.', false)
                    if (str.length != charSequence.toString().length) {
                        etBloodLowSugar.setText(str)
                        etBloodLowSugar.setSelection(str.length)
                    }
                }
            }

            override fun afterTextChanged(charSequence: Editable) {}
        })
        etBloodHighSugar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                preferencesChanged(true, true)

                // set one point accuracy for editText value
                if (rbUnitOfBloodSugarMmolL.isChecked) {
                    val str =
                        MyUtill.getStringNumberWithAccuracy(charSequence.toString(), 1, '.', false)
                    if (str.length != charSequence.toString().length) {
                        etBloodHighSugar.setText(str)
                        etBloodHighSugar.setSelection(str.length)
                    }
                }
            }

            override fun afterTextChanged(charSequence: Editable) {}
        })
        etAmountCarb.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                preferencesChanged(true, true)

                // set one point accuracy for editText value
                if (rbUnitOfBloodSugarMmolL.isChecked) {
                    val str =
                        MyUtill.getStringNumberWithAccuracy(charSequence.toString(), 1, '.', false)
                    if (str.length != charSequence.toString().length) {
                        etAmountCarb.setText(str)
                        etAmountCarb.setSelection(str.length)
                    }
                }
            }

            override fun afterTextChanged(charSequence: Editable) {}
        })

        // On Checked Change event to enable button save preferences
        rgUnitOfBloodSugar.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.rbUnitOfBloodSugarMmolL -> {
                    etBloodLowSugar.inputType =
                        (InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
                    if (!MyUtill.isEmpty(etBloodLowSugar)) {
                        tmpBloodLowSugar = etBloodLowSugar.text.toString().toFloat()
                        etBloodLowSugar.setText(
                            MyUtill.roundUp(tmpBloodLowSugar / 18, 1).toString()
                        )
                    }
                    if (!MyUtill.isEmpty(etBloodHighSugar)) {
                        tmpBloodHighSugar = etBloodHighSugar.getText().toString().toFloat()
                        etBloodHighSugar.setText(
                            MyUtill.roundUp(tmpBloodHighSugar / 18, 1).toString()
                        )
                    }
                }
                R.id.rbUnitOfBloodSugarMgdL -> {
                    etBloodLowSugar.inputType = InputType.TYPE_CLASS_NUMBER
                    if (!MyUtill.isEmpty(etBloodLowSugar)) {
                        tmpBloodLowSugar = etBloodLowSugar.text.toString().toFloat()
                        etBloodLowSugar.setText(((tmpBloodLowSugar * 18).toInt()).toString())
                    }
                    if (!MyUtill.isEmpty(etBloodHighSugar)) {
                        tmpBloodHighSugar = etBloodHighSugar.text.toString().toFloat()
                        etBloodHighSugar.setText(((tmpBloodHighSugar * 18).toInt()).toString())
                    }
                }
                else -> {}
            }
            setEditTextsHints(rbUnitOfBloodSugarMmolL.isChecked)
            preferencesChanged(true, true)
        }
        rgDiabetesType.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            preferencesChanged(true, true)
        }
        rgTimeFormat.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            preferencesChanged(true, true)
        }
        rgBeginningWeek.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            preferencesChanged(true, true)
        }
    }

    companion object {
        //-------------------------------------DECLARE BLOCK START------------------------------------//
        // value range for BloodLowSugar and BloodHighSugar (without much need not to change)
        const val BLOOD_LOW_SUGAR_LOWER_BOUND = 2.0f
        const val BLOOD_LOW_SUGAR_UPPER_BOUND = 4.9f
        const val BLOOD_HIGH_SUGAR_LOWER_BOUND = 7.8f
        const val BLOOD_HIGH_SUGAR_UPPER_BOUND = 12f
        const val AMOUNT_CARBS_IN_BREAD_UNIT_LOWER_BOUND = 10f
        const val AMOUNT_CARBS_IN_BREAD_UNIT_UPPER_BOUND = 15f

        // default preferences value
        const val BLOOD_LOW_SUGAR_DEFAULT = 3.8f
        const val BLOOD_HIGH_SUGAR_DEFAULT = 8.9f
        const val AMOUNT_CARBS_IN_BREAD_UNIT_DEFAULT = 12f
        const val DIABETES_1_TYPE_DEFAULT = true
        const val UNIT_BLOOD_SUGAR_MMOL_DEFAULT = true
        const val TIME_FORMAT_24H_DEFAULT = true
        const val BEGINNING_WEEK_DEFAULT = 2

        // keys for option menu
        const val IDM_INFO = 101

        // keys
        const val KEY_PREFS = "settings_pref"
        const val KEY_PREFS_DIABETES_1TYPE = "diabetes1Type"
        const val KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL = "unitBloodSugarMmol"
        const val KEY_PREFS_BLOOD_LOW_SUGAR = "bloodLowSugar"
        const val KEY_PREFS_BLOOD_HIGH_SUGAR = "bloodHighSugar"
        const val KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT = "amountCarbohydratesInBreadUnit"
        const val KEY_PREFS_FIRST_RUN_AGREEMENT = "firstRunAgreement"
        const val KEY_PREFS_TIME_FORMAT_24H = "timeFormat24hDefault2"
        const val KEY_PREFS_BEGINNING_WEEK = "beginningWeek"
        private const val TAG = "PreferencesActivity"

        // temporary variables
        private var back_pressed: Long = 0
    }
}