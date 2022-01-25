package ru.opalevapps.EveryGlic.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // start main Activity and finish splash
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }
}