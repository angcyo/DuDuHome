package com.dudu.android.launcher.ui.activity.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.DialogUtils;
import com.dudu.android.launcher.utils.NetworkUtils;
import com.dudu.android.launcher.utils.StatusBarManager;
import com.dudu.android.launcher.utils.WeatherUtil;
import com.dudu.android.launcher.utils.WeatherUtils;
import com.dudu.event.BleStateChange;
import com.dudu.event.DeviceEvent;
import com.dudu.monitor.Monitor;
import com.dudu.monitor.event.CarStatus;
import com.dudu.monitor.utils.LocationUtils;

import java.util.Iterator;

import de.greenrobot.event.EventBus;


public abstract class BaseTitlebarActivity extends BaseActivity {

    public static final String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    private static final int SIM_SIGNAL_IMAGE_IDS[] = {
            R.drawable.signal_0,
            R.drawable.signal_1,
            R.drawable.signal_2,
            R.drawable.signal_3,
            R.drawable.signal_4,
            R.drawable.signal_5
    };

    private ConnectivityChangeReceiver mConnectivityReceiver;

    private TextView mSignalTextView;

    private ImageView mSignalImage;

    private ImageView mGpsSignalImage;

    private ImageView mVideoSignalImage;

    private ImageView mBluetoothImage;

    private int mSatellite = 0;

    private int disConnectedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_custom_title);

        EventBus.getDefault().register(this);

        initTitleBar();

        mConnectivityReceiver = new ConnectivityChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CONNECTIVITY_CHANGE);
        registerReceiver(mConnectivityReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mConnectivityReceiver);

        EventBus.getDefault().unregister(this);
    }

    private void initTitleBar() {
        mSignalTextView = (TextView) getWindow().findViewById(R.id.signal_textview);

        mSignalImage = (ImageView) getWindow().findViewById(R.id.signal_image);

        mGpsSignalImage = (ImageView) getWindow().findViewById(R.id.gps_img);

        mVideoSignalImage = (ImageView) getWindow().findViewById(R.id.video_signal_image);

        mBluetoothImage = (ImageView) getWindow().findViewById(R.id.bluetooth_img);

        mVideoSignalImage.setImageResource(StatusBarManager.getInstance().isRecording() == DeviceEvent.ON ?
                R.drawable.video_signal_recording : R.drawable.video_signal_stop);

        setSimLevel(StatusBarManager.getInstance().getSignalLevel() + 1);

        mBluetoothImage.setImageResource(StatusBarManager.getInstance().getBleConnState() == BleStateChange.BLEDISCONNECTED ?
                R.drawable.bluetooth_off : R.drawable.bluetooth_on);


    }

    private void setSimLevel(int level) {
        if (level < 0 || level >= SIM_SIGNAL_IMAGE_IDS.length) {
            return;
        }

        mSignalImage.setImageResource(SIM_SIGNAL_IMAGE_IDS[level]);
    }

    private void setSimType(String type) {
        if (type.equals("2G") || type.equals("3G") || type.equals("4G")) {
            // 当网络连接时重新获取天气信息

            WeatherUtils.getInstance().startWeatherUnderstanding(LocationUtils.getInstance(this).getCurrentCity() + "今天的天气");

            mSignalTextView.setText(type);
            mSignalImage.setVisibility(View.VISIBLE);
        } else {
            mSignalTextView.setText(R.string.no_4g_signal);
            mSignalImage.setVisibility(View.GONE);
        }
    }

    private class ConnectivityChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_CONNECTIVITY_CHANGE)) {
                String type = NetworkUtils.getCurrentNetworkType(BaseTitlebarActivity.this);
                setSimType(type);
            }

        }
    }

    public void onEventMainThread(GpsStatus gpsStatus) {
        int maxSatellites = gpsStatus.getMaxSatellites();
        Iterator<GpsSatellite> iterator = gpsStatus.getSatellites()
                .iterator();
        mSatellite = 0;
        while (iterator.hasNext() && mSatellite <= maxSatellites) {
            mSatellite++;
        }

        if (mSatellite > 0 && (!Monitor.getInstance(this).getCurrentLocation().getProvider().equals("lbs"))) {
            mGpsSignalImage.setImageResource(R.drawable.gps_signal_normal);
        } else {
            mGpsSignalImage.setImageResource(R.drawable.gps_signal_error);
        }
    }

    public void onEventMainThread(CarStatus carStatus) {
        switch (carStatus) {
            case OFFLINE:
                mGpsSignalImage.setImageResource(R.drawable.gps_signal_error);
                break;
        }
    }

    public void onEventMainThread(DeviceEvent.Video event) {
        StatusBarManager.getInstance().setRecording(event.getState());
        mVideoSignalImage.setImageResource(event.getState() == DeviceEvent.ON ?
                R.drawable.video_signal_recording : R.drawable.video_signal_stop);
    }

    public void onEventMainThread(BleStateChange event) {
        StatusBarManager.getInstance().setBleConnState(event.getConnState());
        switch (event.getConnState()) {
            case BleStateChange.BLEDISCONNECTED:
                mBluetoothImage.setImageResource(R.drawable.bluetooth_off);
                disConnectedCount++;
                if (disConnectedCount >= 30)
                    DialogUtils.showOBDErrorDialog(BaseTitlebarActivity.this);
                break;
            case BleStateChange.BLECONNECTED:
                disConnectedCount = 0;
                DialogUtils.dismissOBDErrorDialog(BaseTitlebarActivity.this);
                mBluetoothImage.setImageResource(R.drawable.bluetooth_on);
                break;
        }
    }

    public void onEventMainThread(DeviceEvent.SimLevel simLevel) {
        setSimLevel(simLevel.getSimLevel() + 1);
    }

    public void onEventMainThread(DeviceEvent.SimType simType) {
        setSimType(simType.getSimType());
    }

}
