package ru.opalevapps.EveryGlic.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.opalevapps.EveryGlic.MyUtill
import ru.opalevapps.EveryGlic.R
import java.util.*

class CalculatorCarbsActivity : AppCompatActivity(), View.OnClickListener {
    // temporary variables
    var carbsIn100GramsOfProduct = 0f
    var gramsInProduct = 0f
    var carbsInGramsOfProduct = 0f
    var amountBreadUnits = 0f
    private var strTemp: String? = null
    var gramsInProductHasFocus = false
    var amountBreadUnitsHasFocus = false
    private var carbsIn100GramsOfProductHasFocus = false

    // variables for preferences
    var prefsAmountCarbsInBreadUnit = 0f

    // VARS
    // for date and time
    var dateAndTime = Calendar.getInstance()
    var now: Calendar? = null

    //
    private var stackForCalc = ArrayList<Float>()
    private var stackSum = 0.0f

    // views declare
    lateinit var etGramsOfProduct: EditText
    lateinit var etCarbsIn100GramsOfProduct: EditText
    lateinit var etCarbsInGramsOfProduct: EditText
    lateinit var etAmountBreadUnits: EditText
    private lateinit var btnClearStack: Button
    private lateinit var btnDeleteLastInStack: Button
    private lateinit var btnAddToStack: Button
    private lateinit var tvCarbsInBreadUnit: TextView
    private lateinit var tvStack: TextView
    private lateinit var tvStackSum: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator_carbs)

        initViews()

        // get preferences object
        val sharedPref = getSharedPreferences(PreferencesActivity.KEY_PREFS, MODE_PRIVATE)

        // get saved value
        prefsAmountCarbsInBreadUnit = sharedPref.getFloat(
            PreferencesActivity.KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT,
            PreferencesActivity.AMOUNT_CARBS_IN_BREAD_UNIT_DEFAULT
        )

        // set amount of carbs in bread unit
        strTemp =
            "1 ${getString(R.string.bu)} = $prefsAmountCarbsInBreadUnit ${getString(R.string.gr)}"
        tvCarbsInBreadUnit.text = strTemp

        // set textView stackSum to 0.0 if stack is empty
        if (stackForCalc.isEmpty()) {
            tvStackSum.text = "= 0.0"
        } else {
            // calculate stack's sum and set value to textView stackSum
            for (i in stackForCalc.indices) {
                stackSum += stackForCalc[i]
            }
            tvStackSum.text = stackSum.toString()
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
            R.id.btnClearStack -> {
                if (stackForCalc.isNotEmpty()) {
                    stackForCalc.clear()
                    stackSum = 0.0f

                    // sets to textViews empty values
                    tvStack.setText(R.string.empty)
                    tvStackSum.text = "= 0.0"
                }
                saveStackHistoryState()
                tvStack.scrollTo(0, 0)
            }
            R.id.btnDeleteLastInStack -> {
                if (stackForCalc.isNotEmpty()) {
                    // subtract last stack value from stack sum
                    stackSum -= stackForCalc[stackForCalc.size - 1]
                    strTemp = "= " + MyUtill.getStringNumberWithAccuracy(
                        stackSum.toString(),
                        1,
                        '.',
                        false
                    )
                    tvStackSum.text = strTemp

                    // remove last value in stack's textView
                    if (stackForCalc.size == 1) {
                        tvStack.setText(R.string.empty)
                        tvStackSum.text = "= 0.0"
                        Toast.makeText(
                            this, R.string.all_value_deleted, Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val stackCurState = tvStack.text.toString()
                        val lastPlusIndex = stackCurState.lastIndexOf("+")

                        // delete last value from textView
                        // by substring from first character to last sign plus without it
                        tvStack.text = stackCurState.substring(0, lastPlusIndex - 1)
                    }

                    // remove last stack's value from ArrayList
                    stackForCalc.removeAt(stackForCalc.size - 1)
                }
                MyUtill.scrollToBottomOfTextView(tvStack)
            }
            R.id.btnAddToStack -> {
                //                TimeUnit.SECONDS.sleep(2);
//                Thread.sleep(1000);
                if (etAmountBreadUnits.text.toString().isNotEmpty()) {
                    val curStackVal = etAmountBreadUnits.text.toString().toFloat()

                    // add value to stack if value greater than 0.1 (don't to add value 0 to stack)
                    // (because variable float calculation value in memory can be less 0.1)
                    if (curStackVal > 0.1) {
                        stackSum += curStackVal
                        stackForCalc.add(curStackVal)

                        // set value with one point accuracy
                        strTemp = "= " + MyUtill.getStringNumberWithAccuracy(
                            stackSum.toString(),
                            1,
                            '.',
                            false
                        )
                        tvStackSum.text = strTemp

                        // if stack's value is first, then set to stack textView without sign plus
                        if (stackForCalc.size == 1) {
                            // adding round numbers without point
                            if ((curStackVal - curStackVal.toInt()) < 0.1) {
                                tvStack.text = "${curStackVal.toInt()}"
                            } else {
                                tvStack.text = "$curStackVal"
                            }
                        } else {
                            if (curStackVal - curStackVal.toInt() > 0.09) {
                                tvStack.append(" + $curStackVal")
                            } else {
                                tvStack.append(" + ${curStackVal.toInt()}")
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Field Amount of bread units should not be empty.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                /*// for scrollable textView
                tvStack.setMovementMethod(new ScrollingMovementMethod());*/
                MyUtill.scrollToBottomOfTextView(tvStack)
            }
            else -> {}
        }
    }

    override fun onResume() {
        super.onResume()

        // get shared preferences for Calc Carbs
        val sharedPrefState = getSharedPreferences(
            KEY_PREFS_CALC_CARBS_STATE, MODE_PRIVATE
        )

        // set editText values on resume
        etCarbsIn100GramsOfProduct.setText(
            sharedPrefState.getString(KEY_PREFS_CCS_CARBS_IN_100_GRAMS, "0.0")
        )
        etGramsOfProduct.setText(
            sharedPrefState.getString(KEY_PREFS_CCS_GRAMS_IN_PRODUCT, "0.0")
        )
        etCarbsInGramsOfProduct.setText(
            sharedPrefState.getString(KEY_PREFS_CCS_CARBS_IN_GRAMS_PRODUCT, "0.0")
        )
        etAmountBreadUnits.setText(
            sharedPrefState.getString(KEY_PREFS_CCS_AMOUNT_BREAD_UNITS, "0.0")
        )
        try {
            // get last state of stack's history
            val stack = sharedPrefState.getString(KEY_PREFS_CCS_STACK, STACK_DEFAULT_EMPTY_VALUE)
            val stackArr = stack!!.split(" ".toRegex()).toTypedArray()
            stackSum = 0.0f

            // check on empty stack history
            if (stack == STACK_DEFAULT_EMPTY_VALUE) {
                tvStack.setText(R.string.empty)
            } else {
                // fill stack Array and calculate stack's sum
                // i+2 because stack value alternate with plus sign (stackArr={1,+,2,+,3})
                var i = 0
                while (i < stackArr.size) {
                    stackForCalc.add(stackArr[i].toFloat())
                    stackSum += stackArr[i].toFloat()
                    i += 2
                }
                tvStack.text = stack
                strTemp =
                    "= " + MyUtill.getStringNumberWithAccuracy(stackSum.toString(), 1, '.', false)
                tvStackSum.text = strTemp
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

        etCarbsIn100GramsOfProduct.requestFocus()

        // for scrollable textView
        tvStack.movementMethod = ScrollingMovementMethod()
        // scroll to end of tvStack textView
        MyUtill.scrollToBottomOfTextView(tvStack)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "CalculatorCarbsActivity, onPause: ")
        val sharedPrefState = getSharedPreferences(KEY_PREFS_CALC_CARBS_STATE, MODE_PRIVATE)
        val sharedPrefStateEditor = sharedPrefState.edit()

        // save editText values on pause
        // put editTexts state(their value) to shared preferences
        sharedPrefStateEditor.putString(
            KEY_PREFS_CCS_CARBS_IN_100_GRAMS, etCarbsIn100GramsOfProduct.text.toString()
        )
        sharedPrefStateEditor.putString(
            KEY_PREFS_CCS_GRAMS_IN_PRODUCT, etGramsOfProduct.text.toString()
        )
        sharedPrefStateEditor.putString(
            KEY_PREFS_CCS_CARBS_IN_GRAMS_PRODUCT, etCarbsInGramsOfProduct.text.toString()
        )
        sharedPrefStateEditor.putString(
            KEY_PREFS_CCS_AMOUNT_BREAD_UNITS, etAmountBreadUnits.text.toString()
        )

        // apply shared preferences changes
        sharedPrefStateEditor.apply()
        saveStackHistoryState()
    }

    // save stack history state to shared prefs
    private fun saveStackHistoryState() {
        val sharedPrefState = getSharedPreferences(
            KEY_PREFS_CALC_CARBS_STATE, MODE_PRIVATE
        )
        val sharedPrefStateEditor = sharedPrefState.edit()
        if (stackForCalc.isNotEmpty()) {
            // build string to further split on elements for ArrayList
            val strStackTemp = StringBuilder()
            for (curStackVal in stackForCalc) {
                strStackTemp.append(curStackVal).append(",")
            }
            // save stack history to shared prefs
            sharedPrefStateEditor.putString(KEY_PREFS_CCS_STACK, tvStack.text.toString())
        } else {
            sharedPrefStateEditor.putString(KEY_PREFS_CCS_STACK, STACK_DEFAULT_EMPTY_VALUE)
        }

        // apply shared preferences changes
        sharedPrefStateEditor.apply()
    }

    // for save fields values
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    // my logs for stack
    fun myLog(caller: String) {
        if (stackForCalc.isNotEmpty()) {
            Log.d(
                TAG, """
     $caller
     size(): ${stackForCalc.size}
     isEmpty(): ${stackForCalc.isEmpty()}
     last stack value: ${stackForCalc[stackForCalc.size - 1]}
     """.trimIndent()
            )
        }
    }

    // initialize views on screen and their listening
    private fun initViews() {
        // find views on screen by id
        etGramsOfProduct = findViewById(R.id.etGramsInProduct)
        etCarbsIn100GramsOfProduct = findViewById(R.id.etCarbsIn100GramsOfProduct)
        etCarbsInGramsOfProduct = findViewById(R.id.etCarbsInGramsOfProduct)
        etAmountBreadUnits = findViewById(R.id.etAmountBreadUnits)
        btnClearStack = findViewById(R.id.btnClearStack)
        btnDeleteLastInStack = findViewById(R.id.btnDeleteLastInStack)
        btnAddToStack = findViewById(R.id.btnAddToStack)
        tvCarbsInBreadUnit = findViewById(R.id.tvCarbsInBreadUnit)
        tvStack = findViewById(R.id.tvStack)
        tvStackSum = findViewById(R.id.tvStackSum)

        // listeners for views
        btnClearStack.setOnClickListener(this)
        btnDeleteLastInStack.setOnClickListener(this)
        btnAddToStack.setOnClickListener(this)

        // editTexts listener
        etCarbsIn100GramsOfProduct.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                var s = charSequence.toString()

                // set one point accuracy for editText value
                if (s.contains(".")) {
                    s = MyUtill.getStringNumberWithAccuracy(s, 1, '.', false)
                    if (s.length != charSequence.toString().length) {
                        etCarbsIn100GramsOfProduct.setText(s)
                        etCarbsIn100GramsOfProduct.setSelection(s.length)
                    }
                }

                // for limit carbsIn100GramsOfProduct value in editText
                if (s.isNotEmpty() && s.toFloat() > 100.01) {
                    etCarbsIn100GramsOfProduct.setText(R.string._100_0)
                    etCarbsIn100GramsOfProduct.setSelection(
                        etCarbsIn100GramsOfProduct.text.length
                    )
                }

                // for calculate values when changes happen in this editText (has focus)
                if (!MyUtill.isEmpty(etCarbsIn100GramsOfProduct) && !MyUtill.isEmpty(
                        etGramsOfProduct
                    )
                ) {
                    carbsIn100GramsOfProduct =
                        etCarbsIn100GramsOfProduct.text.toString().toFloat()
                    gramsInProduct = etGramsOfProduct.text.toString().toFloat()
                    carbsInGramsOfProduct = gramsInProduct / 100 * carbsIn100GramsOfProduct
                    amountBreadUnits = carbsInGramsOfProduct / prefsAmountCarbsInBreadUnit
                    // need to fix displaying small number by mantissa
                    if (carbsInGramsOfProduct < 0.1) {
                        carbsInGramsOfProduct = 0f
                    }
                    if (amountBreadUnits < 0.1) {
                        amountBreadUnits = 0f
                    }

                    // for limit gramsInProduct value in editText
                    val tmp =
                        ((99.9 * prefsAmountCarbsInBreadUnit * 100) / carbsIn100GramsOfProduct + 0.01).toString()
                    if (amountBreadUnits > 99.91) {
                        etGramsOfProduct.setText(tmp)
                    }
                    etCarbsInGramsOfProduct.setText(carbsInGramsOfProduct.toString())
                    etAmountBreadUnits.setText(amountBreadUnits.toString())
                } else {
                    etCarbsInGramsOfProduct.setText("0.0")
                    etAmountBreadUnits.setText("0.0")
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        etGramsOfProduct.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                var s = charSequence.toString()

                // set one point accuracy for editText value
                if (s.contains(".")) {
                    s = MyUtill.getStringNumberWithAccuracy(s, 1, '.', false)
                    if (s.length != charSequence.toString().length) {
                        etGramsOfProduct.setText(s)
                        etGramsOfProduct.setSelection(s.length)
                    }
                }

                // 99.9 - msx amount of bread units, 100 - grams
                val gramsInProductMax = (99.9 * prefsAmountCarbsInBreadUnit * 100
                        / carbsIn100GramsOfProduct + 0.01).toFloat()

                // for limit gramsInProduct value in editText
                if (s.isNotEmpty() && s.toFloat() > gramsInProductMax) {
                    etGramsOfProduct.setText(gramsInProductMax.toString())
                    etGramsOfProduct.setSelection(etGramsOfProduct.text.length)
                }

                // for calculate values when changes happen in this editText (has focus)
                if (gramsInProductHasFocus) {
                    if (!MyUtill.isEmpty(etGramsOfProduct) && !MyUtill.isEmpty(
                            etCarbsIn100GramsOfProduct
                        )
                    ) {
                        gramsInProduct = etGramsOfProduct.text.toString().toFloat()
                        carbsIn100GramsOfProduct =
                            etCarbsIn100GramsOfProduct.text.toString().toFloat()
                        carbsInGramsOfProduct = gramsInProduct / 100 * carbsIn100GramsOfProduct
                        amountBreadUnits = carbsInGramsOfProduct / prefsAmountCarbsInBreadUnit
                        if (carbsInGramsOfProduct < 0.1) {
                            carbsInGramsOfProduct = 0f
                        }
                        if (amountBreadUnits < 0.1) {
                            amountBreadUnits = 0f
                        }
                        etCarbsInGramsOfProduct.setText(carbsInGramsOfProduct.toString())
                        etAmountBreadUnits.setText(amountBreadUnits.toString())
                    } else {
                        etCarbsInGramsOfProduct.setText("0.0")
                        etAmountBreadUnits.setText("0.0")
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        etCarbsInGramsOfProduct.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                var s = charSequence.toString()
                // set one point accuracy for editText value
                if (s.contains(".")) {
                    s = MyUtill.getStringNumberWithAccuracy(s, 1, '.', false)
                    if (s.length != charSequence.toString().length) {
                        etCarbsInGramsOfProduct.setText(s)
                        etCarbsInGramsOfProduct.setSelection(s.length)
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        etAmountBreadUnits.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                var s = charSequence.toString()

                // set one point accuracy for editText value
                if (s.contains(".")) {
                    s = MyUtill.getStringNumberWithAccuracy(s, 1, '.', false)
                    if (s.length != charSequence.toString().length) {
                        etAmountBreadUnits.setText(s)
                        etAmountBreadUnits.setSelection(s.length)
                    }
                }

                // for limit gramsInProduct value in editText
                if (s.isNotEmpty() && s.toFloat() > 99.91) {
                    etAmountBreadUnits.setText(R.string._99_9)
                    etAmountBreadUnits.setSelection(etAmountBreadUnits.text.length)
                }

                // for calculate values when changes happen in this editText (has focus)
                if (amountBreadUnitsHasFocus) {
                    if (!MyUtill.isEmpty(etAmountBreadUnits) && !MyUtill.isEmpty(
                            etCarbsIn100GramsOfProduct
                        )
                    ) {
                        carbsIn100GramsOfProduct =
                            etCarbsIn100GramsOfProduct.text.toString().toFloat()
                        if (carbsIn100GramsOfProduct > 0) {
                            amountBreadUnits =
                                etAmountBreadUnits.text.toString().toFloat()
                            gramsInProduct = (amountBreadUnits * prefsAmountCarbsInBreadUnit
                                    * 100) / carbsIn100GramsOfProduct
                            carbsInGramsOfProduct = gramsInProduct / 100 * carbsIn100GramsOfProduct
                            etGramsOfProduct.setText(gramsInProduct.toString())
                            etCarbsInGramsOfProduct.setText(carbsInGramsOfProduct.toString())
                        }
                    } else {
                        etGramsOfProduct.setText("0.0")
                        etCarbsInGramsOfProduct.setText("0.0")
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        // event on change focus in editTexts (set cursor to end)
        etGramsOfProduct.setOnFocusChangeListener { view: View?, hasFocus: Boolean ->
            gramsInProductHasFocus = hasFocus
        }
        etAmountBreadUnits.setOnFocusChangeListener { view: View?, hasFocus: Boolean ->
            amountBreadUnitsHasFocus = hasFocus
        }
        etCarbsIn100GramsOfProduct.setOnFocusChangeListener { view: View?, hasFocus: Boolean ->
            carbsIn100GramsOfProductHasFocus = hasFocus
        }
    }

    companion object {
        // keys (CCS - Calculator carbs state)
        private const val KEY_PREFS_CALC_CARBS_STATE = "calcCarbsState"
        private const val KEY_PREFS_CCS_CARBS_IN_100_GRAMS = "carbsIn100GramsOfProduct"
        private const val KEY_PREFS_CCS_GRAMS_IN_PRODUCT = "gramsInProduct"
        private const val KEY_PREFS_CCS_CARBS_IN_GRAMS_PRODUCT = "carbsInGramsOfProduct"
        private const val KEY_PREFS_CCS_AMOUNT_BREAD_UNITS = "amountBreadUnits"
        private const val KEY_PREFS_CCS_STACK = "stack"
        private const val STACK_DEFAULT_EMPTY_VALUE = "0.0"
        private const val TAG = "myLog"
    }
}