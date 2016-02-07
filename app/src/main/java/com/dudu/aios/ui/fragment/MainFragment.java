package com.dudu.aios.ui.fragment;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dudu.aios.ui.fragment.base.BaseFragment;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.CarCheckingActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class MainFragment extends BaseFragment implements View.OnClickListener {
    private LinearLayout vehicleInspection, drivingRecord, navigation, bluetoothPhone, flow, preventRob;
    private TextView mDateTextView, mWeatherView, mTemperatureView;

    @Override
    public View getChildView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main_layout, null);
        initFragmentView(view);
        initOnClickListener();
        initData();
        return view;
    }

    private void initData() {
        initDate();
    }

    private void initDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy/MM/dd");
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)) {
            mWay = "星期天";
        } else if ("2".equals(mWay)) {
            mWay = "星期一";
        } else if ("3".equals(mWay)) {
            mWay = "星期二";
        } else if ("4".equals(mWay)) {
            mWay = "星期三";
        } else if ("5".equals(mWay)) {
            mWay = "星期四";
        } else if ("6".equals(mWay)) {
            mWay = "星期五";
        } else if ("7".equals(mWay)) {
            mWay = "星期六";
        }

        mDateTextView.setText(dateFormat.format(new Date()) + mWay);
    }

    private void initOnClickListener() {
        vehicleInspection.setOnClickListener(this);
        drivingRecord.setOnClickListener(this);
        navigation.setOnClickListener(this);
        bluetoothPhone.setOnClickListener(this);
        flow.setOnClickListener(this);
        preventRob.setOnClickListener(this);
    }

    private void initFragmentView(View view) {
        vehicleInspection = (LinearLayout) view.findViewById(R.id.vehicle_inspection);
        drivingRecord = (LinearLayout) view.findViewById(R.id.driving_record_button);
        navigation = (LinearLayout) view.findViewById(R.id.navigation_button);
        bluetoothPhone = (LinearLayout) view.findViewById(R.id.bluetooth_phone_button);
        flow = (LinearLayout) view.findViewById(R.id.flow_button);
        preventRob = (LinearLayout) view.findViewById(R.id.prevent_rob);
        mDateTextView = (TextView) view.findViewById(R.id.text_date);
        mTemperatureView = (TextView) view.findViewById(R.id.text_temperature);
        mWeatherView = (TextView) view.findViewById(R.id.text_weather);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vehicle_inspection:
                startActivity(new Intent(getActivity(), CarCheckingActivity.class));
                break;
            case R.id.driving_record_button:
                break;
            case R.id.navigation_button:
                break;
            case R.id.bluetooth_phone_button:
                break;
            case R.id.flow_button:
                break;
            case R.id.prevent_rob:
                break;

        }

    }
}
