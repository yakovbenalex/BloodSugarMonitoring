package ru.opalevapps.EveryGlic.ui;

import static ru.opalevapps.EveryGlic.ui.PreferencesActivity.KEY_PREFS;
import static ru.opalevapps.EveryGlic.ui.PreferencesActivity.KEY_PREFS_TIME_FORMAT_24H;
import static ru.opalevapps.EveryGlic.ui.PreferencesActivity.KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL;
import static ru.opalevapps.EveryGlic.ui.PreferencesActivity.UNIT_BLOOD_SUGAR_MMOL_DEFAULT;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ru.opalevapps.EveryGlic.MyWorks;
import ru.opalevapps.EveryGlic.R;
import ru.opalevapps.EveryGlic.db.DBHelper;

public class AddOrChangeMeasurementActivity extends AppCompatActivity implements View.OnClickListener {
    // limit to back starts on 1970 (this is enough)
    private static final int yearLimitLowerBound = 1970;

    // Sugar range limit
    private static final float bloodSugarLimitLow = 0.3f;
    private static final float bloodSugarLimitHigh = 55.5f;

    // for date and time
    Calendar dateAndTime = Calendar.getInstance();
    Calendar now;

    String DATE_FORMAT = "dd/MM/yy - HH:mm";
    SimpleDateFormat sdf;

    // id record
    int idRec;
    // choose add or update measurement
    boolean updateRec = false;

    // prefs vars
    boolean prefsTimeFormat24h;
    boolean prefsUnitBloodSugarMmol;

    // views
    Button btnChooseDate;
    Button btnChooseTime;
    Button btnSaveMeasurement;
    Button btnDeleteCurMeasurements;

    // TEST ---------------
    Button btnAddTestData;

    EditText etBloodSugarMeasurement;
    EditText etComment;

    TextView tvDateAndTime;
    TextView tvUnitBloodSugar;

    // SQLite database
    DBHelper dbHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_measurement);

        dbHelper = new DBHelper(this);

        // get id record from Activity intent
        idRec = (int) getIntent().getLongExtra("idRec", -1);

        // get shared preferences object
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
        prefsTimeFormat24h = sharedPref.getBoolean(KEY_PREFS_TIME_FORMAT_24H, true);
        prefsUnitBloodSugarMmol = sharedPref.getBoolean(KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL, UNIT_BLOOD_SUGAR_MMOL_DEFAULT);

        // date and time format display
        if (prefsTimeFormat24h) {
            DATE_FORMAT = "dd/MM/yy - " + "HH:mm";
        } else {
            DATE_FORMAT = "dd/MM/yy - " + "h:mm a";
        }
        sdf = new SimpleDateFormat(DATE_FORMAT);

        initViews();

        // if editing measurement
        if (idRec != -1) {
            // enable delete button
            btnDeleteCurMeasurements.setEnabled(true);
            setTitle(getString(R.string.edit_measurement));

            // load measurement
            loadRecords();
        } else {
//            btnDeleteCurMeasurements.setEnabled(false);
            etBloodSugarMeasurement.requestFocus();
        }

        // display date and time
        setCaptionDateTime();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MyWorks.createInfoItemInActionBar(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        MyWorks.parseMenuItemInfo(this, item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // TEST ------
            case R.id.btnAddTestData:
                int testDataCount = 50;

                // choose update or add measurement
                updateRec = false;

                for (int i = 0; i < testDataCount; i++) {
                    // check date to more than current and reset if so
                    now = Calendar.getInstance();

                    float leftLimit = 3.5f;
                    float rightLimit = 15.5f;
                    float newMeasure = leftLimit + (float) (Math.random() * (rightLimit - leftLimit));
                    String newMeasureStr = Float.toString(newMeasure);
                    newMeasure = Float.parseFloat(newMeasureStr.substring(0, newMeasureStr.indexOf(".") + 1));

                    long dateLeftLimit = 1L;
                    long dateRightLimit = 2L * 30 * 24 * 60 * 60;
                    long dateOffset = dateLeftLimit + (long) (Math.random() * (dateRightLimit - dateLeftLimit));
                    long date = dateAndTime.getTimeInMillis() / 1000 - dateOffset;
                    String comment = "Test data " + i + " - " + newMeasure;

                    // save measurement or update if updateRec is true
                    writeRecord(newMeasure, date, comment, updateRec);

//                    finish();
                }
                Toast.makeText(this, testDataCount + " test data added", Toast.LENGTH_SHORT).show();
                break;

            // show dialog to choose date
            case R.id.btnChooseDate:
                new DatePickerDialog(this, datePickerDialog,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH))
                        .show();
                break;

            // show dialog to choose time
            case R.id.btnChooseTime:
                new TimePickerDialog(this, timePickerDialog,
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE),
                        prefsTimeFormat24h)
                        .show();
                break;

            // delete measurement by id
            case R.id.btnDeleteCurMeasurements:
                // Alert dialog to confirm delete all measurements
                AlertDialog.Builder alertDelCurMes = new AlertDialog.Builder(this);

                alertDelCurMes.setTitle(getString(R.string.delete_current_measurements));
                alertDelCurMes.setMessage(getString(R.string.these_changes_can_t_return));
                alertDelCurMes.setNegativeButton(getString(android.R.string.no), (dialog, whichButton) -> {
                    // Canceled.
                });

                // confirm delete current measurement
                alertDelCurMes.setPositiveButton(getString(android.R.string.yes), (dialog, whichButton) -> {
                    // delete current measurement from database
                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    database.delete(DBHelper.TABLE_MEASUREMENTS,
                            DBHelper.KEY_ID + " = " + idRec, null);

                    deleteRecord(idRec);
                    Toast.makeText(AddOrChangeMeasurementActivity.this,
                            getString(R.string.the_current_measurement_has_been_deleted),
                            Toast.LENGTH_LONG).show();
                    finish();

                });
                alertDelCurMes.show();
                break;

            // add or update measurement
            case R.id.btnSaveMeasurement:
                // choose update or add measurement
                updateRec = idRec != -1;

                // check date to more than current and reset if so
                now = Calendar.getInstance();
                if (dateAndTime.getTimeInMillis() > now.getTimeInMillis()) {
                    Toast.makeText(AddOrChangeMeasurementActivity.this, getString(R.string.incorrect_date) + "\n"
                            + getString(R.string.date_cannot_be_greater_than_the_current), Toast.LENGTH_SHORT).show();
                } else {
                    if (isCorrectInputValues()) {
                        float measurement = Float.parseFloat(etBloodSugarMeasurement.getText().toString());
                        long date = dateAndTime.getTimeInMillis() / 1000;
                        String comment = etComment.getText().toString();

                        // save measurement or update if updateRec is true
                        writeRecord(measurement, date, comment, updateRec);

                        Toast.makeText(this, getString(R.string.measurement_has_been_saved), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;

            default:
                break;
        }
    }

    // set date to caption
    public void setCaptionDateTime() {
        tvDateAndTime.setText(sdf.format(dateAndTime.getTimeInMillis()));
    }

    // listener for DatePickerDialog
    DatePickerDialog.OnDateSetListener datePickerDialog = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(year, monthOfYear, dayOfMonth);

            // checking on current date and reset date to current
            now = Calendar.getInstance();
            if (dateAndTime.getTimeInMillis() > now.getTimeInMillis()) {
                Toast.makeText(AddOrChangeMeasurementActivity.this, getString(R.string.incorrect_date) + "\n"
                        + getString(R.string.date_cannot_be_greater_than_the_current) + "\n"
                        + getString(R.string.date_has_been_reset), Toast.LENGTH_SHORT).show();

                // set current date
                dateAndTime.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
            } else {
                if (year < yearLimitLowerBound) {
                    Toast.makeText(AddOrChangeMeasurementActivity.this, getString(R.string.incorrect_date) + "\n"
                            + String.format(getString(R.string.date_cannot_be_less_than__year), yearLimitLowerBound) + "\n"
                            + getString(R.string.date_has_been_reset), Toast.LENGTH_SHORT).show();

                    // set current date
                    dateAndTime.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                }
            }
            setCaptionDateTime();
        }
    };

    // listener for TimePickerDialog
    TimePickerDialog.OnTimeSetListener timePickerDialog = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            now = Calendar.getInstance();
            dateAndTime.set(dateAndTime.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH), dateAndTime.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);

            // checking on current date and reset date to current
            now = Calendar.getInstance();
            if (dateAndTime.getTimeInMillis() > now.getTimeInMillis()) {
                Toast.makeText(AddOrChangeMeasurementActivity.this, getString(R.string.incorrect_date) + "\n"
                        + getString(R.string.date_cannot_be_greater_than_the_current) + "\n"
                        + getString(R.string.date_has_been_reset), Toast.LENGTH_SHORT).show();

                // set current date
                dateAndTime.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
            }
            setCaptionDateTime();
        }
    };

    //writing measurement record
    public void writeRecord(float measurement, long date, String comment, boolean updateRec) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if (prefsUnitBloodSugarMmol) {
            contentValues.put(DBHelper.KEY_MEASUREMENT, measurement);
        } else {
            float tmpBloodLowSugar = MyWorks.roundUp(
                    Float.parseFloat(etBloodSugarMeasurement.getText().toString()) / 18, 1).floatValue();
            contentValues.put(DBHelper.KEY_MEASUREMENT, tmpBloodLowSugar);
        }

        contentValues.put(DBHelper.KEY_TIME_IN_SECONDS, date);
        contentValues.put(DBHelper.KEY_COMMENT, comment);

        if (updateRec) {
            database.update(DBHelper.TABLE_MEASUREMENTS, contentValues,
                    DBHelper.KEY_ID + " = " + idRec, null);
        } else {
            database.insert(DBHelper.TABLE_MEASUREMENTS, null, contentValues);
        }

        database.close();
    }

    // delete measurement record by id
    public void deleteRecord(int idRec) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.delete(DBHelper.TABLE_MEASUREMENTS
                , DBHelper.KEY_ID + " = " + idRec
                , null);
        database.close();
    }


    private void loadRecords() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        long timeInMillis;

        Cursor cursor = database.query(DBHelper.TABLE_MEASUREMENTS, null,
                DBHelper.KEY_ID + " = " + idRec, null, null, null, null);
        cursor.moveToFirst();

        // db columns indexes
        int idMeasurement = cursor.getColumnIndex(DBHelper.KEY_MEASUREMENT);
        int idTimeInSeconds = cursor.getColumnIndex(DBHelper.KEY_TIME_IN_SECONDS);
        int idComment = cursor.getColumnIndex(DBHelper.KEY_COMMENT);

        // filling fields
        if (prefsUnitBloodSugarMmol)
            etBloodSugarMeasurement.setText(String.valueOf(cursor.getFloat(idMeasurement)));
        else
            etBloodSugarMeasurement.setText(String.valueOf((int) (cursor.getFloat(idMeasurement) * 18)));
        etComment.setText(cursor.getString(idComment));
        timeInMillis = getMillisInSeconds(cursor.getLong(idTimeInSeconds));

        //  Calendar time picker set
        dateAndTime.setTimeInMillis(timeInMillis);
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

        cursor.close();
        database.close();
    }

    // set hints for editText
    public void setEditTextsHints(boolean prefsUnitBloodSugarMmol) {
        if (prefsUnitBloodSugarMmol) {
            etBloodSugarMeasurement.setHint(String.format(Locale.ENGLISH, getString(R.string.from_toFloat),
                    bloodSugarLimitLow, bloodSugarLimitHigh));
        } else {
            etBloodSugarMeasurement.setHint(String.format(getString(R.string.from_toDecimal),
                    (int) (bloodSugarLimitLow * 18), (int) (bloodSugarLimitHigh * 18)));
        }
    }

    // check fields for correctness
    public boolean isCorrectInputValues() {
        // is empty
        if (MyWorks.requiredFiledEmpty(etBloodSugarMeasurement)) {
            Toast.makeText(this, getString(R.string.sugar_measurement_field_must_be_filled), Toast.LENGTH_SHORT).show();
            return false;
        }

        // check on range input value and set focus on them
        if (prefsUnitBloodSugarMmol) {
            if (!MyWorks.numberInRange(Float.parseFloat(etBloodSugarMeasurement.getText().toString()),
                    bloodSugarLimitLow, bloodSugarLimitHigh)) {
                etBloodSugarMeasurement.requestFocus();
                MyWorks.clearET(etBloodSugarMeasurement);
                Toast.makeText(this, getString(R.string.incorrect_value), Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            if (!MyWorks.numberInRange(Float.parseFloat(etBloodSugarMeasurement.getText().toString()),
                    (int) (bloodSugarLimitLow * 18), (int) (bloodSugarLimitHigh * 18))) {
                etBloodSugarMeasurement.requestFocus();
                MyWorks.clearET(etBloodSugarMeasurement);
                Toast.makeText(this, getString(R.string.incorrect_value), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    // get time in millis
    public long getMillisInSeconds(long timeInSeconds) {
        return timeInSeconds * 1000;
    }

    // initialize views on screen and their listening
    public void initViews() {
        // find views on screen by id
        btnChooseDate = findViewById(R.id.btnChooseDate);
        btnChooseTime = findViewById(R.id.btnChooseTime);
        btnSaveMeasurement = findViewById(R.id.btnSaveMeasurement);
        btnDeleteCurMeasurements = findViewById(R.id.btnDeleteCurMeasurements);

        // TEST -------
        btnAddTestData = findViewById(R.id.btnAddTestData);


        etBloodSugarMeasurement = findViewById(R.id.etBloodSugarMeasurementEdit);
        etComment = findViewById(R.id.etCommentEdit);

        tvDateAndTime = findViewById(R.id.tvDateAndTime);
        tvUnitBloodSugar = findViewById(R.id.tvUnitBloodSugar);


        // set hint for editText
        setEditTextsHints(prefsUnitBloodSugarMmol);

        // set views properties and other
//        timePicker.setIs24HourView(true);

        if (prefsUnitBloodSugarMmol) {
            tvUnitBloodSugar.setText(getString(R.string.mmol_l));
        } else {
            tvUnitBloodSugar.setText(getString(R.string.mg_dl));
        }

        // set the listeners for views
        btnChooseDate.setOnClickListener(this);
        btnChooseTime.setOnClickListener(this);
        btnSaveMeasurement.setOnClickListener(this);
        btnDeleteCurMeasurements.setOnClickListener(this);

        // TEST -------
        btnAddTestData.setOnClickListener(this);

        // event on text change in editTexts
        etBloodSugarMeasurement.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String s = String.valueOf(charSequence);

                // set one point accuracy for editText value
                if (s.contains(".")) {
                    s = MyWorks.getStringNumberWithAccuracy(s, 1, '.', false);
                    if (s.length() != charSequence.toString().length()) {
                        etBloodSugarMeasurement.setText(s);
                        etBloodSugarMeasurement.setSelection(s.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}