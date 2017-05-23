package com.example.jason.bloodGlucoseMonitoring;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Locale;

import static com.example.jason.bloodGlucoseMonitoring.MyWorks.getStringNumberWithAccuracy;
import static com.example.jason.bloodGlucoseMonitoring.MyWorks.numberInRange;
import static com.example.jason.bloodGlucoseMonitoring.MyWorks.roundUp;


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
    protected static final boolean diabetes1TypeDefault = true;
    protected static final boolean unitBloodSugarMmolDefault = true;
    protected static final boolean timeFormat24hDefault = true;

    // keys
    protected static final String KEY_PREFS = "settings_pref";
    protected static final String KEY_PREFS_DIABETES_1TYPE = "diabetes1Type";
    protected static final String KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL = "unitBloodSugarMmol";
    protected static final String KEY_PREFS_BLOOD_LOW_SUGAR = "bloodLowSugar";
    protected static final String KEY_PREFS_BLOOD_HIGH_SUGAR = "bloodHighSugar";
    protected static final String KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT = "amountCarbohydratesInBreadUnit";
    protected static final String KEY_PREFS_FIRST_RUN_AGREEMENT = "firstRunAgreement";
    protected static final String KEY_PREFS_TIME_FORMAT_24H = "timeFormat24hDefault";
    private static final String TAG = "myLog";

    // variables for preferences
    float prefsBloodLowSugar;
    float prefsBloodHighSugar;
    float prefsAmountCarbsInBreadUnit;
    boolean prefsDiabetes1Type;
    boolean prefsUnitBloodSugarMmol;
    boolean prefsTimeFormat24h;
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
    RadioButton rbTimeFormat12h;
    RadioButton rbTimeFormat24h;
    RadioButton rbUnitOfBloodSugarMmolL;
    RadioButton rbUnitOfBloodSugarMgdL;

    RadioGroup rgDiabetesType;
    RadioGroup rgUnitOfBloodSugar;
    RadioGroup rgTimeFormat;

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

        dbHelper = new DBHelper(this);

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
        rbUnitOfBloodSugarMgdL = (RadioButton) findViewById(R.id.rbUnitOfBloodSugarMgdL);

        rgDiabetesType = (RadioGroup) findViewById(R.id.rgDiabetesType);
        rgUnitOfBloodSugar = (RadioGroup) findViewById(R.id.rgUnitOfBloodSugar);
        rgTimeFormat = (RadioGroup) findViewById(R.id.rgTimeFormat);

        etBloodLowSugar = (EditText) findViewById(R.id.etBloodLowSugar);
        etBloodHighSugar = (EditText) findViewById(R.id.etBloodHighSugar);
        etAmountCarb = (EditText) findViewById(R.id.etAmountCarbInBreadUnit);

        // set the listeners for views
        btnSavePreferences.setOnClickListener(this);
        btnResetToDefault.setOnClickListener(this);
        btnDeleteAllMeasurements.setOnClickListener(this);

        rbDiabetesType1.setOnClickListener(this);
        rbDiabetesType2.setOnClickListener(this);
        rbUnitOfBloodSugarMmolL.setOnClickListener(this);
        rbUnitOfBloodSugarMgdL.setOnClickListener(this);
        rbTimeFormat12h.setOnClickListener(this);
        rbTimeFormat24h.setOnClickListener(this);

        //  load and set preferences in preferences menu
        loadPreferences();

        // set hint for editText
        setEditTextsHints(prefsUnitBloodSugarMmol);
        etAmountCarb.setHint(String.format(Locale.ENGLISH, getString(R.string.from_toF),
                amountCarbsInBreadUnitLowerBound, amountCarbsInBreadUnitUpperBound));

        // set preferences values
        etAmountCarb.setText(String.valueOf(prefsAmountCarbsInBreadUnit));

        if (prefsDiabetes1Type) rbDiabetesType1.setChecked(true);
        else rbDiabetesType2.setChecked(true);

        if (prefsUnitBloodSugarMmol) {
            etBloodLowSugar.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            rbUnitOfBloodSugarMmolL.setChecked(true);
            etBloodLowSugar.setText(String.valueOf(prefsBloodLowSugar));
            etBloodHighSugar.setText(String.valueOf(prefsBloodHighSugar));
        } else {
            etBloodLowSugar.setInputType(InputType.TYPE_CLASS_NUMBER);

            rbUnitOfBloodSugarMgdL.setChecked(true);
            etBloodLowSugar.setText(String.valueOf((int) (prefsBloodLowSugar * 18)));
            etBloodHighSugar.setText(String.valueOf((int) (prefsBloodHighSugar * 18)));
        }

        if (prefsTimeFormat24h) rbTimeFormat24h.setChecked(true);
        else rbTimeFormat12h.setChecked(true);


        // event on text change in editTexts
        etBloodLowSugar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                preferencesChanged(true, true);
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

        // get firstRunAgreement value
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
        firstRun = sharedPref.getBoolean(KEY_PREFS_FIRST_RUN_AGREEMENT, true);

        // check for first run app
        if (firstRun) {
            preferencesChanged(true, true);
            Intent intent = new Intent(PreferencesActivity.this, AgreementActivity.class);
            startActivity(intent);
        }
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
                alert.setNegativeButton(getString(android.R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //Canceled.
                            }
                        });
                alert.setNeutralButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SQLiteDatabase database = dbHelper.getWritableDatabase();
                        database.delete(DBHelper.TABLE_MEASUREMENTS, null, null);
                        database.close();
                        Toast.makeText(PreferencesActivity.this, getString(
                                R.string.all_measurements_has_been_deleted),
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                alert.show();
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
        prefsDiabetes1Type = sharedPref.getBoolean(KEY_PREFS_DIABETES_1TYPE, diabetes1TypeDefault);
        prefsUnitBloodSugarMmol = sharedPref.getBoolean(KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL,
                unitBloodSugarMmolDefault);
        prefsBloodLowSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_LOW_SUGAR, bloodLowSugarDefault);
        prefsBloodHighSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_HIGH_SUGAR, bloodHighSugarDefault);
        prefsAmountCarbsInBreadUnit = sharedPref.getFloat(KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT,
                amountCarbsInBreadUnitDefault);

        // get saved value for function of interface
        prefsTimeFormat24h = sharedPref.getBoolean(KEY_PREFS_TIME_FORMAT_24H, timeFormat24hDefault);
    }

    // save preferences in sharedPreferences
    public void savePreferences() {
        // get settings object in private mode
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
            prefEditor.putFloat(KEY_PREFS_BLOOD_LOW_SUGAR, tmpBloodHighSugar);
        }

        prefEditor.putFloat(KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT,
                Float.parseFloat(etAmountCarb.getText().toString()));

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

        etAmountCarb.setText((String.valueOf(amountCarbsInBreadUnitDefault)));

        if (diabetes1TypeDefault) rbDiabetesType1.setChecked(true);
        else rbDiabetesType2.setChecked(true);

        if (rbUnitOfBloodSugarMmolL.isChecked()) {
            etBloodLowSugar.setText((String.valueOf(bloodLowSugarDefault)));
            etBloodHighSugar.setText((String.valueOf(bloodHighSugarDefault)));
        } else {
            etBloodLowSugar.setText((String.valueOf((int) (bloodLowSugarDefault * 18))));
            etBloodHighSugar.setText((String.valueOf((int) (bloodHighSugarDefault * 18))));
        }

        if (timeFormat24hDefault) rbTimeFormat24h.setChecked(true);
        else rbTimeFormat12h.setChecked(true);

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
                    bloodLowSugarLowerBound, bloodLowSugarUpperBound));
            etBloodHighSugar.setHint(String.format(Locale.ENGLISH, getString(R.string.from_toF),
                    bloodHighSugarLowerBound, bloodHighSugarUpperBound));
        } else {
            etBloodLowSugar.setHint(String.format(getString(R.string.from_toD),
                    (int) (bloodLowSugarLowerBound * 18), (int) (bloodLowSugarUpperBound * 18)));
            etBloodHighSugar.setHint(String.format(getString(R.string.from_toD),
                    (int) (bloodHighSugarLowerBound * 18), (int) (bloodHighSugarUpperBound * 18)));
        }
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
        if (rbUnitOfBloodSugarMmolL.isChecked()) {
            if (!numberInRange(Float.parseFloat(etBloodLowSugar.getText().toString()),
                    bloodLowSugarLowerBound, bloodLowSugarUpperBound)) {
                setFocus(etBloodLowSugar, true, true, getString(R.string.incorrect_value));
                return false;
            }
            if (!numberInRange(Float.parseFloat(etBloodHighSugar.getText().toString()),
                    bloodHighSugarLowerBound, bloodHighSugarUpperBound)) {
                setFocus(etBloodHighSugar, true, true, getString(R.string.incorrect_value));
                return false;
            }
        } else {
            if (!numberInRange(Float.parseFloat(etBloodLowSugar.getText().toString()),
                    (int) (bloodLowSugarLowerBound * 18), bloodLowSugarUpperBound * 18)) {
                setFocus(etBloodLowSugar, true, true, getString(R.string.incorrect_value));
                return false;
            }
            if (!numberInRange(Float.parseFloat(etBloodHighSugar.getText().toString()),
                    (int) (bloodHighSugarLowerBound * 18), bloodHighSugarUpperBound * 18)) {
                setFocus(etBloodHighSugar, true, true, getString(R.string.incorrect_value));
                return false;
            }
        }
        if (!numberInRange(Float.parseFloat(etAmountCarb.getText().toString()),
                amountCarbsInBreadUnitLowerBound, amountCarbsInBreadUnitUpperBound)) {
            setFocus(etAmountCarb, true, true, getString(R.string.incorrect_value));
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