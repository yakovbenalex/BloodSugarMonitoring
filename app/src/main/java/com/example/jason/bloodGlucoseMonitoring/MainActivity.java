package com.example.jason.bloodGlucoseMonitoring;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_BLOOD_HIGH_SUGAR;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_BLOOD_LOW_SUGAR;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_DIABETES_1TYPE;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_FIRST_RUN_AGREEMENT;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.KEY_PREFS_UNIT_BLOOD_SUGAR_MMOL;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.amountCarbsInBreadUnitDefault;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.bloodHighSugarDefault;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.bloodLowSugarDefault;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.diabetes1TypeDefault;
import static com.example.jason.bloodGlucoseMonitoring.PreferencesActivity.unitBloodSugarMmolDefault;


// implements View.OnClickListener
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // keys
//    private static final String KEY_PREFS_FIRST_RUN_AGREEMENT = "firstRun";

    // variables for preferences
    float prefsBloodLowSugar;
    float prefsBloodHighSugar;
    float prefsAmountCarb;
    boolean prefsDiabetes1Type;
    boolean prefsUnitBloodSugarMmol;
    boolean firstRun;


    // views
    Button btnCalculatorCarbs;
    Button btnAddMeasurement;
    Button btnMeasurements;
    Button btnStatistics;

    TextView textView;

    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
        firstRun = sharedPref.getBoolean(KEY_PREFS_FIRST_RUN_AGREEMENT, true);

        if (firstRun) {
            Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
            startActivity(intent);
        } else {

        }

        btnCalculatorCarbs = (Button) findViewById(R.id.btnCalculatorCarbs);
        btnAddMeasurement = (Button) findViewById(R.id.btnAddMeasurement);
        btnMeasurements = (Button) findViewById(R.id.btnMeasurements);
        btnStatistics = (Button) findViewById(R.id.btnStatistics);

        textView = (TextView) findViewById(R.id.textView);


        // Listeners
        btnCalculatorCarbs.setOnClickListener(this);
        btnAddMeasurement.setOnClickListener(this);
        btnMeasurements.setOnClickListener(this);
        btnStatistics.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        /*SubMenu subMenuFile = menu.addSubMenu("Файл");
        subMenuFile.add(Menu.NONE, 10, Menu.NONE, "Новый");
        SubMenu subMenuEdit = menu.addSubMenu("Правка");
        subMenuEdit.add(Menu.NONE, 14, Menu.NONE, "Вырезать");
        menu.add(Menu.NONE, 17, Menu.NONE, "Справка");*/

        return true;
    }

    @Override
    protected void onResume() {
        loadPreferences();
        textView.setText(String.valueOf(prefsDiabetes1Type));
        textView.append("\n" + prefsUnitBloodSugarMmol);
        textView.append("\n" + String.valueOf(prefsBloodLowSugar));
        textView.append("\n" + String.valueOf(prefsBloodHighSugar));
        textView.append("\n" + String.valueOf(prefsAmountCarb));

        super.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intentPreferencesActivity = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivity(intentPreferencesActivity);
                break;

            case R.id.action_help:
//                Intent intent = new Intent(MainActivity.this, AgreementActivity.class);
//                startActivity(intent);

                break;

            case R.id.action_agreement:
                Intent intentAgreementActivity = new Intent(MainActivity.this, AgreementActivity.class);
                startActivity(intentAgreementActivity);
                break;

            case R.id.action_about:
                Intent intentAboutActivity = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intentAboutActivity);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCalculatorCarbs:
                Intent intentCalculatorCarbsActivity = new Intent(this, CalculatorCarbsActivity.class);
                startActivity(intentCalculatorCarbsActivity);
                break;

            case R.id.btnAddMeasurement:
                Intent intentAddMeasurementActivity = new Intent(this, AddMeasurementActivity.class);
                startActivity(intentAddMeasurementActivity);
                break;

            case R.id.btnMeasurements:
                Intent intentMeasurementsActivity = new Intent(this, MeasurementsActivity.class);
                startActivity(intentMeasurementsActivity);
                break;

            case R.id.btnStatistics:
                Intent intentStatisticsActivity = new Intent(this, StatisticsActivity.class);
                startActivity(intentStatisticsActivity);
                break;

            default:
                break;
        }
    }

    public void loadPreferences() {
        // get preferences object
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);

        // get saved value
        prefsDiabetes1Type = sharedPref.getBoolean(KEY_PREFS_DIABETES_1TYPE, diabetes1TypeDefault);
        prefsUnitBloodSugarMmol = sharedPref.getBoolean(KEY_PREFS_DIABETES_1TYPE, unitBloodSugarMmolDefault);
        prefsBloodLowSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_LOW_SUGAR, bloodLowSugarDefault);
        prefsBloodHighSugar = sharedPref.getFloat(KEY_PREFS_BLOOD_HIGH_SUGAR, bloodHighSugarDefault);
        prefsAmountCarb = sharedPref.getFloat(KEY_PREFS_AMOUNT_CARBS_IN_BREAD_UNIT, amountCarbsInBreadUnitDefault);
    }

    @Override
    public void onBackPressed() {
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(MainActivity.this);
        quitDialog.setTitle(R.string.exit_are_you_sure);
        quitDialog.setPositiveButton(R.string.still_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        quitDialog.setNegativeButton(R.string.never, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel
            }
        });

        quitDialog.show();
    }
}









/*
        // App memory usage
        // Get the Java runtime
        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
        Toast.makeText(this, String.valueOf(memoryUsed), Toast.LENGTH_LONG).show();

*/

    /*
    int memoryClass = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
            .getMemoryClass();
    Toast.makeText(this, String.valueOf(memoryClass), Toast.LENGTH_LONG).show();
    */