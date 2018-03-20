package com.example.jason.EveryGlic;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import static com.example.jason.EveryGlic.MyWorks.createInfoItemInActionBar;
import static com.example.jason.EveryGlic.MyWorks.parseMenuItemInfo;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {
    // const
    private static final String DB_NAME = "info.db";
    private static final String TAG = "myLog";

    // views
    Button btnInfoUsefulTips;
    Button btnInfoEmergencyConditions;
    Button btnInfoChronicComplications;
    Button btnInfoUrgentMedicalAssistance;

    // field to output info
    TextView tvInfo;

    //Переменная для работы с БД
    private SQLiteDatabase myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // find views on screen by id
        btnInfoUsefulTips = findViewById(R.id.btnInfoUsefulTips);
        btnInfoEmergencyConditions = findViewById(R.id.btnInfoEmergencyConditions);
        btnInfoChronicComplications = findViewById(R.id.btnInfoChronicComplications);
        btnInfoUrgentMedicalAssistance = findViewById(R.id.btnInfoUrgentMedicalAssistance);

        tvInfo = findViewById(R.id.tvInfo);

        // set the listeners for views
        btnInfoUsefulTips.setOnClickListener(this);
        btnInfoEmergencyConditions.setOnClickListener(this);
        btnInfoChronicComplications.setOnClickListener(this);
        btnInfoUrgentMedicalAssistance.setOnClickListener(this);

        // get current locale
        Locale curLocale = getResources().getConfiguration().locale;
        boolean isLocaleRu = (curLocale.getLanguage().equals(new Locale("ru").getLanguage()));
        // if locale is not russian then show an appropriate message
        if (!isLocaleRu) {
            MyWorks.showDialog(this, getString(R.string.attention),
                    getString(R.string.help_is_only_in_russian)
                            + getString(R.string.languageNotSupport));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInfoUsefulTips:
                tvInfo.setText(getString(R.string._InfoUsefulTips));
                break;

            case R.id.btnInfoEmergencyConditions:
                tvInfo.setText(getString(R.string._InfoEmergencyConditions));
                break;

            case R.id.btnInfoChronicComplications:
                tvInfo.setText(getString(R.string._InfoChronicComplications));
                break;

            case R.id.btnInfoUrgentMedicalAssistance:
                tvInfo.setText(getString(R.string._InfoUrgentMedicalAssistance));
                break;

            default:
                break;
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
}