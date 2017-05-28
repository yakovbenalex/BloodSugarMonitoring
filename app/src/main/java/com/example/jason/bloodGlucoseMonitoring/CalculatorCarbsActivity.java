package com.example.jason.bloodGlucoseMonitoring;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static com.example.jason.bloodGlucoseMonitoring.MyWorks.getStringNumberWithAccuracy;
import static com.example.jason.bloodGlucoseMonitoring.MyWorks.isEmpty;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.amountCarbsInBreadUnitDefault;

public class CalculatorCarbsActivity extends AppCompatActivity {
    // keys
    private static final String KEY_PREFS_CALC_CARBS_STATE = "calcCarbsState";
    private static final String KEY_PREFS_CCS_CARBS_IN_100_GRAMS = "carbsIn100GramsOfProduct";
    private static final String KEY_PREFS_CCS_GRAMS_IN_PRODUCT = "gramsInProduct";
    private static final String KEY_PREFS_CCS_CARBS_IN_GRAMS_PRODUCT = "carbsInGramsOfProduct";
    private static final String KEY_PREFS_CCS_AMOUNT_BREAD_UNITS = "amountBreadUnits";

    // temporary variables
    float carbsIn100GramsOfProduct;
    float gramsInProduct;
    float carbsInGramsOfProduct;
    float amountBreadUnits;

    boolean gramsInProductHasFocus;
    boolean amountBreadUnitsHasFocus;
    boolean carbsIn100GramsOfProductHasFocus;

    // variables for preferences
    float prefsAmountCarbsInBreadUnit;

    // views declare
    EditText etGramsInProduct;
    EditText etCarbsIn100GramsOfProduct;
    EditText etCarbsInGramsOfProduct;
    EditText etAmountBreadUnits;

    TextView tvCarbsInBreadUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_carbs);

        // find views on screen by id
        etGramsInProduct = (EditText) findViewById(R.id.etGramsInProduct);
        etCarbsIn100GramsOfProduct = (EditText) findViewById(R.id.etCarbsInProduct);
        etCarbsInGramsOfProduct = (EditText) findViewById(R.id.etCarbsInGramsOfProduct);
        etAmountBreadUnits = (EditText) findViewById(R.id.etAmountBreadUnits);

        tvCarbsInBreadUnit = (TextView) findViewById(R.id.tvCarbsInBreadUnit);

        // get preferences object
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);

        // get saved value
        prefsAmountCarbsInBreadUnit = sharedPref.getFloat(KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT, amountCarbsInBreadUnitDefault);

        // set amount of carbs in bread unit
        tvCarbsInBreadUnit.setText("1 BU = " + prefsAmountCarbsInBreadUnit + " gr");

        // editTexts listener
        etGramsInProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = String.valueOf(charSequence);

                // set one point accuracy for editText value
                if (s.contains(".")) {
                    s = getStringNumberWithAccuracy(s, 1, '.', false);
                    if (s.length() != charSequence.toString().length()) {
                        etGramsInProduct.setText(s);
                        etGramsInProduct.setSelection(s.length());
                    }
                }

                // 99.9 - msx amount of bread units, 100 - grams
                float gramsInProductMax = (float) (99.9 * prefsAmountCarbsInBreadUnit * 100 / carbsIn100GramsOfProduct + 0.01);

                // for limit gramsInProduct value in editText
                if (s.length() != 0 && Float.parseFloat(s) > gramsInProductMax) {
                    etGramsInProduct.setText(String.valueOf(gramsInProductMax));
                    etGramsInProduct.setSelection(etGramsInProduct.getText().length());
                }

                // for calculate values when changes happen in this editText (has focus)
                if (gramsInProductHasFocus) {
                    if (!isEmpty(etGramsInProduct) && !isEmpty(etCarbsIn100GramsOfProduct)) {
                        gramsInProduct = Float.parseFloat(etGramsInProduct.getText().toString());
                        carbsIn100GramsOfProduct = Float.parseFloat(etCarbsIn100GramsOfProduct.getText().toString());

                        carbsInGramsOfProduct = gramsInProduct / 100 * carbsIn100GramsOfProduct;
                        amountBreadUnits = carbsInGramsOfProduct / prefsAmountCarbsInBreadUnit;

                        etCarbsInGramsOfProduct.setText(String.valueOf(carbsInGramsOfProduct));
                        etAmountBreadUnits.setText(String.valueOf(amountBreadUnits));

                    } else {
                        etCarbsInGramsOfProduct.setText(String.valueOf("0.0"));
                        etAmountBreadUnits.setText(String.valueOf("0.0"));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        etCarbsInGramsOfProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = String.valueOf(charSequence);
                // set one point accuracy for editText value
                if (s.contains(".")) {
                    s = getStringNumberWithAccuracy(s, 1, '.', false);
                    if (s.length() != charSequence.toString().length()) {
                        etCarbsInGramsOfProduct.setText(s);
                        etCarbsInGramsOfProduct.setSelection(s.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        etAmountBreadUnits.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = String.valueOf(charSequence);

                // set one point accuracy for editText value
                if (s.contains(".")) {
                    s = getStringNumberWithAccuracy(s, 1, '.', false);
                    if (s.length() != charSequence.toString().length()) {
                        etAmountBreadUnits.setText(s);
                        etAmountBreadUnits.setSelection(s.length());
                    }
                }

                // for limit gramsInProduct value in editText
                if (s.length() != 0 && Float.parseFloat(s) > 99.91) {
                    etAmountBreadUnits.setText(R.string._99_9);
                    etAmountBreadUnits.setSelection(etAmountBreadUnits.getText().length());
                }

                // for calculate values when changes happen in this editText (has focus)
                if (amountBreadUnitsHasFocus) {
                    if (!isEmpty(etAmountBreadUnits) && !isEmpty(etCarbsIn100GramsOfProduct)) {
                        carbsIn100GramsOfProduct = Float.parseFloat(etCarbsIn100GramsOfProduct.getText().toString());
                        if (carbsIn100GramsOfProduct > 0) {
                            amountBreadUnits = Float.parseFloat(etAmountBreadUnits.getText().toString());

                            gramsInProduct = amountBreadUnits * prefsAmountCarbsInBreadUnit * 100 / carbsIn100GramsOfProduct;
                            carbsInGramsOfProduct = gramsInProduct / 100 * carbsIn100GramsOfProduct;

                            etGramsInProduct.setText(String.valueOf(gramsInProduct));
                            etCarbsInGramsOfProduct.setText(String.valueOf(carbsInGramsOfProduct));
                        }
                    } else {
                        etGramsInProduct.setText(String.valueOf("0.0"));
                        etCarbsInGramsOfProduct.setText(String.valueOf("0.0"));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        etCarbsIn100GramsOfProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = String.valueOf(charSequence);

                // set one point accuracy for editText value
                if (s.contains(".")) {
                    s = getStringNumberWithAccuracy(s, 1, '.', false);
                    if (s.length() != charSequence.toString().length()) {
                        etCarbsIn100GramsOfProduct.setText(s);
                        etCarbsIn100GramsOfProduct.setSelection(s.length());
                    }
                }

                // for limit carbsIn100GramsOfProduct value in editText
                if (s.length() != 0 && Float.parseFloat(s) > 100.01) {
                    etCarbsIn100GramsOfProduct.setText(R.string._100_0);
                    etCarbsIn100GramsOfProduct.setSelection(etCarbsIn100GramsOfProduct.getText().length());
                }

                // for calculate values when changes happen in this editText (has focus)
                if (!isEmpty(etCarbsIn100GramsOfProduct) && !isEmpty(etGramsInProduct)) {
                    carbsIn100GramsOfProduct = Float.parseFloat(etCarbsIn100GramsOfProduct.getText().toString());
                    gramsInProduct = Float.parseFloat(etGramsInProduct.getText().toString());

                    carbsInGramsOfProduct = gramsInProduct / 100 * carbsIn100GramsOfProduct;
                    amountBreadUnits = carbsInGramsOfProduct / prefsAmountCarbsInBreadUnit;

                    // for limit gramsInProduct value in editText
                    if (amountBreadUnits > 99.91) {
                        etGramsInProduct.setText(String.valueOf(99.9 * prefsAmountCarbsInBreadUnit * 100 / carbsIn100GramsOfProduct + 0.01));
                    }

                    etCarbsInGramsOfProduct.setText(String.valueOf(carbsInGramsOfProduct));
                    etAmountBreadUnits.setText(String.valueOf(amountBreadUnits));
                } else {
                    etCarbsInGramsOfProduct.setText(String.valueOf("0.0"));
                    etAmountBreadUnits.setText(String.valueOf("0.0"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // event on change focus in editTexts (set cursor to end)
        etGramsInProduct.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                gramsInProductHasFocus = hasFocus;
            }
        });
        etAmountBreadUnits.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                amountBreadUnitsHasFocus = hasFocus;
            }
        });
        etCarbsIn100GramsOfProduct.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                carbsIn100GramsOfProductHasFocus = hasFocus;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // set editText values on resume
        SharedPreferences sharedPrefState = getSharedPreferences(KEY_PREFS_CALC_CARBS_STATE, MODE_PRIVATE);

        etCarbsIn100GramsOfProduct.setText(sharedPrefState.getString(KEY_PREFS_CCS_CARBS_IN_100_GRAMS, "0.0"));
        etGramsInProduct.setText(sharedPrefState.getString(KEY_PREFS_CCS_GRAMS_IN_PRODUCT, "0.0"));
        etCarbsInGramsOfProduct.setText(sharedPrefState.getString(KEY_PREFS_CCS_CARBS_IN_GRAMS_PRODUCT, "0.0"));
        etAmountBreadUnits.setText(sharedPrefState.getString(KEY_PREFS_CCS_AMOUNT_BREAD_UNITS, "0.0"));

        etCarbsIn100GramsOfProduct.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // save editText values on pause
        SharedPreferences sharedPrefState = getSharedPreferences(KEY_PREFS_CALC_CARBS_STATE, MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefStateEditor = sharedPrefState.edit();

        sharedPrefStateEditor.putString(KEY_PREFS_CCS_CARBS_IN_100_GRAMS, etCarbsIn100GramsOfProduct.getText().toString());
        sharedPrefStateEditor.putString(KEY_PREFS_CCS_GRAMS_IN_PRODUCT, etGramsInProduct.getText().toString());
        sharedPrefStateEditor.putString(KEY_PREFS_CCS_CARBS_IN_GRAMS_PRODUCT, etCarbsInGramsOfProduct.getText().toString());
        sharedPrefStateEditor.putString(KEY_PREFS_CCS_AMOUNT_BREAD_UNITS, etAmountBreadUnits.getText().toString());
        sharedPrefStateEditor.apply();
    }

    // for save fields values
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
