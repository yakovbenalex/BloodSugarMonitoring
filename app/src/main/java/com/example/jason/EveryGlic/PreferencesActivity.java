package com.example.jason.EveryGlic;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Locale;

import static com.example.jason.EveryGlic.MyWorks.createInfoItemInActionBar;
import static com.example.jason.EveryGlic.MyWorks.getStringNumberWithAccuracy;
import static com.example.jason.EveryGlic.MyWorks.isEmpty;
import static com.example.jason.EveryGlic.MyWorks.numberInRange;
import static com.example.jason.EveryGlic.MyWorks.parseMenuItemInfo;
import static com.example.jason.EveryGlic.MyWorks.roundUp;


public class PreferencesActivity extends AppCompatActivity implements View.OnClickListener {
    //-------------------------------------DECLARE BLOCK START------------------------------------//
    // value range for BloodLowSugar and BloodHighSugar (without much need not to change)
    protected static final float BLOOD_LOW_SUGAR_LOWER_BOUND = 3.2f;
    protected static final float BLOOD_LOW_SUGAR_UPPER_BOUND = 4.9f;
    protected static final float BLOOD_HIGH_SUGAR_LOWER_BOUND = 7.8f;
    protected static final float BLOOD_HIGH_SUGAR_UPPER_BOUND = 12f;
    protected static final float AMOUNT_CARBS_IN_BREAD_UNIT_LOWER_BOUND = 10f;
    protected static final float AMOUNT_CARBS_IN_BREAD_UNIT_UPPER_BOUND = 15f;

    // default preferences value
    protected static final float BLOOD_LOW_SUGAR_DEFAULT = 3.8f;
    protected static final float BLOOD_HIGH_SUGAR_DEFAULT = 8.9f;
    protected static final float AMOUNT_CARBS_IN_BREAD_UNIT_DEFAULT = 12f;
    protected static final boolean DIABETES_1_TYPE_DEFAULT = true;
    protected static final boolean UNIT_BLOOD_SUGAR_MMOL_DEFAULT = true;
    protected static final boolean TIME_FORMAT_24H_DEFAULT = true;
    protected static final int BEGINNING_WEEK_DEFAULT = 2;

    // keys for option menu
    static final int IDM_INFO = 101;

    // keys
    protected static final String KEY_PREFS = "settings_pref";
    protected static final String KEY_PREFS_DIABETES_1TYPE = "diabetes1Type";
    protected static final String KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL = "unitBloodSugarMmol";
    protected static final String KEY_PREFS_BLOOD_LOW_SUGAR = "bloodLowSugar";
    protected static final String KEY_PREFS_BLOOD_HIGH_SUGAR = "bloodHighSugar";
    protected static final String KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT = "amountCarbohydratesInBreadUnit";
    protected static final String KEY_PREFS_FIRST_RUN_AGREEMENT = "firstRunAgreement";
    protected static final String KEY_PREFS_TIME_FORMAT_24H = "timeFormat24hDefault2";
    protected static final String KEY_PREFS_BEGINNING_WEEK = "beginningWeek";

    protected static final String KEY_PREFS_CUR_APP_VER_CODE = "currentAppVersionCode";
    protected static final String KEY_PREFS_DB_NEED_UPDATE = "dbNeedUpdate";

    private static final String TAG = "PreferencesActivity";

    // variables for preferences
    float prefsBloodLowSugar;
    float prefsBloodHighSugar;
    float prefsAmountCarbsInBreadUnit;
    boolean prefsDiabetes1Type;
    boolean prefsUnitBloodSugarMmol;
    boolean prefsTimeFormat24h;
    int prefsBeginningWeek;
    boolean firstRun;

    // temporary variables
    private static long back_pressed;
    boolean isChangeSetting;
    float tmpBloodLowSugar;
    float tmpBloodHighSugar;

    // views declare
    Button btnSavePreferences;
    Button btnResetToDefault;
    Button btnDeleteAllMeasurements;

    RadioButton rbDiabetesType1;
    RadioButton rbDiabetesType2;
    RadioButton rbUnitOfBloodSugarMmolL;
    RadioButton rbUnitOfBloodSugarMgdL;
    RadioButton rbTimeFormat12h;
    RadioButton rbTimeFormat24h;
    RadioButton rbBeginningWeekSat;
    RadioButton rbBeginningWeekSun;
    RadioButton rbBeginningWeekMon;

    RadioGroup rgDiabetesType;
    RadioGroup rgUnitOfBloodSugar;
    RadioGroup rgTimeFormat;
    RadioGroup rgBeginningWeek;

    EditText etBloodLowSugar;
    EditText etBloodHighSugar;
    EditText etAmountCarb;

    View vBtnDelAllMeasTop;
    View vBtnDelAllMeasBottom;

    // SQLite database
    DBHelper dbHelper;
    //-------------------------------------DECLARE BLOCK END--------------------------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        // create database object
        dbHelper = new DBHelper(this);

        // initializing starts variables
        isChangeSetting = false;

        // find views on screen by id
        btnSavePreferences = findViewById(R.id.btnSavePreferences);
        btnResetToDefault = findViewById(R.id.btnResetToDefault);
        btnDeleteAllMeasurements = findViewById(R.id.btnDeleteAllMeasurements);

        rbDiabetesType1 = findViewById(R.id.rbDiabetesType1);
        rbDiabetesType2 = findViewById(R.id.rbDiabetesType2);
        rbUnitOfBloodSugarMmolL = findViewById(R.id.rbUnitOfBloodSugarMmolL);
        rbUnitOfBloodSugarMgdL = findViewById(R.id.rbUnitOfBloodSugarMgdL);
        rbTimeFormat12h = findViewById(R.id.rbTimeFormat12h);
        rbTimeFormat24h = findViewById(R.id.rbTimeFormat24h);
        rbBeginningWeekSat = findViewById(R.id.rbBeginningWeekSat);
        rbBeginningWeekSun = findViewById(R.id.rbBeginningWeekSun);
        rbBeginningWeekMon = findViewById(R.id.rbBeginningWeekMon);

        rgDiabetesType = findViewById(R.id.rgDiabetesType);
        rgUnitOfBloodSugar = findViewById(R.id.rgUnitOfBloodSugar);
        rgTimeFormat = findViewById(R.id.rgTimeFormat);
        rgBeginningWeek = findViewById(R.id.rgBeginningWeek);

        etBloodLowSugar = findViewById(R.id.etBloodLowSugar);
        etBloodHighSugar = findViewById(R.id.etBloodHighSugar);
        etAmountCarb = findViewById(R.id.etAmountCarbInBreadUnit);

        vBtnDelAllMeasTop = findViewById(R.id.vBtnDelAllMeasTop);
        vBtnDelAllMeasBottom = findViewById(R.id.vBtnDelAllMeasBottom);

        // listeners for views
        btnSavePreferences.setOnClickListener(this);
        btnResetToDefault.setOnClickListener(this);
        btnDeleteAllMeasurements.setOnClickListener(this);

        rbDiabetesType1.setOnClickListener(this);
        rbDiabetesType2.setOnClickListener(this);
        rbUnitOfBloodSugarMmolL.setOnClickListener(this);
        rbUnitOfBloodSugarMgdL.setOnClickListener(this);
        rbTimeFormat12h.setOnClickListener(this);
        rbTimeFormat24h.setOnClickListener(this);

        // get preferences values
        loadPreferences();

        // set hint for editText
        setEditTextsHints(prefsUnitBloodSugarMmol);
        etAmountCarb.setHint(String.format(Locale.ENGLISH, getString(R.string.from_toF),
                AMOUNT_CARBS_IN_BREAD_UNIT_LOWER_BOUND, AMOUNT_CARBS_IN_BREAD_UNIT_UPPER_BOUND));

        // set preferences values
        etAmountCarb.setText(String.valueOf(prefsAmountCarbsInBreadUnit));

        if (prefsDiabetes1Type) rbDiabetesType1.setChecked(true);
        else rbDiabetesType2.setChecked(true);

        if (prefsUnitBloodSugarMmol) {
            // set editTexts input type to decimal number
            etBloodLowSugar.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            etBloodHighSugar.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            rbUnitOfBloodSugarMmolL.setChecked(true);
            etBloodLowSugar.setText(String.valueOf(prefsBloodLowSugar));
            etBloodHighSugar.setText(String.valueOf(prefsBloodHighSugar));
        } else {
            // set editTexts input type to integer number
            etBloodLowSugar.setInputType(InputType.TYPE_CLASS_NUMBER);
            etBloodHighSugar.setInputType(InputType.TYPE_CLASS_NUMBER);

            rbUnitOfBloodSugarMgdL.setChecked(true);
            etBloodLowSugar.setText(String.valueOf((int) (prefsBloodLowSugar * 18)));
            etBloodHighSugar.setText(String.valueOf((int) (prefsBloodHighSugar * 18)));
        }

        if (prefsTimeFormat24h) rbTimeFormat24h.setChecked(true);
        else rbTimeFormat12h.setChecked(true);

        switch (prefsBeginningWeek) {
            case 2:
                rbBeginningWeekMon.setChecked(true);
                break;
            case 1:
                rbBeginningWeekSun.setChecked(true);
                break;
            case 0:
                rbBeginningWeekSat.setChecked(true);
                break;
            default:
                break;
        }

        // event on text change in editTexts
        etBloodLowSugar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                preferencesChanged(true, true);

                // set one point accuracy for editText value
                if (rbUnitOfBloodSugarMmolL.isChecked()) {
                    String str = getStringNumberWithAccuracy(String.valueOf(charSequence), 1, '.', false);
                    if (str.length() != charSequence.toString().length()) {
                        etBloodLowSugar.setText(str);
                        etBloodLowSugar.setSelection(str.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable charSequence) {
            }
        });
        etBloodHighSugar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                preferencesChanged(true, true);

                // set one point accuracy for editText value
                if (rbUnitOfBloodSugarMmolL.isChecked()) {
                    String str = getStringNumberWithAccuracy(String.valueOf(charSequence), 1, '.', false);
                    if (str.length() != charSequence.toString().length()) {
                        etBloodHighSugar.setText(str);
                        etBloodHighSugar.setSelection(str.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable charSequence) {
            }
        });
        etAmountCarb.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                preferencesChanged(true, true);

                // set one point accuracy for editText value
                if (rbUnitOfBloodSugarMmolL.isChecked()) {
                    String str = getStringNumberWithAccuracy(String.valueOf(charSequence), 1, '.', false);
                    if (str.length() != charSequence.toString().length()) {
                        etAmountCarb.setText(str);
                        etAmountCarb.setSelection(str.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable charSequence) {
            }
        });

        // On Checked Change event to enable button save preferences
        rgUnitOfBloodSugar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                // set converted value depending on the type unit of blood sugar
                switch (checkedId) {
                    case R.id.rbUnitOfBloodSugarMmolL:
                        etBloodLowSugar.setInputType(InputType.TYPE_CLASS_NUMBER
                                | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        if (!isEmpty(etBloodLowSugar)) {
                            tmpBloodLowSugar = Float.parseFloat(etBloodLowSugar.getText().toString());
                            etBloodLowSugar.setText(String.valueOf(roundUp(tmpBloodLowSugar / 18, 1)));
                        }
                        if (!isEmpty(etBloodHighSugar)) {
                            tmpBloodHighSugar = Float.parseFloat(etBloodHighSugar.getText().toString());
                            etBloodHighSugar.setText(String.valueOf(roundUp(tmpBloodHighSugar / 18, 1)));
                        }
                        break;

                    case R.id.rbUnitOfBloodSugarMgdL:
                        etBloodLowSugar.setInputType(InputType.TYPE_CLASS_NUMBER);
                        if (!isEmpty(etBloodLowSugar)) {
                            tmpBloodLowSugar = Float.parseFloat(etBloodLowSugar.getText().toString());
                            etBloodLowSugar.setText(String.valueOf((int) (tmpBloodLowSugar * 18)));
                        }
                        if (!isEmpty(etBloodHighSugar)) {
                            tmpBloodHighSugar = Float.parseFloat(etBloodHighSugar.getText().toString());
                            etBloodHighSugar.setText(String.valueOf((int) (tmpBloodHighSugar * 18)));
                        }
                        break;

                    default:
                        break;
                }
                setEditTextsHints(rbUnitOfBloodSugarMmolL.isChecked());
                preferencesChanged(true, true);
            }
        });

        rgDiabetesType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                preferencesChanged(true, true);
            }
        });
        rgTimeFormat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                preferencesChanged(true, true);
            }
        });
        rgBeginningWeek.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                preferencesChanged(true, true);
            }
        });

        // get firstRunAgreement value
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
        firstRun = sharedPref.getBoolean(KEY_PREFS_FIRST_RUN_AGREEMENT, true);

        // check for first run app
        if (firstRun) {
            // hide button delete all measurements
            btnDeleteAllMeasurements.setVisibility(View.INVISIBLE);
            vBtnDelAllMeasTop.setVisibility(View.INVISIBLE);
            vBtnDelAllMeasBottom.setVisibility(View.INVISIBLE);

            preferencesChanged(true, true);

            //start Agreement Activity
            Intent intent = new Intent(PreferencesActivity.this, AgreementActivity.class);
            startActivity(intent);
        }
    }

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
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // delete all measurements
            case R.id.btnDeleteAllMeasurements:
                // Alert dialog to confirm delete all measurements
                AlertDialog.Builder alert = new AlertDialog.Builder(this);

                alert.setTitle(getString(R.string.delete_all_measurements));
                alert.setMessage(getString(R.string.these_changes_can_t_return));
                alert.setNegativeButton(getString(android.R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Canceled.
                            }
                        });
                alert.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SQLiteDatabase database = dbHelper.getWritableDatabase();
                        database.delete(DBHelper.TABLE_MEASUREMENTS, null, null);
                        database.close();
                        Toast.makeText(PreferencesActivity.this, getString(
                                R.string.all_measurements_has_been_deleted),
                                Toast.LENGTH_LONG).show();

                        finish(); //exit from preferences
                    }
                });
                alert.show();
                break;

            // reset preferences to default but not save
            case R.id.btnResetToDefault:
                resetPreferencesToDefault();
                break;

            // saving preferences
            case R.id.btnSavePreferences:
                if (isCorrectInputValues()) {
                    savePreferences();
                    Toast.makeText(this, R.string.settings_have_been_saved, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        backToMenu(isChangeSetting, getString(R.string.please_press_again_to_back_to_menu) + "\n" +
                getString(R.string.changes_will_not_saved));
    }

    // back to menu on double press to back button in a short time, else show specified message
    public void backToMenu(boolean checked, String message) {
        if (checked) {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                finish();
            } else {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
            back_pressed = System.currentTimeMillis();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    public void loadPreferences() {
        // get settings object
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);

        // get saved value for diabetes
        prefsDiabetes1Type = sharedPref.getBoolean(KEY_PREFS_DIABETES_1TYPE, DIABETES_1_TYPE_DEFAULT);
        prefsUnitBloodSugarMmol = sharedPref.getBoolean(KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL,
                UNIT_BLOOD_SUGAR_MMOL_DEFAULT);
        prefsBloodLowSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_LOW_SUGAR, BLOOD_LOW_SUGAR_DEFAULT);
        prefsBloodHighSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_HIGH_SUGAR, BLOOD_HIGH_SUGAR_DEFAULT);
        prefsAmountCarbsInBreadUnit = sharedPref.getFloat(KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT,
                AMOUNT_CARBS_IN_BREAD_UNIT_DEFAULT);

        // get saved value for function of interface
        prefsTimeFormat24h = sharedPref.getBoolean(KEY_PREFS_TIME_FORMAT_24H, TIME_FORMAT_24H_DEFAULT);
        prefsBeginningWeek = sharedPref.getInt(KEY_PREFS_BEGINNING_WEEK, BEGINNING_WEEK_DEFAULT);
    }

    // save preferences in sharedPreferences
    public void savePreferences() {
        // get settings object
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();

        // put preferences values
        if (rbDiabetesType1.isChecked()) prefEditor.putBoolean(KEY_PREFS_DIABETES_1TYPE, true);
        else prefEditor.putBoolean(KEY_PREFS_DIABETES_1TYPE, false);

        if (rbUnitOfBloodSugarMmolL.isChecked()) {
            prefEditor.putBoolean(KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL, true);
            prefEditor.putFloat(KEY_PREFS_BLOOD_LOW_SUGAR,
                    Float.parseFloat(etBloodLowSugar.getText().toString()));
            prefEditor.putFloat(KEY_PREFS_BLOOD_HIGH_SUGAR,
                    Float.parseFloat(etBloodHighSugar.getText().toString()));
        } else {
            tmpBloodLowSugar = roundUp(Float.parseFloat(etBloodLowSugar.getText().toString()) / 18, 1).floatValue();
            tmpBloodHighSugar = roundUp(Float.parseFloat(etBloodHighSugar.getText().toString()) / 18, 1).floatValue();

            prefEditor.putBoolean(KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL, false);
            prefEditor.putFloat(KEY_PREFS_BLOOD_LOW_SUGAR, tmpBloodLowSugar);
            prefEditor.putFloat(KEY_PREFS_BLOOD_HIGH_SUGAR, tmpBloodHighSugar);
        }

        prefEditor.putFloat(KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT,
                Float.parseFloat(etAmountCarb.getText().toString()));

        if (rbTimeFormat24h.isChecked()) prefEditor.putBoolean(KEY_PREFS_TIME_FORMAT_24H, true);
        else prefEditor.putBoolean(KEY_PREFS_TIME_FORMAT_24H, false);


        switch (rgBeginningWeek.getCheckedRadioButtonId()) {
            case R.id.rbBeginningWeekSat:
                prefEditor.putInt(KEY_PREFS_BEGINNING_WEEK, 0);
                break;

            case R.id.rbBeginningWeekSun:
                prefEditor.putInt(KEY_PREFS_BEGINNING_WEEK, 1);
                break;

            case R.id.rbBeginningWeekMon:
                prefEditor.putInt(KEY_PREFS_BEGINNING_WEEK, 2);
                break;

            default:
                break;
        }

        // save change of preferences
        prefEditor.apply();

        // set flag of changing settings to false and disabled button savePreferences
        preferencesChanged(false, false);
    }

    // reset preferences to default but not save
    public void resetPreferencesToDefault() {
        // set default preferences for views

        etAmountCarb.setText((String.valueOf(AMOUNT_CARBS_IN_BREAD_UNIT_DEFAULT)));

        if (DIABETES_1_TYPE_DEFAULT) rbDiabetesType1.setChecked(true);
        else rbDiabetesType2.setChecked(true);

        if (rbUnitOfBloodSugarMmolL.isChecked()) {
            etBloodLowSugar.setText((String.valueOf(BLOOD_LOW_SUGAR_DEFAULT)));
            etBloodHighSugar.setText((String.valueOf(BLOOD_HIGH_SUGAR_DEFAULT)));
        } else {
            etBloodLowSugar.setText((String.valueOf((int) (BLOOD_LOW_SUGAR_DEFAULT * 18))));
            etBloodHighSugar.setText((String.valueOf((int) (BLOOD_HIGH_SUGAR_DEFAULT * 18))));
        }

        if (TIME_FORMAT_24H_DEFAULT) rbTimeFormat24h.setChecked(true);
        else rbTimeFormat12h.setChecked(true);

        switch (BEGINNING_WEEK_DEFAULT) {
            case 2:
                rbBeginningWeekMon.setChecked(true);
                break;
            case 1:
                rbBeginningWeekSun.setChecked(true);
                break;
            case 0:
                rbBeginningWeekSat.setChecked(true);
                break;
            default:
                break;
        }

        // set flag of changing settings to true and enabled button savePreferences
        preferencesChanged(true, true);
    }

    // changing flag of changed preferences
    public void preferencesChanged(boolean changedSetting, boolean setEnabledBtnSavePreferences) {
        // set flag of changing settings
        isChangeSetting = changedSetting;
        // set enabled button save preferences
        btnSavePreferences.setEnabled(setEnabledBtnSavePreferences);
    }

    // set hints for editTexts
    public void setEditTextsHints(boolean unitBloodSugarMmol) {
        if (unitBloodSugarMmol) {
            etBloodLowSugar.setHint(String.format(Locale.ENGLISH, getString(R.string.from_toF),
                    BLOOD_LOW_SUGAR_LOWER_BOUND, BLOOD_LOW_SUGAR_UPPER_BOUND));
            etBloodHighSugar.setHint(String.format(Locale.ENGLISH, getString(R.string.from_toF),
                    BLOOD_HIGH_SUGAR_LOWER_BOUND, BLOOD_HIGH_SUGAR_UPPER_BOUND));
        } else {
            etBloodLowSugar.setHint(String.format(getString(R.string.from_toD),
                    (int) (BLOOD_LOW_SUGAR_LOWER_BOUND * 18), (int) (BLOOD_LOW_SUGAR_UPPER_BOUND * 18)));
            etBloodHighSugar.setHint(String.format(getString(R.string.from_toD),
                    (int) (BLOOD_HIGH_SUGAR_LOWER_BOUND * 18), (int) (BLOOD_HIGH_SUGAR_UPPER_BOUND * 18)));
        }
    }

    // set focus and additional option: show message, clear editText if needs
    public void setFocusWithMessage(EditText et, boolean clearEditText, boolean showMessage, String message) {
        et.requestFocus();
        if (clearEditText) {
            et.setText("");
        }
        if (showMessage) Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // request focus for empty required fields and return true if so
    public boolean requiredFiledEmpty(EditText et) {
        if (isEmpty(et)) {
            Toast.makeText(this, getString(R.string.all_required_fields_must_be_filled), Toast.LENGTH_SHORT).show();
            et.requestFocus();
            return true;
        }
        return false;
    }

    // check fields for correctness
    public boolean isCorrectInputValues() {
        // is empty
        if (requiredFiledEmpty(etBloodLowSugar)) return false;
        if (requiredFiledEmpty(etBloodHighSugar)) return false;
        if (requiredFiledEmpty(etAmountCarb)) return false;

        // check on range input value and set focus on them
        if (rbUnitOfBloodSugarMmolL.isChecked()) {
            if (!numberInRange(Float.parseFloat(etBloodLowSugar.getText().toString()),
                    BLOOD_LOW_SUGAR_LOWER_BOUND, BLOOD_LOW_SUGAR_UPPER_BOUND)) {
                setFocusWithMessage(etBloodLowSugar, true, true, getString(R.string.incorrect_value));
                return false;
            }
            if (!numberInRange(Float.parseFloat(etBloodHighSugar.getText().toString()),
                    BLOOD_HIGH_SUGAR_LOWER_BOUND, BLOOD_HIGH_SUGAR_UPPER_BOUND)) {
                setFocusWithMessage(etBloodHighSugar, true, true, getString(R.string.incorrect_value));
                return false;
            }
        } else {
            if (!numberInRange(Float.parseFloat(etBloodLowSugar.getText().toString()),
                    (int) (BLOOD_LOW_SUGAR_LOWER_BOUND * 18), BLOOD_LOW_SUGAR_UPPER_BOUND * 18)) {
                setFocusWithMessage(etBloodLowSugar, true, true, getString(R.string.incorrect_value));
                return false;
            }
            if (!numberInRange(Float.parseFloat(etBloodHighSugar.getText().toString()),
                    (int) (BLOOD_HIGH_SUGAR_LOWER_BOUND * 18), BLOOD_HIGH_SUGAR_UPPER_BOUND * 18)) {
                setFocusWithMessage(etBloodHighSugar, true, true, getString(R.string.incorrect_value));
                return false;
            }
        }
        if (!numberInRange(Float.parseFloat(etAmountCarb.getText().toString()),
                AMOUNT_CARBS_IN_BREAD_UNIT_LOWER_BOUND, AMOUNT_CARBS_IN_BREAD_UNIT_UPPER_BOUND)) {
            setFocusWithMessage(etAmountCarb, true, true, getString(R.string.incorrect_value));
            return false;
        }
        return true;
    }
}