package com.example.jason.bloodGlucoseMonitoring;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Objects;


public class PreferencesActivity extends AppCompatActivity implements View.OnClickListener {
    //-------------------------------------DECLARE BLOCK START------------------------------------//
    // value range for seekBar BloodLowSugar and BloodHighSugar (without much need not to change)
    protected static final float bloodLowSugarLowerBound = 3.2f;
    protected static final float bloodLowSugarUpperBound = 4.9f;
    protected static final float bloodHighSugarLowerBound = 7.8f;
    protected static final float bloodHighSugarUpperBound = 12f;
    protected static final float amountCarbsInBreadUnitLowerBound = 10f;
    protected static final float amountCarbsInBreadUnitUpperBound = 15f;

    // default preferences value
    protected static final float bloodLowSugarDefault = 3.8f;
    protected static final float bloodHighSugarDefault = 8.9f;
    protected static final float amountCarbsInBreadUnitDefault = 12f;
    protected static final boolean diabetesType1Default = true;
    protected static final boolean timeFormat24h = true;
    /*//---FUTURE OPTION---unit blood sugar-mg/dL---
    private static final boolean unitBloodSugarMmolLDefaultValue = true;
    */

    // keys
    protected static final String KEY_PREFS = "settings_pref";
    protected static final String KEY_PREFS_DIABETES_TYPE = "diabetesType";
    protected static final String KEY_PREFS_UNIT_BLOOD_SUGAR = "unitBloodSugar";
    protected static final String KEY_PREFS_BLOOD_LOW_SUGAR = "bloodLowSugar";
    protected static final String KEY_PREFS_BLOOD_HIGH_SUGAR = "bloodHighSugar";
    protected static final String KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT = "amountCarbohydratesInBreadUnit";
    protected static final String KEY_PREFS_FIRST_RUN_AGREEMENT = "firstRunAgreement";
    protected static final String KEY_PREFS_TIME_FORMAT_24H = "timeFormat24h";

    // variables for preferences
    String prefsDiabetesType;
    String prefsUnitBloodSugar;
    float prefsBloodLowSugar;
    float prefsBloodHighSugar;
    float prefsAmountCarbsInBreadUnit;
    boolean prefsTimeFormat24h;

    // temporary variables
    private static long back_pressed;
    boolean isChangeSetting;
    boolean firstRun;

    // views declare
    Button btnSavePreferences;
    Button btnResetToDefault;
    Button btnDeleteAllMeasurements;

    RadioButton rbDiabetesType1;
    RadioButton rbDiabetesType2;
    RadioButton rbTimeFormat12h;
    RadioButton rbTimeFormat24h;
    RadioButton rbUnitOfBloodSugarMmolL;
    /*//---FUTURE OPTION---unit blood sugar-mg/dL---
    RadioButton rbUnitOfBloodSugarMgdL;
    */

    EditText etBloodLowSugar;
    EditText etBloodHighSugar;
    EditText etAmountCarb;

    // SQLite database
    DBHelper dbHelper;
    //-------------------------------------DECLARE BLOCK END--------------------------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        // initializing starts variables
        isChangeSetting = false;

        // find views on screen by id
        btnSavePreferences = (Button) findViewById(R.id.btnSavePreferences);
        btnResetToDefault = (Button) findViewById(R.id.btnResetToDefault);
        btnDeleteAllMeasurements = (Button) findViewById(R.id.btnDeleteAllMeasurements);

        rbDiabetesType1 = (RadioButton) findViewById(R.id.rbDiabetesType1);
        rbDiabetesType2 = (RadioButton) findViewById(R.id.rbDiabetesType2);
        rbTimeFormat12h = (RadioButton) findViewById(R.id.rbTimeFormat12h);
        rbTimeFormat24h = (RadioButton) findViewById(R.id.rbTimeFormat24h);
        rbUnitOfBloodSugarMmolL = (RadioButton) findViewById(R.id.rbUnitOfBloodSugarMmolL);
        /*//---FUTURE OPTION---unit blood sugar-mg/dL---
        rbUnitOfBloodSugarMgdL = (RadioButton) findViewById(R.id.rbUnitOfBloodSugarMgdL);
        */

        etBloodLowSugar = (EditText) findViewById(R.id.etBloodLowSugar);
        etBloodHighSugar = (EditText) findViewById(R.id.etBloodHighSugar);
        etAmountCarb = (EditText) findViewById(R.id.etAmountCarbInBreadUnit);

        // set hints for editText
        etBloodLowSugar.setHint(String.format(getString(R.string.from_to), bloodLowSugarLowerBound, bloodLowSugarUpperBound));
        etBloodHighSugar.setHint(String.format(getString(R.string.from_to), bloodHighSugarLowerBound, bloodHighSugarUpperBound));
        etAmountCarb.setHint(String.format(getString(R.string.from_to), amountCarbsInBreadUnitLowerBound, amountCarbsInBreadUnitUpperBound));

        // set the listeners for views
        btnSavePreferences.setOnClickListener(this);
        btnResetToDefault.setOnClickListener(this);
        btnDeleteAllMeasurements.setOnClickListener(this);

        rbDiabetesType1.setOnClickListener(this);
        rbDiabetesType2.setOnClickListener(this);
        rbTimeFormat12h.setOnClickListener(this);
        rbTimeFormat24h.setOnClickListener(this);

        //  load and set preferences in preferences menu
        loadPreferences();

        etBloodLowSugar.setText(String.valueOf(prefsBloodLowSugar));
        etBloodHighSugar.setText(String.valueOf(prefsBloodHighSugar));
        etAmountCarb.setText(String.valueOf(prefsAmountCarbsInBreadUnit));

        if (Objects.equals(prefsDiabetesType, getString(R.string._1_type))) {
            rbDiabetesType1.setChecked(true);
        } else {
            rbDiabetesType2.setChecked(true);
        }
        if (Objects.equals(prefsUnitBloodSugar, getString(R.string.mmol_l))) {
            rbUnitOfBloodSugarMmolL.setChecked(true);
        }
        //---FUTURE OPTION---unit blood sugar-mg/dL---
        //else {
        //    rbUnitOfBloodSugarMgdL.setChecked(true);
        //}
        //
        if (prefsTimeFormat24h) {
            rbTimeFormat24h.setChecked(true);
        } else {
            rbTimeFormat12h.setChecked(true);
        }

        // event on text change in editTexts
        etBloodLowSugar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                preferencesChanged(true, true);
                String str = getStringNumberWithAccuracy(String.valueOf(charSequence), 1, '.', false);
                if (str.length() != charSequence.toString().length()) {
                    etBloodLowSugar.setText(str);
                    etBloodLowSugar.setSelection(str.length());
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
                String str = getStringNumberWithAccuracy(String.valueOf(charSequence), 1, '.', false);
                if (str.length() != charSequence.toString().length()) {
                    etBloodHighSugar.setText(str);
                    etBloodHighSugar.setSelection(str.length());
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
                String str = getStringNumberWithAccuracy(String.valueOf(charSequence), 1, '.', false);
                if (str.length() != charSequence.toString().length()) {
                    etAmountCarb.setText(str);
                    etAmountCarb.setSelection(str.length());
                }
            }

            @Override
            public void afterTextChanged(Editable charSequence) {
            }
        });

        // get firstRunAgreement value
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
        firstRun = sharedPref.getBoolean(KEY_PREFS_FIRST_RUN_AGREEMENT, true);

        // check for first run app
        if (firstRun) {
            preferencesChanged(true, true);
            Intent intent = new Intent(PreferencesActivity.this, AgreementActivity.class);
            startActivity(intent);
        }

        dbHelper = new DBHelper(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // saving preferences
            case R.id.btnSavePreferences:
                if (isCorrectInputValues()) {
                    savePreferences();
                    Toast.makeText(this, R.string.settings_have_been_saved, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            // reset preferences to default but not save
            case R.id.btnResetToDefault:
                resetPreferencesToDefault();
                break;

            // delete all measurements
            case R.id.btnDeleteAllMeasurements:
                // Alert dialog to confirm delete all measurements
                AlertDialog.Builder alert = new AlertDialog.Builder(this);

                alert.setTitle(getString(R.string.delete_all_measurements));
                alert.setMessage(getString(R.string.these_changes_can_t_return));
                alert.setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Canceled.
                    }
                });
                alert.setNeutralButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SQLiteDatabase database = dbHelper.getWritableDatabase();
                        database.delete(DBHelper.TABLE_MEASUREMENTS, null, null);
                        database.close();
                        Toast.makeText(PreferencesActivity.this, getString(R.string.all_measurements_has_been_deleted), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                alert.show();
                break;

            case R.id.rbDiabetesType1:
            case R.id.rbDiabetesType2:
            case R.id.rbTimeFormat12h:
            case R.id.rbTimeFormat24h:
                preferencesChanged(true, true);
                break;

            // switch default
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
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
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
        prefsDiabetesType = sharedPref.getString(KEY_PREFS_DIABETES_TYPE, getString(R.string._1_type));
        prefsUnitBloodSugar = sharedPref.getString(KEY_PREFS_UNIT_BLOOD_SUGAR, getString(R.string.mmol_l));
        prefsBloodLowSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_LOW_SUGAR, bloodLowSugarDefault);
        prefsBloodHighSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_HIGH_SUGAR, bloodHighSugarDefault);
        prefsAmountCarbsInBreadUnit = sharedPref.getFloat(KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT, amountCarbsInBreadUnitDefault);

        // get saved value for function of interface
        prefsTimeFormat24h = sharedPref.getBoolean(KEY_PREFS_TIME_FORMAT_24H, true);
    }

    // save preferences in sharedPreferences
    public void savePreferences() {
        // get settings object in private mode
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPref.edit();

        // put preferences values
        prefEditor.putFloat(KEY_PREFS_BLOOD_LOW_SUGAR, Float.parseFloat(etBloodLowSugar.getText().toString()));
        prefEditor.putFloat(KEY_PREFS_BLOOD_HIGH_SUGAR, Float.parseFloat(etBloodHighSugar.getText().toString()));
        prefEditor.putFloat(KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT, Float.parseFloat(etAmountCarb.getText().toString()));

        if (rbDiabetesType1.isChecked()) {
            prefEditor.putString(KEY_PREFS_DIABETES_TYPE, rbDiabetesType1.getText().toString());
        } else {
            prefEditor.putString(KEY_PREFS_DIABETES_TYPE, rbDiabetesType2.getText().toString());
        }
        /*//---FUTURE OPTION---unit blood sugar-mg/dL---
        if (rbUnitOfBloodSugarMmolL.isChecked()) {
            prefEditor.putString("prefsUnitBloodSugar",
                rbUnitOfBloodSugarMmolL.getText().toString());
        } else {
            prefEditor.putString("prefsUnitBloodSugar",
                rbUnitOfBloodSugarMgdL.getText().toString());
        }
        */

        if (rbTimeFormat24h.isChecked()) prefEditor.putBoolean(KEY_PREFS_TIME_FORMAT_24H, true);
        else prefEditor.putBoolean(KEY_PREFS_TIME_FORMAT_24H, false);

        // save change of preferences
        prefEditor.apply();

        // set flag of changing settings to false and disabled button savePreferences
        preferencesChanged(false, false);
    }

    // reset preferences to default but not save
    public void resetPreferencesToDefault() {
        // set default preferences for views
        etBloodLowSugar.setText((String.valueOf(bloodLowSugarDefault)));
        etBloodHighSugar.setText((String.valueOf(bloodHighSugarDefault)));
        etAmountCarb.setText((String.valueOf(amountCarbsInBreadUnitDefault)));

        if (diabetesType1Default) rbDiabetesType1.setChecked(true);
        else rbDiabetesType2.setChecked(true);
        /*//---FUTURE OPTION---unit blood sugar-mg/dL---
        if (unitBloodSugarMmolLDefaultValue) rbUnitOfBloodSugarMmolL.setChecked(true);
            else rbUnitOfBloodSugarMgdL.setChecked(true);
        */
        if (timeFormat24h) rbTimeFormat24h.setChecked(true);
        else rbDiabetesType2.setChecked(true);

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

    // get text with set accuracy after specified symbol(separator)
    public String getStringNumberWithAccuracy(String s, int scale, Character separator, boolean fillZero) {
        int sLength = s.length();
        int sepIndex = s.indexOf(separator);
        // conditions of if:
        //  1 - length of string must be greater than 1
        //  2 - is the number needs to be set accuracy
        //  3 - is the number contains stated symbol
        if ((sLength > 1) && (sLength - 1 != sepIndex + scale) && (s.contains(separator.toString()))) {
            // (sLength - 1 - sepIndex) - number of digits after the separator greater than scale
            if ((sLength - 1 - sepIndex > scale)) {
                // get substring with specified number of digits after separator
                s = s.substring(0, sepIndex + 1 + scale);
            } else {
                // is needed filling zeros
                if (fillZero) {
                    // filling zeros at start
                    while (s.length() - 1 < sepIndex + scale) {
                        s += "0";
                    }
                }
            }
        }
        return s;
    }

    // checking of number for included in the specified range.
    public boolean numberInRange(Float number, float lowerBound, float upperBound) {
        if (number < lowerBound || number > upperBound) {
            return false;
        }
        return true;
    }

    // checking fields on empty
    public boolean isEmpty(EditText et) {
        return et.getText().toString().trim().length() == 0;
    }

    // set focus and additional option: show message, clear editText if needs
    public void setFocus(EditText et, boolean clearET, boolean showMessage, String message) {
        if (showMessage) Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        if (clearET) et.setText("");
        et.requestFocus();
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
        if (!numberInRange(Float.parseFloat(etBloodLowSugar.getText().toString()), bloodLowSugarLowerBound, bloodLowSugarUpperBound)) {
            setFocus(etBloodLowSugar, true, true, getString(R.string.incorrect_input_value));
            return false;
        }
        if (!numberInRange(Float.parseFloat(etBloodHighSugar.getText().toString()), bloodHighSugarLowerBound, bloodHighSugarUpperBound)) {
            setFocus(etBloodHighSugar, true, true, getString(R.string.incorrect_input_value));
            return false;
        }
        if (!numberInRange(Float.parseFloat(etAmountCarb.getText().toString()), amountCarbsInBreadUnitLowerBound, amountCarbsInBreadUnitUpperBound)) {
            setFocus(etAmountCarb, true, true, getString(R.string.incorrect_input_value));
            return false;
        }
        return true;
    }
}













/*
    String[] listDiabetesType = new String[] {"1", "2"};
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PreferencesActivity.this,
            R.layout.support_simple_spinner_dropdown_item, listDiabetesType);
adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        Spinner spinnerDiabetesType = (Spinner) findViewById(R.id.spinnerDiabetesType);
        spinnerDiabetesType.setAdapter(adapter);
        spinnerDiabetesType.setPrompt("Choose you diabetes type");
        spinnerDiabetesType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
@Override
public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(PreferencesActivity.this, "" + position + " " + id, Toast.LENGTH_SHORT).show();
        }

@Override
public void onNothingSelected(AdapterView<?> parent) {

        }
        });





    // move text with seekBar item
        int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
        tvBloodLowSugarValue.setText(String.format("%.1f", progressDecimal));
        tvBloodLowSugarValue.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);


*/