package com.example.jason.bloodGlucoseMonitoring;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

class MyItemRecordsAdapter extends BaseAdapter {

    private ArrayList<MyItemRecords> data = new ArrayList<>();
    private Context context;

    // variables for preferences
    private float prefsBloodLowSugar;
    private float prefsBloodHighSugar;
    private boolean prefsTimeFormat24h;

    MyItemRecordsAdapter(Context context, ArrayList<MyItemRecords> arr, float prefsBloodLowSugar, float prefsBloodHighSugar, boolean prefsTimeFormat24h) {
        if (arr != null) {
            data = arr;
        }
        this.context = context;
        this.prefsBloodLowSugar = prefsBloodLowSugar;
        this.prefsBloodHighSugar = prefsBloodHighSugar;
        this.prefsTimeFormat24h = prefsTimeFormat24h;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int num) {
        return data.get(num);
    }

    @Override
    public long getItemId(int arg0) {
        return getId(arg0);
    }

    @Override
    public View getView(int i, View someView, ViewGroup arg2) {
        // get object of inflater from context
        LayoutInflater inflater = LayoutInflater.from(context);

        // If someView is null then load his from (with) inflater
        if (someView == null) {
            someView = inflater.inflate(R.layout.my_list_item_records, arg2, false);
        }

        // find views on screen by id
        TextView tvListDate = (TextView) someView.findViewById(R.id.tvListDate);
        TextView tvListTime = (TextView) someView.findViewById(R.id.tvListTime);
        TextView tvListSugar = (TextView) someView.findViewById(R.id.tvListSugar);

        // get data
        float sugar = data.get(i).getMeasurementSugar();

        // sugar color range
        if (sugar < prefsBloodLowSugar) { // low sugar
            tvListSugar.setTextColor(Color.BLUE);
        } else {
            if (sugar < prefsBloodHighSugar) { // normal sugar
                tvListSugar.setTextColor(Color.DKGRAY);
            } else { // high sugar
                tvListSugar.setTextColor(Color.RED);
            }
        }

        // set date and time format
        String DATE_FORMAT = "dd/MM/yyyy";
        String TIME_FORMAT;
        if (prefsTimeFormat24h) {
            TIME_FORMAT = "HH:mm";
        } else {
            TIME_FORMAT = "h:mm a";
        }
        SimpleDateFormat sdfDate = new SimpleDateFormat(DATE_FORMAT);
        SimpleDateFormat sdfTime = new SimpleDateFormat(TIME_FORMAT);

        // set textViews data
        tvListDate.setText(sdfDate.format(data.get(i).getTimeInMillis()));
        tvListTime.setText(sdfTime.format(data.get(i).getTimeInMillis()));
        tvListSugar.setText(String.valueOf(data.get(i).getMeasurementSugar()));

        return someView;
    }

    private int getId(int position) {
        return data.get(position).getId();
    }
}