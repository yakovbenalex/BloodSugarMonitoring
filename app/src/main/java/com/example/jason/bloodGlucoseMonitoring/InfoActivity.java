package com.example.jason.bloodGlucoseMonitoring;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity implements View.OnClickListener {
    // views
    Button btnInfoUsefulTips;
    Button btnInfoEmergencyConditions;
    Button btnInfoChronicComplications;
    Button btnInfoUrgentMedicalAssistance;

    TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // find views on screen by id
        btnInfoUsefulTips = (Button) findViewById(R.id.btnInfoUsefulTips);
        btnInfoEmergencyConditions = (Button) findViewById(R.id.btnInfoEmergencyConditions);
        btnInfoChronicComplications = (Button) findViewById(R.id.btnInfoChronicComplications);
        btnInfoUrgentMedicalAssistance = (Button) findViewById(R.id.btnInfoUrgentMedicalAssistance);

        tvInfo = (TextView) findViewById(R.id.tvInfo);

        // set the listeners for views
        btnInfoUsefulTips.setOnClickListener(this);
        btnInfoEmergencyConditions.setOnClickListener(this);
        btnInfoChronicComplications.setOnClickListener(this);
        btnInfoUrgentMedicalAssistance.setOnClickListener(this);
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
}