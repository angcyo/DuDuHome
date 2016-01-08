package com.dudu.android.launcher.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.SharedPreferencesUtil;
import com.dudu.android.launcher.utils.WeatherUtil;
import com.dudu.android.launcher.utils.WeatherUtils;
import com.dudu.monitor.utils.LocationUtils;
import com.dudu.voice.semantic.chain.WeatherChain;

import de.greenrobot.event.EventBus;

public class WeatherAlarmReceiver extends BroadcastReceiver {
    public WeatherAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        WeatherUtils.getInstance().startWeatherUnderstanding(LocationUtils.getInstance(context).getCurrentCity() + "今天的天气");
    }
}
