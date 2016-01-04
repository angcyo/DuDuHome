package com.dudu.android.launcher.ui.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
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

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.broadcast.TFlashCardReceiver;
import com.dudu.android.launcher.broadcast.WeatherAlarmReceiver;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;
import com.dudu.android.launcher.ui.activity.video.VideoActivity;
import com.dudu.android.launcher.ui.dialog.IPConfigDialog;
import com.dudu.android.launcher.utils.AgedUtils;
import com.dudu.android.launcher.utils.Utils;
import com.dudu.android.launcher.utils.WeatherUtil;
import com.dudu.android.launcher.utils.WifiApAdmin;
import com.dudu.event.DeviceEvent;
import com.dudu.event.VoiceEvent;
import com.dudu.init.InitManager;
import com.dudu.map.NavigationClerk;
import com.dudu.navi.event.NaviEvent;
import com.dudu.obd.ObdInit;
import com.dudu.video.VideoManager;
import com.dudu.voice.semantic.VoiceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;


public class MainActivity extends BaseTitlebarActivity implements
        OnClickListener {

    private static final int START_RECORDING = 1;

    private static final int STOP_RECORDING = 2;

    private static final int START_VOICE_SERVICE = 3;

    private Button mVideoButton, mNavigationButton,
            mDiDiButton, mWlanButton;

    private TextView mDateTextView, mWeatherView, mTemperatureView;

    private VideoManager mVideoManager;

    private ImageView mWeatherImage;

    private LinearLayout mSelfCheckingView;

    private AlarmManager mAlarmManager;

    private HandlerThread mWorkerThread;

    private WorkerHandler mWorkerHandler;

    private TFlashCardReceiver mTFlashCardReceiver;

    private Button mVoiceButton;

    private Logger log_init;

    private class WorkerHandler extends Handler {
        public WorkerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_RECORDING:
                    mVideoManager.startRecord();
                    break;
                case STOP_RECORDING:
                    mVideoManager.stopRecord();
                    break;
                case START_VOICE_SERVICE:
                    VoiceManager.getInstance().startVoiceService();
                    break;
            }
        }
    }

    private void startRecording() {
        mWorkerHandler.sendMessage(mWorkerHandler.obtainMessage(START_RECORDING));
    }

    private void stopRecording() {
        mWorkerHandler.sendMessage(mWorkerHandler.obtainMessage(STOP_RECORDING));
    }

    private void startVoiceService() {
        mWorkerHandler.sendMessage(mWorkerHandler.obtainMessage(START_VOICE_SERVICE));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log_init = LoggerFactory.getLogger("init.start");

        log_init.debug("MainActivity 调用onCreate方法初始化...");

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);

        InitManager.getInstance().init();

        initDate();

        setWeatherAlarm();

        mVideoManager = VideoManager.getInstance();

        mWorkerThread = new HandlerThread("video and voice worker thread");
        mWorkerThread.start();

        mWorkerHandler = new WorkerHandler(mWorkerThread.getLooper());

       registerTFlashCardReceiver();
    }

    private void registerTFlashCardReceiver() {
        mTFlashCardReceiver = new TFlashCardReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addDataScheme("file");
        registerReceiver(mTFlashCardReceiver, intentFilter);
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

        if (Utils.isDemoVersion(this)) {
            mWlanButton.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    EventBus.getDefault().post(new VoiceEvent(VoiceEvent.INIT_RECORDING_SERVICE));
                    AgedUtils.proceedAgeTest(MainActivity.this);
                    return true;
                }
            });
        }

        mVideoButton.setOnClickListener(this);
        mNavigationButton.setOnClickListener(this);

        if (Utils.isDemoVersion(this)) {
            mDiDiButton.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings",
                            "com.android.settings.Settings"));
                    startActivity(intent);
                    //显示返回的按钮
                    EventBus.getDefault().post(NaviEvent.FloatButtonEvent.SHOW);
                    return true;
                }
            });
        }

        mWlanButton.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                new IPConfigDialog().showDialog(MainActivity.this);
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
        super.onDestroy();

        log_init.debug("MainActivity 调用onDestroy释放资源...");

        InitManager.getInstance().unInit();

        cancelWeatherAlarm();

        unregisterReceiver(mTFlashCardReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_button:
                startActivity(new Intent(MainActivity.this, VideoActivity.class));
                break;

            case R.id.didi_button:
                Utils.openJD(this);
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
                if (VoiceManager.getInstance().isUnderstandingOrSpeaking()) {
                    return;
                }

                EventBus.getDefault().post(new VoiceEvent(VoiceEvent.INIT_VOICE_SERVICE));
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

    private void setWeatherAlarm() {
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(MainActivity.this, WeatherAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 30 * 60 * 1000, pi);
    }

    private void cancelWeatherAlarm() {
        Intent intent = new Intent(MainActivity.this, WeatherAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        mAlarmManager.cancel(pi);
    }

    private void updateWeatherInfo(String weather, String temperature) {
        LinearLayout ll_weatherInfo = (LinearLayout) findViewById(R.id.ll_weather_info);
        RelativeLayout.LayoutParams lps = (RelativeLayout.LayoutParams) ll_weatherInfo.getLayoutParams();
        if (!TextUtils.isEmpty(weather) && !TextUtils.isEmpty(temperature)) {
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
            mWeatherImage.setImageResource(WeatherUtil
                    .getWeatherIcon(WeatherUtil.getWeatherType(weather)));
        } else {
            Toast.makeText(this, R.string.get_weather_info_failed,
                    Toast.LENGTH_SHORT).show();
            lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mWeatherView.setGravity(Gravity.CENTER);
            mWeatherView.setText(R.string.unkown_weather_info);
            mTemperatureView.setText("");
        }
    }

    public void onEventMainThread(DeviceEvent.Screen event) {
        log_init.debug("DeviceEvent.Screen {}", event.getState());
        com.dudu.android.hideapi.SystemPropertiesProxy.getInstance()
                .set(MainActivity.this, "persist.sys.screen", event.getState() == DeviceEvent.ON ? "on" : "off");
    }

    public void onEventMainThread(VoiceEvent event) {
        switch (event.getVoiceEvent()) {
            case VoiceEvent.INIT_VOICE_SERVICE:
                VoiceManager.setUnderstandingOrSpeaking(true);

                stopRecording();

                startVoiceService();

                startRecording();
                break;

            case VoiceEvent.STOP_VOICE_SERVICE:
                VoiceManager.setUnderstandingOrSpeaking(false);

                stopRecording();

                startRecording();
                break;
            case VoiceEvent.INIT_RECORDING_SERVICE:
                stopRecording();

                startRecording();
                break;
        }
    }

    public void onEventMainThread(DeviceEvent.Weather weather) {
        updateWeatherInfo(weather.getWeather(), weather.getTemperature());
        initDate();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e2.getX() - e1.getX() > 400 && e2.getY() - e1.getY() > 400) {
            if (!InitManager.getInstance().isFinished()) {
                return true;
            }

            //关闭语音
            VoiceManager.getInstance().stopUnderstanding();

            //关闭Portal
            com.dudu.android.hideapi.SystemPropertiesProxy.getInstance().set(MainActivity.this, "persist.sys.nodog", "stop");

            mVideoManager.stopRecord();

            //关闭热点
            WifiApAdmin.closeWifiAp(this);

            //stop bluetooth
            ObdInit.uninitOBD(this);

            PackageManager packageManager = MainActivity.this.getPackageManager();
            startActivity(new Intent(packageManager.getLaunchIntentForPackage("com.qualcomm.factory")));
        }
        if (e2.getX() - e1.getX() > 400 && e1.getY() - e2.getY() > 360) {
            com.dudu.android.hideapi.SystemPropertiesProxy.getInstance().set(MainActivity.this, "persist.sys.usb.config", "diag,serial_smd,rmnet_bam,adb");
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().post(NaviEvent.FloatButtonEvent.HIDE);
    }

}
