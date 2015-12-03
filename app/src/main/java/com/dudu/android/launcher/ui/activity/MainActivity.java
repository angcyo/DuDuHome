package com.dudu.android.launcher.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.service.RecordBindService;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;
import com.dudu.android.launcher.ui.activity.video.VideoActivity;
import com.dudu.android.launcher.utils.DialogUtils;
import com.dudu.android.launcher.utils.LocationUtils;
import com.dudu.android.launcher.utils.Utils;
import com.dudu.android.launcher.utils.WeatherIconsUtils;
import com.dudu.android.launcher.utils.WifiApAdmin;
import com.dudu.event.BleStateChange;
import com.dudu.event.DeviceEvent;
import com.dudu.init.InitManager;
import com.dudu.map.NavigationClerk;
import com.dudu.monitor.event.CarStatus;
import com.dudu.navi.event.NaviEvent;
import com.dudu.voice.semantic.VoiceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;


public class MainActivity extends BaseTitlebarActivity implements
        OnClickListener, AMapLocalWeatherListener {

    private Button mVideoButton, mNavigationButton,
            mDiDiButton, mWlanButton;
    private LocationManagerProxy mLocationManagerProxy;
    private TextView mDateTextView, mWeatherView, mTemperatureView;
    private ImageView mWeatherImage;
    private LinearLayout mSelfCheckingView;
    private Timer timer;

    private RecordBindService mRecordService;

    private ServiceConnection mServiceConnection;

    private Button mVoiceButton;

    private Logger log_init;

    private int log_step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log_init = LoggerFactory.getLogger("init.start");
        log_step = 0;
        super.onCreate(savedInstanceState);

        initVideoService();

        InitManager.getInstance(this).init();

        initDate();

        initWeatherInfo();
    }

    @Override
    public int initContentView() {
        if (Utils.isTaxiVersion()) {
            return R.layout.activity_taxi_main_layout;
        }

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

        if (Utils.isTaxiVersion()) {
            initTaxiView();
        } else {
            initCarView();
        }
    }

    @Override
    public void initListener() {
        mDiDiButton.setOnClickListener(this);
        mWlanButton.setOnClickListener(this);

        mWlanButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EventBus.getDefault().post(new CarStatus(CarStatus.CAR_ONLINE));
                return true;
            }
        });

        mVideoButton.setOnClickListener(this);
        mNavigationButton.setOnClickListener(this);
        mDiDiButton.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings",
                        "com.android.settings.Settings"));
                startActivity(intent);
                EventBus.getDefault().post(new CarStatus(CarStatus.CAR_OFFLINE));
                return true;
            }
        });
    }

    private void initTaxiView() {
        mVoiceButton = (Button) findViewById(R.id.voice_button);
        mVoiceButton.setOnClickListener(this);
    }

    private void initCarView() {
        mSelfCheckingView = (LinearLayout) findViewById(R.id.self_checking_container);
        mSelfCheckingView.setOnClickListener(this);
    }

    @Override
    public void initDatas() {

    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }

        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }

        super.onDestroy();

        InitManager.getInstance(this).unInit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_button:
                mRecordService.startRecord();
                startActivity(new Intent(MainActivity.this, VideoActivity.class));
                break;

            case R.id.didi_button:
                Utils.startThirdPartyApp(MainActivity.this, "com.sdu.didi.gsui", R.string.error_no_didi);
                break;

            case R.id.wlan_button:
                if (Utils.isTaxiVersion()) {
                    startActivity(new Intent(MainActivity.this, WifiActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, ActivationActivity.class));
                }
                break;

            case R.id.navigation_button:
                NavigationClerk.getInstance().openNavi(NavigationClerk.OPEN_MANUAL);
                break;

            case R.id.self_checking_container:
                startActivity(new Intent(MainActivity.this, OBDCheckingActivity.class));
                break;

            case R.id.voice_button:
                VoiceManager.getInstance().startVoiceService();
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
    private void initDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy年MM月dd日 EEEE", Locale.getDefault());
        mDateTextView.setText(dateFormat.format(new Date()));
    }

    /**
     * 实例化录像服务
     */
    private void initVideoService() {
        log_init.debug("[main][{}]initVideoService", log_step++);
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
        log_init.debug("[main][{}]requestWeatherInfo, lmp={}", log_step++, mLocationManagerProxy);
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.requestWeatherUpdates(
                    LocationManagerProxy.WEATHER_TYPE_LIVE, MainActivity.this);
        }
    }


    @Override
    public void onWeatherForecaseSearched(AMapLocalWeatherForecast arg0) {

    }

    @Override
    public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {
        LinearLayout ll_weatherInfo = (LinearLayout) findViewById(R.id.ll_weather_info);

        RelativeLayout.LayoutParams lps = (RelativeLayout.LayoutParams) ll_weatherInfo.getLayoutParams();

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

            if (weather.length() == 1) {
                lps.removeRule(RelativeLayout.CENTER_HORIZONTAL);
            } else {
                lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
            }
            mWeatherView.setText(weather);
            mWeatherImage.setImageResource(WeatherIconsUtils
                    .getWeatherIcon(WeatherIconsUtils.getWeatherType(weather)));
            mWeatherImage.setImageResource(R.drawable.weather_cloudy);
            LocationUtils.getInstance(this).setCurrentCity(aMapLocalWeatherLive.getCity());
            LocationUtils.getInstance(this).setCurrentCitycode(aMapLocalWeatherLive.getCityCode());
        } else {
            Toast.makeText(this, R.string.get_weather_info_failed,
                    Toast.LENGTH_SHORT).show();
            lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mWeatherView.setGravity(Gravity.CENTER);
            mWeatherView.setText(R.string.unkown_weather_info);
            mTemperatureView.setText("");
        }
    }

    public void onEventMainThread(DeviceEvent.GPS event) {
        com.dudu.android.hideapi.SystemPropertiesProxy.getInstance()
                .set(mContext, "persist.sys.gps", event.getState() == DeviceEvent.ON ? "start" : "stop");
    }

    public void onEventMainThread(DeviceEvent.Screen event) {
        com.dudu.android.hideapi.SystemPropertiesProxy.getInstance()
                .set(mContext, "persist.sys.screen", event.getState() == DeviceEvent.ON ? "on" : "off");
    }

    public void onEventMainThread(BleStateChange event) {
        switch (event.getConnState()) {
            case BleStateChange.BLEDISCONNECTED:
                DialogUtils.showOBDErrorDialog(MainActivity.this);
                break;
            case BleStateChange.BLECONNECTED:
                DialogUtils.dismissOBDErrorDialog(MainActivity.this);
                break;
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e2.getX() - e1.getX() > 400 && e2.getY() - e1.getY() > 400) {
            //关闭语音
            VoiceManager.getInstance().stopUnderstanding();
            VoiceManager.getInstance().stopWakeup();
            //关闭Portal
            com.dudu.android.hideapi.SystemPropertiesProxy.getInstance().set(mContext, "persist.sys.nodog", "stop");
            //关闭热点
            WifiApAdmin.closeWifiAp(mActivity);
            PackageManager packageManager = MainActivity.this.getPackageManager();
            startActivity(new Intent(packageManager.getLaunchIntentForPackage("com.qualcomm.factory")));
        }
        if (e2.getX() - e1.getX() > 400 && e1.getY() - e2.getY() > 400) {
            com.dudu.android.hideapi.SystemPropertiesProxy.getInstance().set(mContext, "persist.sys.usb.config", "diag,serial_smd,rmnet_bam,adb");
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().post(NaviEvent.FloatButtonEvent.HIDE);
    }

}
