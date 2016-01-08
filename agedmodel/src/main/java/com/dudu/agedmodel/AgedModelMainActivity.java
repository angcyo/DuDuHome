package com.dudu.agedmodel;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;
import com.dudu.event.ExitTimerEvent;
import com.dudu.event.GaoMapEvent;
import com.dudu.map.AMapLocationHandler;
import com.dudu.service.FloatBackButtonService;
import com.dudu.service.TimerExitService;
import com.dudu.utils.AgedNaviEvent;
import com.dudu.utils.AgedUtils;
import com.dudu.utils.Contacts;
import com.dudu.utils.LocationUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;

public class AgedModelMainActivity extends NoTitleBaseActivity implements AMapLocalWeatherListener, View.OnClickListener {
    private Handler handler;
    private Intent mIntent = null;
    private int classType;
    private AgedModelMainActivity mActivity;
    private Application mApp;
    private LocationManagerProxy locationManagerProxy;
    private TextView txtDate, txtWeather, txtTemperature;
    private Button btnVideo, btnNavigation, btnDiDi, btnWlan;
    private TextView tvGaoMapMessage;
    private TFlashCardReceiver mTFlashCardReceiver;
    private Handler gaoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                EventBus.getDefault().post(AgedNaviEvent.FloatButtonEvent.SHOW);
                AgedUtils.installGaoDeMap(AgedModelMainActivity.this);
            } else if (msg.what == 1) {
                tvGaoMapMessage.setText((String) msg.obj);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mApp = this.getApplication();
        setContentView(R.layout.activity_aged_model_main);
        initView();
        initData();
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
        btnVideo = (Button) findViewById(R.id.video_button);
        btnNavigation = (Button) findViewById(R.id.navigation_button);
        btnDiDi = (Button) findViewById(R.id.didi_button);
        btnWlan = (Button) findViewById(R.id.wifi_button);
        tvGaoMapMessage = (TextView) findViewById(R.id.tv_gaoMap_message);
    }

    private void initData() {
        btnVideo.setOnClickListener(this);
        btnNavigation.setOnClickListener(this);
        btnDiDi.setOnClickListener(this);
        btnWlan.setOnClickListener(this);

        /**
         * 启动定位的监听事件
         * */
        AMapLocationHandler.getInstance(this).init();
        /**
         * 设置天气的回调事件
         *
         * */
        com.dudu.android.hideapi.SystemPropertiesProxy.getInstance()
                .set(this, "persist.sys.screen", "on");
        locationManagerProxy = LocationManagerProxy.getInstance(this);
        locationManagerProxy.requestWeatherUpdates(LocationManagerProxy.WEATHER_TYPE_LIVE, this);
        handler = null;
        //注册EventBus事件，接受发生的事件
        EventBus.getDefault().register(this);
        startService(new Intent(mActivity, TimerExitService.class));
        startService(new Intent(mActivity, FloatBackButtonService.class));

        handler = new MyHandler();
        gaoHandler.sendEmptyMessageDelayed(0, 5000);

        mTFlashCardReceiver = new TFlashCardReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addDataScheme("file");
        registerReceiver(mTFlashCardReceiver, intentFilter);
        checkGaoMap();
    }

    private void checkGaoMap() {
        if (AgedUtils.checkGaoMaoStall(this)) {
            tvGaoMapMessage.setText(getString(R.string.gao_map_installed));
        } else {
            tvGaoMapMessage.setText(getString(R.string.gao_map_uninstall));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIntent = null;
        Intent intent = getIntent();
        if (intent != null) {
            classType = intent.getIntExtra(Contacts.CLASS_TYPE, Contacts.DEFAULT_TYPE);
            //skipActivity(classType);
        }

//        EventBus.getDefault().post(AgedNaviEvent.FloatButtonEvent.HIDE);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_button:
                AgedUtils.installGaoDeMap(this);
                break;
            case R.id.navigation_button:
                AgedUtils.loadOffLine(this);
                break;
            case R.id.didi_button:
                AgedUtils.uninstallGaoApk(this);
                break;
            case R.id.wifi_button:
                break;
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            startActivity(mIntent);
            finish();

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
        unregisterReceiver(mTFlashCardReceiver);
        handler.removeMessages(0);
    }

    //观察者订阅事件
    public void onEventMainThread(ExitTimerEvent event) {
        Log.i("ji", "accept");
        stopService(new Intent(this, TimerExitService.class));
        EventBus.getDefault().unregister(this);
        System.exit(0);
    }

    public void onEventMainThread(GaoMapEvent event) {
        tvGaoMapMessage.setText(event.getMessage());
        String message = event.getMessage();
        Message msg = new Message();
        msg.what = 1;
        msg.obj = message;
        gaoHandler.sendMessage(msg);
    }

    private class TFlashCardReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            } else if (action.equals(Intent.ACTION_MEDIA_REMOVED)) {
                AgedUtils.uninstallGaoApk(mApp);
            }
        }
    }
}
