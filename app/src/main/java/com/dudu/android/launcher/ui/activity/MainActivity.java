package com.dudu.android.launcher.ui.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.service.NewMessageShowService;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.LocationUtils;
import com.dudu.android.launcher.utils.WeatherIconsUtils;
import com.dudu.map.MapManager;


public class MainActivity extends BaseTitlebarActivity implements
        OnClickListener, AMapLocalWeatherListener {

    private Button mVideoButton, mNavigationButton, mPhoneButton,
            mNearbyButton, mWlanButton;

    private ProgressBar mFlowProgressbar;

    private FlowUpdateReciever mFlowReciever;

    private LocationManagerProxy mLocationManagerProxy;

    private TextView mDateTextView, mWeatherView, mTemperatureView;

    private ImageView mWeatherImage;

    private Timer timer;

    private LinearLayout mActivationContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerFlowReciever();

        startFloatMessageShowService();
    }

    @Override
    public int initContentView() {
        return R.layout.main_layout;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        setContext(this);

        mPhoneButton = (Button) findViewById(R.id.phone_button);
        mNearbyButton = (Button) findViewById(R.id.nearby_button);
        mWlanButton = (Button) findViewById(R.id.wlan_button);
        mVideoButton = (Button) findViewById(R.id.video_button);
        mNavigationButton = (Button) findViewById(R.id.navigation_button);
        mDateTextView = (TextView) findViewById(R.id.date_text);

        mFlowProgressbar = (ProgressBar) findViewById(R.id.flow_progressbar);
        mWeatherView = (TextView) findViewById(R.id.weather_text);
        mTemperatureView = (TextView) findViewById(R.id.temperature_text);
        mWeatherImage = (ImageView) findViewById(R.id.weather_image);

        mActivationContainer = (LinearLayout) findViewById(R.id.activation_container);
    }

    @Override
    public void initListener() {
        mPhoneButton.setOnClickListener(this);
        mNearbyButton.setOnClickListener(this);
        mWlanButton.setOnClickListener(this);
        mVideoButton.setOnClickListener(this);
        mNavigationButton.setOnClickListener(this);
        mActivationContainer.setOnClickListener(this);
        mNearbyButton.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings",
                        "com.android.settings.Settings"));
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public void initDatas() {
        startFloatMessageShowService();

        getDate();

        initVideoService();

        initWeatherInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterFlowReciever();

        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_button:
                break;

            case R.id.nearby_button:
                break;

            case R.id.wlan_button:
                break;

            case R.id.navigation_button:

                Intent navigationintent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isManual", true);
                navigationintent.putExtras(bundle);
                if (MapManager.getInstance().isNavi() ||MapManager.getInstance().isNaviBack()) {
                    if (MapManager.getInstance().isNavi()) {
                        navigationintent.setClass(mContext,
                                NaviCustomActivity.class);
                    } else if (MapManager.getInstance().isNaviBack()) {
                        navigationintent.setClass(mContext,
                                NaviBackActivity.class);
                    }
                } else {
                    navigationintent.setClass(mContext, LocationActivity.class);

                }
                startActivity(navigationintent);

                break;

            case R.id.activation_container:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void registerFlowReciever() {
        mFlowReciever = new FlowUpdateReciever();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.FLOW_UPDATE_BROADCAST);
        registerReceiver(mFlowReciever, intentFilter);
    }

    private void unregisterFlowReciever() {
        if (mFlowReciever != null) {
            unregisterReceiver(mFlowReciever);
        }
    }

    /**
     * 实例化日期
     */
    private void getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy年MM月dd日   EEEE", Locale.getDefault());
        mDateTextView.setText(dateFormat.format(new Date()));
    }

    /**
     * 实例化录像服务
     */
    private void initVideoService() {

    }

    /**
     * 实例化请求地图天气接口
     */
    private void initWeatherInfo() {
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        mLocationManagerProxy.requestWeatherUpdates(
                LocationManagerProxy.WEATHER_TYPE_LIVE, this);

        if (timer == null) {
            timer = new Timer();
        }

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                requestWeatherInfo();
            }
        }, 10 * 1000, 60 * 60 * 1000);
    }

    private void requestWeatherInfo() {
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.requestWeatherUpdates(
                    LocationManagerProxy.WEATHER_TYPE_LIVE, MainActivity.this);
        }
    }

    /**
     * 更新流量进度条
     */
    private void updateFlowUsage() {
        mFlowProgressbar.setProgress(0);
    }

    private class FlowUpdateReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateFlowUsage();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestWeatherInfo();
    }

    @Override
    public void onWeatherForecaseSearched(AMapLocalWeatherForecast arg0) {

    }

    @Override
    public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {
        if (aMapLocalWeatherLive != null
                && aMapLocalWeatherLive.getAMapException().getErrorCode() == 0) {
            String weather = aMapLocalWeatherLive.getWeather();
            String temperature = aMapLocalWeatherLive.getTemperature();
            if (TextUtils.isEmpty(weather) || TextUtils.isEmpty(temperature)) {
                return;
            }

            if (weather.contains("-")) {
                weather = weather
                        .replace("-", getString(R.string.weather_turn));
            }

            mTemperatureView.setText(temperature
                    + getString(R.string.temperature_degree));
            mWeatherView.setText(weather);
            mWeatherImage.setImageResource(WeatherIconsUtils
                    .getWeatherIcon(WeatherIconsUtils.getWeatherType(weather)));
            LocationUtils.getInstance(this).setCurrentCity(aMapLocalWeatherLive.getCity());
        } else {
            Toast.makeText(this, R.string.get_weather_info_failed,
                    Toast.LENGTH_SHORT).show();
            mWeatherView.setText(R.string.unkown_weather_info);
        }
    }

    private void startFloatMessageShowService() {
        Intent i = new Intent(MainActivity.this, NewMessageShowService.class);
        startService(i);
    }

}
