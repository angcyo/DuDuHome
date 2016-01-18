package com.dudu.android.launcher.utils;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.event.DeviceEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by 赵圣琪 on 2015/12/17.
 */
public class StatusBarManager {

    private static StatusBarManager mInstance;

    private Context mContext;

    private int mSignalLevel = 0;

    private String mSignalType = "";

    private PhoneStateListener mPhoneStateListener;

    private TelephonyManager mPhoneManager;

    private int isRecording = 0;

    private int bleConnState = 0;

    public int getBleConnState() {
        return bleConnState;
    }

    public void setBleConnState(int bleConnState) {
        this.bleConnState = bleConnState;
    }

    public int isRecording() {
        return isRecording;
    }

    public void setRecording(int isRecording) {
        this.isRecording = isRecording;
    }

    public int getSignalLevel() {
        return mSignalLevel;
    }

    public static StatusBarManager getInstance() {
        if (mInstance == null) {
            mInstance = new StatusBarManager();
        }

        return mInstance;
    }

    private StatusBarManager() {
        mContext = LauncherApplication.getContext();

        mPhoneManager = (TelephonyManager) mContext.getSystemService(
                Context.TELEPHONY_SERVICE);
        mPhoneStateListener = new PhoneStateListener() {

            @Override
            public void onDataConnectionStateChanged(int state, int networkType) {
                super.onDataConnectionStateChanged(state, networkType);
                String type = NetworkUtils.getCurrentNetworkType(mContext);

                if (type.equals("2G") || type.equals("3G") || type.equals("4G")) {
                    setSimType(type);
                }
            }

            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                try {
                    int level = (int) signalStrength.getClass().getMethod("getLevel").
                            invoke(signalStrength);

                    setSimLevel(level);
                } catch (Exception e) {
                    // 忽略
                }
            }
        };
    }

    public void registerSignalListener() {
        mPhoneManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS |
                PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        // EventBus.getDefault().register(mContext);
    }

    private boolean isCanUseSim() {
        return mPhoneManager.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    private void setSimType(String type) {
        if (!isCanUseSim()) {
            return;
        }

        if (!mSignalType.equals(type)) {
            mSignalType = type;
            Log.v("DeviceEvent", "Type");
            EventBus.getDefault().post(new DeviceEvent.SimType(type));
        }
    }

    private void setSimLevel(int level) {
        if (!isCanUseSim()) {
            return;
        }

        if (mSignalLevel != level) {
            mSignalLevel = level;
            EventBus.getDefault().post(new DeviceEvent.SimLevel(level));
        }
    }
}
