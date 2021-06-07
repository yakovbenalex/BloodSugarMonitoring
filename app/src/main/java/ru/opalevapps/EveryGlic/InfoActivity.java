package ru.opalevapps.EveryGlic;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {
    // const
    private static final String DB_NAME = "info.db";
    private static final String TAG = "myLog";
    String[] spinnerData;

    // views
    Button btnInfoUsefulTips;
    Button btnInfoEmergencyConditions;
    Button btnInfoChronicComplications;
    Button btnInfoUrgentMedicalAssistance;

    Spinner spinnerInfo;

    // field to output info
    TextView tvInfo;

    //Переменная для работы с БД
    private SQLiteDatabase myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        initViews();

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
        /*
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
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MyWorks.createInfoItemInActionBar(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MyWorks.parseMenuItemInfo(this, item);
        return super.onOptionsItemSelected(item);
    }

    // initialize views on screen and their listening
    public void initViews() {
        //TODO in the future to make loading data from DB
        String[] spinnerData = new String[4];
        spinnerData[0] = getString(R.string.UsefulTips);//getString(R.string._InfoUsefulTips);
        spinnerData[1] = getString(R.string.EmergencyConditions);//getString(R.string._InfoEmergencyConditions);
        spinnerData[2] = getString(R.string.ChronicComplications);//getString(R.string._InfoChronicComplications);
        spinnerData[3] = getString(R.string.UrgentMedicalAssistance);//getString(R.string._InfoUrgentMedicalAssistance);

/*
        // find views on screen by id
        btnInfoUsefulTips = findViewById(R.id.btnInfoUsefulTips);
        btnInfoEmergencyConditions = findViewById(R.id.btnInfoEmergencyConditions);
        btnInfoChronicComplications = findViewById(R.id.btnInfoChronicComplications);
        btnInfoUrgentMedicalAssistance = findViewById(R.id.btnInfoUrgentMedicalAssistance);
*/

        tvInfo = findViewById(R.id.tvInfo);

        //TODO correct this place =)
        // adapter
        spinnerInfo = findViewById(R.id.spinnerInfo);
        List<String> list = new ArrayList<>(Arrays.asList(spinnerData));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInfo.setAdapter(spinnerAdapter);
//        spinnerAdapter.add("DELHI");
//        spinnerAdapter.notifyDataSetChanged();

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Получаем выбранный объект
                String item = (String)parent.getItemAtPosition(position);
                tvInfo.setText(item);

                //TODO here will be download info from DB
                switch (position) {
                    case 0:
                        tvInfo.setText(getString(R.string._InfoUsefulTips));
                        break;

                    case 1:
                        tvInfo.setText(getString(R.string._InfoEmergencyConditions));
                        break;

                    case 2:
                        tvInfo.setText(getString(R.string._InfoChronicComplications));
                        break;

                    case 3:
                        tvInfo.setText(getString(R.string._InfoUrgentMedicalAssistance));
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinnerInfo.setOnItemSelectedListener(itemSelectedListener);

/*
        // set the listeners for views
        btnInfoUsefulTips.setOnClickListener(this);
        btnInfoEmergencyConditions.setOnClickListener(this);
        btnInfoChronicComplications.setOnClickListener(this);
        btnInfoUrgentMedicalAssistance.setOnClickListener(this);
*/

    }
}