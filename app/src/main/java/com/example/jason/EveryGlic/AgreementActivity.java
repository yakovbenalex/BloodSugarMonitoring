package com.example.jason.EveryGlic;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static com.example.jason.EveryGlic.PreferencesActivity.KEY_PREFS;
import static com.example.jason.EveryGlic.PreferencesActivity.KEY_PREFS_FIRST_RUN_AGREEMENT;

public class AgreementActivity extends AppCompatActivity implements View.OnClickListener {
    // temporary variables
    private static long back_pressed;

    // views declare
    Button btnConfirm;

    // prefs vars
    private boolean firstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);

        // find views on screen by id
        btnConfirm = (Button) findViewById(R.id.btnConfirm);

        // listeners
        btnConfirm.setOnClickListener(this);

        // get firstRun value
        SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
        firstRun = sharedPref.getBoolean(KEY_PREFS_FIRST_RUN_AGREEMENT, true);
        if (!firstRun) {
            btnConfirm.setText(android.R.string.ok);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConfirm:
                // set firstRun flag to false
                SharedPreferences sharedPref = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.putBoolean(KEY_PREFS_FIRST_RUN_AGREEMENT, false);
                prefEditor.apply();

                finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (firstRun) {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                moveTaskToBack(true);
                finishAffinity();
            } else {
                Toast.makeText(getBaseContext(), R.string.click_the_back_button_again_to_cancel_the_agreement, Toast.LENGTH_SHORT).show();
            }
            back_pressed = System.currentTimeMillis();
        } else {
            super.onBackPressed();
            finish();
        }
    }
}