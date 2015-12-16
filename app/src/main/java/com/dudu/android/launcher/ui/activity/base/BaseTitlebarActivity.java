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
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.DialogUtils;
import com.dudu.android.launcher.utils.NetworkUtils;
import com.dudu.android.launcher.utils.WeatherUtil;
import com.dudu.event.BleStateChange;
import com.dudu.event.DeviceEvent;
import com.dudu.monitor.Monitor;

import java.util.Iterator;

import de.greenrobot.event.EventBus;


public abstract class BaseTitlebarActivity extends BaseActivity {

    private static final String TAG = "BaseTitlebarActivity";

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

    private TelephonyManager mPhoneManager;

    private PhoneStateListener mPhoneStateListener;

    private TextView mSignalTextView;

    private ImageView mSignalImage;

    private ImageView mGpsSignalImage;

    private ImageView mVideoSignalImage;

    private ImageView mBluetoothImage;

    private int mSatellite = 0;

    private int mSignalLevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_custom_title);

        EventBus.getDefault().register(this);

        initTitleBar();

        mPhoneManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        mPhoneStateListener = new PhoneStateListener() {

            @Override
            public void onDataConnectionStateChanged(int state, int networkType) {
                super.onDataConnectionStateChanged(state, networkType);
            }

            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                try {
                    int level = (int) signalStrength.getClass().getMethod("getLevel").
                            invoke(signalStrength);
                    if (level != mSignalLevel) {
                        setSimLevel(level + 1);
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        };

        mPhoneManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS |
                PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);

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

        mBluetoothImage = (ImageView)getWindow().findViewById(R.id.bluetooth_img);
    }

    private void setSimLevel(int level) {
        if (!isCanUseSim()) {
            return;
        }

        if (level < 0 || level >= SIM_SIGNAL_IMAGE_IDS.length) {
            return;
        }

        mSignalLevel = level;
        mSignalImage.setImageResource(SIM_SIGNAL_IMAGE_IDS[level]);
    }

    private boolean isCanUseSim() {
        return mPhoneManager.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    private class ConnectivityChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String type = NetworkUtils.getCurrentNetworkType(BaseTitlebarActivity.this);
            if (type.equals("2G") || type.equals("3G") || type.equals("4G")) {
                // 当网络连接时重新获取天气信息
                WeatherUtil.requestWeatherInfo();

                mSignalTextView.setText(type);
                mSignalImage.setVisibility(View.VISIBLE);
            } else {
                mSignalTextView.setText(R.string.no_4g_signal);
                mSignalImage.setVisibility(View.GONE);
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

    public void onEventMainThread(DeviceEvent.Video event) {
        mVideoSignalImage.setImageResource(event.getState() == DeviceEvent.ON ?
                R.drawable.video_signal_recording : R.drawable.video_signal_stop);
    }

    public void onEventMainThread(BleStateChange event) {
        switch (event.getConnState()) {
            case BleStateChange.BLEDISCONNECTED:
                DialogUtils.showOBDErrorDialog(BaseTitlebarActivity.this);
                mBluetoothImage.setImageResource(R.drawable.bluetooth_off);
                break;
            case BleStateChange.BLECONNECTED:
                DialogUtils.dismissOBDErrorDialog(BaseTitlebarActivity.this);
                mBluetoothImage.setImageResource(R.drawable.bluetooth_on);
                break;
        }
    }

}
