package com.dudu.android.launcher.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.service.NewMessageShowService;
import com.dudu.android.launcher.service.RecordBindService;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;
import com.dudu.android.launcher.ui.activity.video.VideoActivity;
import com.dudu.android.launcher.utils.LocationUtils;
import com.dudu.android.launcher.utils.WeatherIconsUtils;
import com.dudu.map.MapManager;
import com.dudu.obd.OBDDataService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends BaseTitlebarActivity implements
        OnClickListener, AMapLocalWeatherListener {

    private Button mVideoButton, mNavigationButton, mPhoneButton,
            mDiDiButton, mWlanButton;

    private LocationManagerProxy mLocationManagerProxy;

    private TextView mDateTextView, mWeatherView, mTemperatureView;

    private ImageView mWeatherImage;

    private Timer timer;

    private RecordBindService mRecordService;

    private ServiceConnection mServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startFloatMessageShowService();

//        startOBDService();
    }

    @Override
    public int initContentView() {
        return R.layout.activity_main_layout;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        setContext(this);

        mDiDiButton = (Button) findViewById(R.id.didi_button);
        mWlanButton = (Button) findViewById(R.id.wlan_button);
        mVideoButton = (Button) findViewById(R.id.video_button);
        mNavigationButton = (Button) findViewById(R.id.navigation_button);
        mDateTextView = (TextView) findViewById(R.id.date_text);
        mWeatherView = (TextView) findViewById(R.id.weather_text);
        mTemperatureView = (TextView) findViewById(R.id.temperature_text);
        mWeatherImage = (ImageView) findViewById(R.id.weather_image);
    }

    @Override
    public void initListener() {
        mDiDiButton.setOnClickListener(this);
        mWlanButton.setOnClickListener(this);
        mVideoButton.setOnClickListener(this);
        mNavigationButton.setOnClickListener(this);
        mDiDiButton.setOnLongClickListener(new OnLongClickListener() {

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
        if (timer != null) {
            timer.cancel();
        }

        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_button:
                mRecordService.startRecord();
                mRecordService.startRecordTimer();
                startActivity(new Intent(MainActivity.this, VideoActivity.class));
                break;

            case R.id.didi_button:
                break;

            case R.id.wlan_button:
                startActivity(new Intent(MainActivity.this, WifiActivity.class));
                break;

            case R.id.navigation_button:
                Intent navigationIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isManual", true);
                navigationIntent.putExtras(bundle);
                if (MapManager.getInstance().isNavi() || MapManager.getInstance().isNaviBack()) {
                    if (MapManager.getInstance().isNavi()) {
                        navigationIntent.setClass(MainActivity.this,
                                NaviCustomActivity.class);
                    } else if (MapManager.getInstance().isNaviBack()) {
                        navigationIntent.setClass(MainActivity.this,
                                NaviBackActivity.class);
                    }
                } else {
                    navigationIntent.setClass(MainActivity.this, LocationActivity.class);

                }

                startActivity(navigationIntent);
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
        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mRecordService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mRecordService = ((RecordBindService.MyBinder) service).getService();
                ((LauncherApplication) getApplicationContext())
                        .setRecordService(mRecordService);
            }
        };

        Intent intent = new Intent(mContext, RecordBindService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
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

    private void startOBDService() {
        Intent i = new Intent(MainActivity.this, OBDDataService.class);
        startService(i);
    }
}
