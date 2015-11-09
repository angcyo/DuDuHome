package com.dudu.android.launcher.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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
import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.LocationUtils;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.android.launcher.utils.Util;
import com.dudu.android.launcher.utils.WeatherIconsUtils;
import com.dudu.android.launcher.utils.WifiApAdmin;
import com.dudu.map.MapManager;
import com.dudu.obd.OBDDataService;
import com.dudu.voice.semantic.VoiceManager;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseTitlebarActivity implements
        OnClickListener, AMapLocalWeatherListener {

    private Button mVideoButton, mNavigationButton,
            mDiDiButton, mWlanButton;

    private LocationManagerProxy mLocationManagerProxy;

    private TextView mDateTextView, mWeatherView, mTemperatureView;

    private ImageView mWeatherImage;

    private LinearLayout mSelfCheckingView;

    private static int TIME = 10000;

    private Timer timer;

    private RecordBindService mRecordService;

    private ServiceConnection mServiceConnection;

    private Button mVoiceButton;

    private WifiApAdmin mWifiApAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startFloatMessageShowService();

        startOBDService();

        //检测蓝牙设配
        checkBlueTooth();

        requestWeatherInfo();
        //延迟10S开启热点
        new Handler().postDelayed(new Runnable() {
            public void run() {
//                startWifiAp();
            }
        }, TIME);
    }

    private void startWifiAp() {
        File directory = new File(FileUtils.getExternalStorageDirectory(), "nodogsplash");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, "nodogsplash.conf");
        if(file.exists()){
            //开启热点
            WifiApAdmin.startWifiAp(this);
        } else {
            try {
                file.createNewFile();
                //如果不存在就创建然后复制assets下的文件到此文件
                InputStream isAsset = getAssets().open("nodogsplash.conf");
                if (FileUtils.copyFileToSd(isAsset, file)){
                    //开启热点
                    WifiApAdmin.startWifiAp(this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkBlueTooth() {

        //初始化蓝牙的适配器
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                //如果蓝牙没有开启的话，则开启蓝牙
                bluetoothAdapter.enable();
            }
        }
    }

    @Override
    public int initContentView() {
        if (Util.isTaxiVersion()) {
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

        if (Util.isTaxiVersion()) {
            initTaxiView();
        } else {
            initCarView();
        }
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
                Intent intent;
                PackageManager packageManager = getPackageManager();
                intent = packageManager.getLaunchIntentForPackage("com.sdu.didi.gsui");
                if (intent != null) {
                    ((LauncherApplication) getApplication()).setReceivingOrder(true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    ToastUtils.showToast("您还没安装滴滴客户端，请先安装滴滴出行客户端");
                }

                break;

            case R.id.wlan_button:
                if (Util.isTaxiVersion()) {
                    startActivity(new Intent(MainActivity.this, WifiActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, ActivationActivity.class));
                }
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
                    navigationIntent.setClass(MainActivity.this, LocationMapActivity.class);

                }

                startActivity(navigationIntent);
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
    private void getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy年MM月dd日 EEEE", Locale.getDefault());
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
