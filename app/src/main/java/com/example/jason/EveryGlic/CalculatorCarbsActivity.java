package com.example.jason.EveryGlic;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.jason.EveryGlic.MyWorks.createInfoItemInActionBar;
import static com.example.jason.EveryGlic.MyWorks.getStringNumberWithAccuracy;
import static com.example.jason.EveryGlic.MyWorks.isEmpty;
import static com.example.jason.EveryGlic.PreferencesActivity.AMOUNT_CARBS_IN_BREAD_UNIT_DEFAULT;
import static com.example.jason.EveryGlic.PreferencesActivity.KEY_PREFS;
import static com.example.jason.EveryGlic.PreferencesActivity.KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT;

public class CalculatorCarbsActivity extends AppCompatActivity implements View.OnClickListener {
    // keys (CCS - Calculator carbs state)
    private static final String KEY_PREFS_CALC_CARBS_STATE = "calcCarbsState";
    private static final String KEY_PREFS_CCS_CARBS_IN_100_GRAMS = "carbsIn100GramsOfProduct";
    private static final String KEY_PREFS_CCS_GRAMS_IN_PRODUCT = "gramsInProduct";
    private static final String KEY_PREFS_CCS_CARBS_IN_GRAMS_PRODUCT = "carbsInGramsOfProduct";
    private static final String KEY_PREFS_CCS_AMOUNT_BREAD_UNITS = "amountBreadUnits";
    private static final String KEY_PREFS_CCS_STACK = "stack";

    private static final String STACK_DEFAULT_EMPTY_VALUE = "0.0";
    private static final String TAG = "myLog";

    // temporary variables
    float carbsIn100GramsOfProduct;
    float gramsInProduct;
    float carbsInGramsOfProduct;
    float amountBreadUnits;

    String strTemp;

    boolean gramsInProductHasFocus;
    boolean amountBreadUnitsHasFocus;
    boolean carbsIn100GramsOfProductHasFocus;

    // variables for preferences
    float prefsAmountCarbsInBreadUnit;

    // VARS
    // for date and time
    Calendar dateAndTime = Calendar.getInstance();
    Calendar now;
    //
    ArrayList<Float> stackForCalc = new ArrayList<>();
    float stackSum = 0.0f;


    // views declare
    EditText etGramsOfProduct;
    EditText etCarbsIn100GramsOfProduct;
    EditText etCarbsInGramsOfProduct;
    EditText etAmountBreadUnits;

    Button btnClearStack;
    Button btnDeleteLastInStack;
    Button btnAddToStack;

    TextView tvCarbsInBreadUnit;
    TextView tvStack;
    TextView tvStackSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_carbs);
        Log.d(TAG, "CalculatorCarbsActivity, onCreate: ");

        // find views on screen by id
        etGramsOfProduct = findViewById(R.id.etGramsInProduct);
        etCarbsIn100GramsOfProduct = findViewById(R.id.etCarbsInProduct);
        etCarbsInGramsOfProduct = findViewById(R.id.etCarbsInGramsOfProduct);
        etAmountBreadUnits = findViewById(R.id.etAmountBreadUnits);

        btnClearStack = findViewById(R.id.btnClearStack);
        btnDeleteLastInStack = findViewById(R.id.btnDeleteLastInStack);
        btnAddToStack = findViewById(R.id.btnAddToStack);

        tvCarbsInBreadUnit = findViewById(R.id.tvCarbsInBreadUnit);
        tvStack = findViewById(R.id.tvStack);
        tvStackSum = findViewById(R.id.tvStackSum);

        // listeners for views
        btnClearStack.setOnClickListener(this);
        btnDeleteLastInStack.setOnClickListener(this);
        btnAddToStack.setOnClickListener(this);

        // get preferences object
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);

        // get saved value
        prefsAmountCarbsInBreadUnit = sharedPref.getFloat(KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT,
                AMOUNT_CARBS_IN_BREAD_UNIT_DEFAULT);

        // set amount of carbs in bread unit
        strTemp = "1 " + getString(R.string.bu) + " = " + prefsAmountCarbsInBreadUnit + " " + getString(R.string.gr);
        tvCarbsInBreadUnit.setText(strTemp);

        // editTexts listener
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
                    etCarbsIn100GramsOfProduct.setSelection(
                            etCarbsIn100GramsOfProduct.getText().length());
                }

                // for calculate values when changes happen in this editText (has focus)
                if (!isEmpty(etCarbsIn100GramsOfProduct) && !isEmpty(etGramsOfProduct)) {
                    carbsIn100GramsOfProduct = Float.parseFloat(
                            etCarbsIn100GramsOfProduct.getText().toString());
                    gramsInProduct = Float.parseFloat(etGramsOfProduct.getText().toString());

                    carbsInGramsOfProduct = gramsInProduct / 100 * carbsIn100GramsOfProduct;
                    amountBreadUnits = carbsInGramsOfProduct / prefsAmountCarbsInBreadUnit;
                    // need to fix displaying small number by mantissa
                    if (carbsInGramsOfProduct < 0.1) {
                        carbsInGramsOfProduct = 0f;
                    }
                    if (amountBreadUnits < 0.1) {
                        amountBreadUnits = 0f;
                    }

                    // for limit gramsInProduct value in editText
                    if (amountBreadUnits > 99.91) {
                        etGramsOfProduct.setText(String.valueOf(99.9 * prefsAmountCarbsInBreadUnit
                                * 100 / carbsIn100GramsOfProduct + 0.01));
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
        etGramsOfProduct.addTextChangedListener(new TextWatcher() {
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
                        etGramsOfProduct.setText(s);
                        etGramsOfProduct.setSelection(s.length());
                    }
                }

                // 99.9 - msx amount of bread units, 100 - grams
                float gramsInProductMax = (float) (99.9 * prefsAmountCarbsInBreadUnit * 100
                        / carbsIn100GramsOfProduct + 0.01);

                // for limit gramsInProduct value in editText
                if (s.length() != 0 && Float.parseFloat(s) > gramsInProductMax) {
                    etGramsOfProduct.setText(String.valueOf(gramsInProductMax));
                    etGramsOfProduct.setSelection(etGramsOfProduct.getText().length());
                }

                // for calculate values when changes happen in this editText (has focus)
                if (gramsInProductHasFocus) {
                    if (!isEmpty(etGramsOfProduct) && !isEmpty(etCarbsIn100GramsOfProduct)) {
                        gramsInProduct = Float.parseFloat(etGramsOfProduct.getText().toString());
                        carbsIn100GramsOfProduct = Float.parseFloat(
                                etCarbsIn100GramsOfProduct.getText().toString());

                        carbsInGramsOfProduct = gramsInProduct / 100 * carbsIn100GramsOfProduct;
                        amountBreadUnits = carbsInGramsOfProduct / prefsAmountCarbsInBreadUnit;
                        if (carbsInGramsOfProduct < 0.1) {
                            carbsInGramsOfProduct = 0f;
                        }
                        if (amountBreadUnits < 0.1) {
                            amountBreadUnits = 0f;
                        }

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
                        carbsIn100GramsOfProduct = Float.parseFloat(
                                etCarbsIn100GramsOfProduct.getText().toString());
                        if (carbsIn100GramsOfProduct > 0) {
                            amountBreadUnits = Float.parseFloat(
                                    etAmountBreadUnits.getText().toString());

                            gramsInProduct = amountBreadUnits * prefsAmountCarbsInBreadUnit
                                    * 100 / carbsIn100GramsOfProduct;
                            carbsInGramsOfProduct = gramsInProduct / 100 * carbsIn100GramsOfProduct;

                            etGramsOfProduct.setText(String.valueOf(gramsInProduct));
                            etCarbsInGramsOfProduct.setText(String.valueOf(carbsInGramsOfProduct));
                        }
                    } else {
                        etGramsOfProduct.setText(String.valueOf("0.0"));
                        etCarbsInGramsOfProduct.setText(String.valueOf("0.0"));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // event on change focus in editTexts (set cursor to end)
        etGramsOfProduct.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        // set textView stackSum to 0.0 if stack is empty
        if (stackForCalc.isEmpty()) {
            tvStackSum.setText("= 0.0");
        } else {
            // calculate stack's sum and set value to textView stackSum
            for (int i = 0; i < stackForCalc.size(); i++) {
                stackSum += stackForCalc.get(i);
            }
            tvStackSum.setText(String.valueOf(stackSum));
            Log.d(TAG, "CalculatorCarbsActivity, stackGet: " + stackForCalc.get(stackForCalc.size() - 1));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // clear stack and reset textView stackSum to 0.0
            case R.id.btnClearStack:
                if (!stackForCalc.isEmpty()) {
                    stackForCalc.clear();
                    stackSum = 0.0f;

                    // sets to textViews empty values
                    tvStack.setText(R.string.empty);
                    tvStackSum.setText("= 0.0");
                }

                saveStackHistoryState();
                /* Later this place for save stack to history
                *
                *
                *
                * */
                break;

            // remove last value from stack
            case R.id.btnDeleteLastInStack:
                if (!stackForCalc.isEmpty()) {
                    // subtract last stack value from stack sum
                    stackSum -= stackForCalc.get(stackForCalc.size() - 1);
                    tvStackSum.setText(getStringNumberWithAccuracy(String.valueOf(stackSum), 1, '.', false));

                    // remove last value in stack's textView
                    if (stackForCalc.size() == 1) {
                        tvStack.setText(R.string.empty);
                        tvStackSum.setText("= 0.0");
                        Toast.makeText(this, "All value deleted."
                                , Toast.LENGTH_SHORT).show();
                    } else {
                        String stackCurState = tvStack.getText().toString();
                        int lastPlusIndex = stackCurState.lastIndexOf("+");

                        // delete last value from textView
                        // by substring from first character to last sign plus without it
                        tvStack.setText(stackCurState.substring(0, lastPlusIndex - 1));
                    }

                    // remove last stack's value from ArrayList
                    stackForCalc.remove(stackForCalc.size() - 1);
                }

                break;

            // add to stack new value and show stack's sum in textView
            case R.id.btnAddToStack:
//                TimeUnit.SECONDS.sleep(2);
//                Thread.sleep(1000);

                if (!etAmountBreadUnits.getText().toString().isEmpty()) {
                    float curStackVal = Float.parseFloat(etAmountBreadUnits.getText().toString());

                    // add value to stack if value greater than 0.1 (don't to add value 0 to stack)
                    // (because variable float calculation value in memory can be less 0.1)
                    if (!(curStackVal <= 0.1)) {
                        stackSum += curStackVal;
                        stackForCalc.add(curStackVal);

                        // set value with one point accuracy
                        strTemp = "= " + getStringNumberWithAccuracy(String.valueOf(stackSum), 1, '.', false);
                        tvStackSum.setText(strTemp);

                        // if stack's value is first, then set to stack textView without sign plus
                        if (stackForCalc.size() == 1) {
                            // adding round numbers without point
                            if (curStackVal - (int) curStackVal < 0.1) {
                                tvStack.setText(String.valueOf((int) curStackVal));
                            } else {
                                tvStack.setText(String.valueOf(curStackVal));
                            }
                        } else {
                            if (curStackVal - (int) curStackVal > 0.09) {
                                tvStack.append(" + " + curStackVal);
                            } else {
                                tvStack.append(" + " + (int) curStackVal);
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Field Amount of bread units should not be empty."
                            , Toast.LENGTH_SHORT).show();
                }

                myLog("btnAddToStack");
                break;

            default:
                break;
        }
    }

    // my logs for stack
    public void myLog(String caller) {
        if (!stackForCalc.isEmpty()) {
            Log.d(TAG, "\n" + caller + "\nsize(): " + stackForCalc.size() + "\nisEmpty(): "
                    + stackForCalc.isEmpty()
                    + "\nlast stack value: " + stackForCalc.get(stackForCalc.size() - 1));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "CalculatorCarbsActivity, onResume: ");

        // get shared preferences for Calc Carbs
        SharedPreferences sharedPrefState = getSharedPreferences(
                KEY_PREFS_CALC_CARBS_STATE, MODE_PRIVATE);

        // set editText values on resume
        etCarbsIn100GramsOfProduct.setText(sharedPrefState.getString(
                KEY_PREFS_CCS_CARBS_IN_100_GRAMS, "0.0"));
        etGramsOfProduct.setText(sharedPrefState.getString(
                KEY_PREFS_CCS_GRAMS_IN_PRODUCT, "0.0"));
        etCarbsInGramsOfProduct.setText(sharedPrefState.getString(
                KEY_PREFS_CCS_CARBS_IN_GRAMS_PRODUCT, "0.0"));
        etAmountBreadUnits.setText(sharedPrefState.getString(
                KEY_PREFS_CCS_AMOUNT_BREAD_UNITS, "0.0"));

        try {
            // get last state of stack's history
            String stack = sharedPrefState.getString(KEY_PREFS_CCS_STACK, STACK_DEFAULT_EMPTY_VALUE);
            String[] stackArr = stack.split(" ");
            stackSum = 0.0f;

            Log.d(TAG, stack + "\n");

            // check on empty stack history
            if (stack.equals(STACK_DEFAULT_EMPTY_VALUE)) {
                tvStack.setText(R.string.empty);
            } else {
                // fill stack Array and calculate stack's sum
                // i+2 because stack value alternate with plus sign (stackArr={1,+,2,+,3})
                for (int i = 0; i < stackArr.length; i = i + 2) {
                    stackForCalc.add(Float.parseFloat(stackArr[i]));
                    stackSum += Float.parseFloat(stackArr[i]);
                    //            Log.d(TAG, stackArr[i] + "\n");
                }
                tvStack.setText(stack);
                strTemp = "= " + getStringNumberWithAccuracy(String.valueOf(stackSum), 1, '.', false);
                tvStackSum.setText(strTemp);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        myLog("btnAddToStack");
        etCarbsIn100GramsOfProduct.requestFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "CalculatorCarbsActivity, onPause: ");

        SharedPreferences sharedPrefState = getSharedPreferences(
                KEY_PREFS_CALC_CARBS_STATE, MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefStateEditor = sharedPrefState.edit();

        // save editText values on pause
        // put editTexts state(their value) to shared preferences
        sharedPrefStateEditor.putString(
                KEY_PREFS_CCS_CARBS_IN_100_GRAMS, etCarbsIn100GramsOfProduct.getText().toString());
        sharedPrefStateEditor.putString(
                KEY_PREFS_CCS_GRAMS_IN_PRODUCT, etGramsOfProduct.getText().toString());
        sharedPrefStateEditor.putString(
                KEY_PREFS_CCS_CARBS_IN_GRAMS_PRODUCT, etCarbsInGramsOfProduct.getText().toString());
        sharedPrefStateEditor.putString(
                KEY_PREFS_CCS_AMOUNT_BREAD_UNITS, etAmountBreadUnits.getText().toString());

        // apply shared preferences changes
        sharedPrefStateEditor.apply();

        saveStackHistoryState();
    }

    // save stack history state to shared prefs
    protected void saveStackHistoryState() {
        SharedPreferences sharedPrefState = getSharedPreferences(
                KEY_PREFS_CALC_CARBS_STATE, MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefStateEditor = sharedPrefState.edit();

        if (!stackForCalc.isEmpty()) {
            // build string to further split on elements for ArrayList
            StringBuilder strStackTemp = new StringBuilder();
            for (Float curStackVal : stackForCalc) {
                strStackTemp.append(curStackVal).append(",");
            }
            // save stack history to shared prefs
            sharedPrefStateEditor.putString(KEY_PREFS_CCS_STACK, tvStack.getText().toString());
        } else {
            sharedPrefStateEditor.putString(KEY_PREFS_CCS_STACK, STACK_DEFAULT_EMPTY_VALUE);
        }

        // apply shared preferences changes
        sharedPrefStateEditor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        createInfoItemInActionBar(menu);
        return super.onCreateOptionsMenu(menu);
    }

    // for save fields values
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}