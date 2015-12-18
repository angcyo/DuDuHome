package com.dudu.android.launcher.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.WeatherUtil;

import de.greenrobot.event.EventBus;

public class WeatherAlarmReceiver extends BroadcastReceiver {
    public WeatherAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        WeatherUtil.requestWeatherInfo();
    }
}