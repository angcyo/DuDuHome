package com.dudu.aios.ui.fragment.base;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dudu.aios.ui.activity.MainRecordActivity;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.DialogUtils;
import com.dudu.android.launcher.utils.NetworkUtils;
import com.dudu.android.launcher.utils.StatusBarManager;

import com.dudu.android.launcher.utils.WeatherUtils;
import com.dudu.event.BleStateChange;
import com.dudu.event.DeviceEvent;
import com.dudu.monitor.Monitor;

import java.util.Iterator;

import de.greenrobot.event.EventBus;

public abstract class BaseFragment extends Fragment {

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


    private LinearLayout childViewContainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.base_title_layout, container, false);

        initData();

        initView(view);

        return view;
    }

    private void initData() {

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);

        mConnectivityReceiver = new ConnectivityChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CONNECTIVITY_CHANGE);
        getActivity().registerReceiver(mConnectivityReceiver, intentFilter);
    }


    public abstract View getChildView();

    private void initView(View view) {
        childViewContainer = (LinearLayout) view.findViewById(R.id.child_container);

        mSignalTextView = (TextView) view.findViewById(R.id.signal_textview);

        mSignalImage = (ImageView) view.findViewById(R.id.signal_image);

        mGpsSignalImage = (ImageView) view.findViewById(R.id.gps_img);

        mVideoSignalImage = (ImageView) view.findViewById(R.id.video_signal_image);

        mBluetoothImage = (ImageView) view.findViewById(R.id.bluetooth_img);

        childViewContainer.addView(getChildView());

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

    private class ConnectivityChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_CONNECTIVITY_CHANGE)) {
                String type = NetworkUtils.getCurrentNetworkType(getActivity());
                setSimType(type);
            }

        }
    }

    private void setSimType(String type) {
        if (type.equals("2G") || type.equals("3G") || type.equals("4G")) {
            // 当网络连接时重新获取天气信息

            WeatherUtils.requestWeather(getActivity());

            mSignalTextView.setText(type);
            mSignalImage.setVisibility(View.VISIBLE);
        } else {
            mSignalTextView.setText(R.string.no_4g_signal);
            mSignalImage.setVisibility(View.GONE);
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

        if (mSatellite > 0 && (!Monitor.getInstance(getActivity()).getCurrentLocation().getProvider().equals("lbs"))) {
            mGpsSignalImage.setImageResource(R.drawable.gps_signal_normal);
        } else {
            mGpsSignalImage.setImageResource(R.drawable.gps_signal_error);
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
                    DialogUtils.showOBDErrorDialog(getActivity());
                break;
            case BleStateChange.BLECONNECTED:
                disConnectedCount = 0;
                DialogUtils.dismissOBDErrorDialog(getActivity());
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unregisterReceiver(mConnectivityReceiver);

        EventBus.getDefault().unregister(this);

    }

    public void replaceFragment(String name) {
        MainRecordActivity activity = (MainRecordActivity) getActivity();
        activity.replaceFragment(name);
    }
}
