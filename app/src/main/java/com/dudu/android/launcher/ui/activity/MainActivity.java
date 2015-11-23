package com.dudu.android.launcher.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
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
import android.view.Window;
import android.view.WindowManager;
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
import com.dudu.android.launcher.service.NewMessageShowService;
import com.dudu.android.launcher.service.RecordBindService;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;
import com.dudu.android.launcher.ui.activity.video.VideoActivity;
import com.dudu.android.launcher.ui.dialog.BluetoothAlertDialog;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.LocationUtils;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.android.launcher.utils.Utils;
import com.dudu.android.launcher.utils.WeatherIconsUtils;
import com.dudu.android.launcher.utils.WifiApAdmin;
import com.dudu.event.BleStateChange;
import com.dudu.event.DeviceEvent;
import com.dudu.event.InitEvent;
import com.dudu.map.MapManager;
import com.dudu.obd.OBDDataService;
import com.dudu.voice.semantic.VoiceManager;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ch.qos.logback.core.android.SystemPropertiesProxy;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.functions.Action1;

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

    private BluetoothAlertDialog bluetoothDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log_init = LoggerFactory.getLogger("init.start");
        log_step = 0;
        super.onCreate(savedInstanceState);

        log_init.debug("[main][{}]register EventBus", log_step++);
        EventBus.getDefault().register(this);

        startFloatMessageShowService();

        getDate();

        initVideoService();

        initWeatherInfo();

        //添加生产测试判断
        log_init.debug("[main][{}]checkBTFT after 5s", log_step++);
        Observable.timer(5, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(final Long aLong) {
                        EventBus.getDefault().post(new InitEvent.CheckBtFt());
                    }
                });
    }

    public void onEventMainThread(InitEvent.CheckBtFt event) {
        log_init.debug("[main][{}]start checkBTFT", log_step++);
        checkBTFT();
    }

    private void checkBTFT() {
        SystemPropertiesProxy sps = SystemPropertiesProxy.getInstance();
        boolean need_bt = !"1".equals(sps.get("persist.sys.bt", "0"));
        boolean need_ft = !"1".equals(sps.get("persist.sys.ft", "0"));
        Intent intent;
        PackageManager packageManager = getPackageManager();
        intent = packageManager.getLaunchIntentForPackage("com.qualcomm.factory");
        log_init.debug("[main][{}] bt:{}, ft:{}, app:{}", log_step++, !need_bt, !need_ft, intent != null);
        if ((need_bt || need_ft) && intent != null) {
            //close wifi ap for ft test
            WifiApAdmin.closeWifiAp(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            initAfterFT();
        }
    }

    private void initAfterFT() {
        log_init.debug("[main][{}]initAfterFT", log_step++);

        //关闭ADB调试端口
        if (!Utils.isDemoVersion(this)) {
            com.dudu.android.hideapi.SystemPropertiesProxy.getInstance().set(mContext, "persist.sys.usb.config", "charging");
        }

        // 设置使用v5+
        StringBuffer param = new StringBuffer();
        param.append("appid=" + Constants.XUFEIID);
        param.append(",");
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);

        log_init.debug("[main][{}]SpeechUtility createUtility", log_step++);
        SpeechUtility.createUtility(this, param.toString());

        log_init.debug("[main][{}]VoiceWakeuper startWakeup", log_step++);
        VoiceManager.getInstance().startWakeup();

        //延迟10S开启其他服务
        Observable.timer(10, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(final Long aLong) {
                        EventBus.getDefault().post(new InitEvent.InitAfter10s());
                    }
                });
    }

    public void onEventMainThread(InitEvent.InitAfter10s event) {
        initAfter10s();
    }

    private void initAfter10s() {
        log_init.debug("initAfter10s");

        WifiApAdmin.initWifiApState(this);

        openBlueTooth();

        startOBDService();
    }

    private void startOBDService() {
        log_init.debug("startOBDService");
        com.dudu.android.hideapi.SystemPropertiesProxy.getInstance().set(mContext, "persist.sys.gps", "start");
        Intent i = new Intent(this, OBDDataService.class);
        startService(i);
    }

    private void openBlueTooth() {
        //初始化蓝牙的适配器
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                //如果蓝牙没有开启的话，则开启蓝牙
                log_init.debug("bluetoothAdapter.enable");
                bluetoothAdapter.enable();
            }
        }
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
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);

        if (timer != null) {
            timer.cancel();
        }

        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }

        super.onDestroy();
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
                if (Utils.isTaxiVersion()) {
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
        }
    }

    private void startFloatMessageShowService() {
        log_init.debug("[main][{}]startFloatMessageShowService", log_step++);
        Intent i = new Intent(MainActivity.this, NewMessageShowService.class);
        startService(i);
    }

    private void showBleDialog() {
        if (Utils.isDemoVersion(this)) {
            return;
        }

        if (bluetoothDialog != null && bluetoothDialog.isShowing()) {
            return;
        }

        bluetoothDialog = new BluetoothAlertDialog(mContext);
        Window dialogWindow = bluetoothDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 10; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.alpha = 0.8f; // 透明度
        dialogWindow.setAttributes(lp);
        bluetoothDialog.show();
    }

    private void disMissbluetoothDialog() {
        if (Utils.isDemoVersion(this)) {
            return;
        }

        if (bluetoothDialog != null && bluetoothDialog.isShowing()) {
            bluetoothDialog.cancel();
            bluetoothDialog = null;
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
                showBleDialog();
                break;
            case BleStateChange.BLECONNECTED:
                disMissbluetoothDialog();
                break;
        }

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e2.getX() - e1.getX() > 400 && e2.getY() - e1.getY() > 400) {
            //关闭语音
            VoiceManager.getInstance().stopUnderstanding();
            VoiceManager.getInstance().stopWakeup();
            PackageManager packageManager = MainActivity.this.getPackageManager();
            startActivity(new Intent(packageManager.getLaunchIntentForPackage("com.qualcomm.factory")));
        }
        if (e2.getX() - e1.getX() > 400 && e1.getY() - e2.getY() > 400) {
            com.dudu.android.hideapi.SystemPropertiesProxy.getInstance().set(mContext, "persist.sys.usb.config", "diag,serial_smd,rmnet_bam,adb");
        }
        return true;
    }
}
