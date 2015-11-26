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
import com.dudu.android.launcher.utils.LocationUtils;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.NetworkUtils;
import com.dudu.event.DeviceEvent;

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

    private int mSimLevel = 0;

    private ImageView mGpsSignalImage;

    private ImageView mVideoSignalImage;

    private int satellite = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_custom_title);

        EventBus.getDefault().register(this);

        mPhoneManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        mPhoneStateListener = new PhoneStateListener() {

            @Override
            public void onDataConnectionStateChanged(int state, int networkType) {
                super.onDataConnectionStateChanged(state, networkType);
                LogUtils.e(TAG, "networkType: " + networkType);
            }

            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                try {
                    int level = (int) signalStrength.getClass().getMethod("getLevel").
                            invoke(signalStrength);
                    LogUtils.e(TAG, "signal level: " + level);
                } catch (Exception e) {
                    LogUtils.e(TAG, e.getMessage() + "");
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
        TextView textView = (TextView) getWindow().findViewById(
                R.id.signal_textview);
        String type = NetworkUtils.getCurrentNetworkType(mContext);
        textView.setText(type);

        mGpsSignalImage = (ImageView) findViewById(R.id.gps_img);

        mVideoSignalImage = (ImageView) findViewById(R.id.video_signal_image);
    }

    private void updateSimSignalLevel() {

    }

    private class ConnectivityChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            initTitleBar();
        }
    }

    public void onEventMainThread(GpsStatus gpsStatus) {
        int maxSatellites = gpsStatus.getMaxSatellites();
        Iterator<GpsSatellite> iterator = gpsStatus.getSatellites()
                .iterator();
        satellite = 0;
        while (iterator.hasNext() && satellite <= maxSatellites) {
            satellite++;
        }

        if (satellite > 0 && (!LocationUtils.getInstance(this).getLocProvider().equals("lbs"))) {
            mGpsSignalImage.setBackgroundResource(R.drawable.gps_signal_normal);
        } else {
            mGpsSignalImage.setBackgroundResource(R.drawable.gps_signal_error);
        }
    }

    public void onEventMainThread(DeviceEvent.Video event) {
        mVideoSignalImage.setVisibility(event.getState() == DeviceEvent.ON ?
                View.VISIBLE : View.INVISIBLE);
    }

}
