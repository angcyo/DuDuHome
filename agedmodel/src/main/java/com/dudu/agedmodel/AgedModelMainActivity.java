package com.dudu.agedmodel;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;
import com.dudu.event.ExitTimerEvent;
import com.dudu.map.AMapLocationHandler;
import com.dudu.service.TimerExitService;
import com.dudu.utils.Contacts;
import com.dudu.utils.LocationUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;

public class AgedModelMainActivity extends NoTitleBaseActivity implements AMapLocalWeatherListener {
    private Handler handler;
    private Intent mIntent = null;
    private int classType;
    private AgedModelMainActivity mActivity;
    private LocationManagerProxy locationManagerProxy;
    private TextView txtDate, txtWeather, txtTemperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_aged_model_main);
        initData();
        initView();
        getDate();
    }

    private void getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy年MM月dd日 EEEE", Locale.getDefault());
        txtDate.setText(dateFormat.format(new Date()));
    }

    private void initView() {
        txtDate = (TextView) findViewById(R.id.data_text);
        txtWeather = (TextView) findViewById(R.id.weather_text);
        txtTemperature = (TextView) findViewById(R.id.temperature_text);
    }

    private void initData() {
        /**
         * 启动定位的监听事件
         * */

        /**
         * 设置天气的回调事件
         *
         * */
        com.dudu.android.hideapi.SystemPropertiesProxy.getInstance()
                .set(this, "persist.sys.screen","on");
        locationManagerProxy = LocationManagerProxy.getInstance(this);
        locationManagerProxy.requestWeatherUpdates(LocationManagerProxy.WEATHER_TYPE_LIVE, this);
        handler = null;
        //注册EventBus事件，接受发生的事件
        EventBus.getDefault().register(this);
        startService(new Intent(mActivity, TimerExitService.class));
        handler = new MyHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIntent = null;
        Intent intent = getIntent();
        if (intent != null) {
            classType = intent.getIntExtra(Contacts.CLASS_TYPE, Contacts.DEFAULT_TYPE);
            skipActivity(classType);
        }
    }

    @Override
    public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {
        String city = aMapLocalWeatherLive.getCity();
        String cityCode = aMapLocalWeatherLive.getCityCode();
        //将所在的城市存储到sharedPreferences中
        LocationUtils.getInstance(this).setCurrentCity(city);
        LocationUtils.getInstance(this).setCurrentCitycode(cityCode);
        String weather = aMapLocalWeatherLive.getWeather();
        String temperature = aMapLocalWeatherLive.getTemperature();
        if (weather != null) {
            if (weather.contains("-")) {
                weather = weather
                        .replace("-", getString(R.string.weather_turn));
            }
            txtWeather.setText(weather);
        }
        if (temperature != null) {
            txtTemperature.setText(temperature + getString(R.string.temperature_degree));
        }
    }

    @Override
    public void onWeatherForecaseSearched(AMapLocalWeatherForecast aMapLocalWeatherForecast) {

    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                startActivity(mIntent);
                finish();
            }
        }
    }

    private void skipActivity(int classType) {
        switch (classType) {
            case Contacts.DEFAULT_TYPE:
                mIntent = new Intent(mActivity, AgedCameraActivity.class);
                break;
            case Contacts.CAMERA_TYPE:
                mIntent = new Intent(AgedModelMainActivity.this, AgedVideoPlayActivity.class);
                break;
            case Contacts.VIDEO_PLAY_TYPE:
                mIntent = new Intent(mActivity, AgedMapActivity.class);
                break;
            case Contacts.END_NAV_TYPE:
                mIntent = new Intent(mActivity, AgedModelMainActivity.class);
                mIntent.putExtra(Contacts.CLASS_TYPE, Contacts.CLICK_ICON_TYPE);
                break;
            case Contacts.CLICK_ICON_TYPE:
                mIntent = new Intent(mActivity, AgedModelMainActivity.class);
                break;
        }
        handler.sendEmptyMessageDelayed(0, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
    }

    //观察者订阅事件
    public void onEventMainThread(ExitTimerEvent event) {
        Log.i("ji", "accept");
        stopService(new Intent(this, TimerExitService.class));
        EventBus.getDefault().unregister(this);
        System.exit(0);
    }

}
