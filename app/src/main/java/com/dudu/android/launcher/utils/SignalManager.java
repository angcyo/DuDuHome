package com.dudu.android.launcher.utils;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.event.DeviceEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by 赵圣琪 on 2015/12/17.
 */
public class SignalManager {

    private static SignalManager mInstance;

    private Context mContext;

    private int mSignalLevel = 0;

    private String mSignalType = "";

    private PhoneStateListener mPhoneStateListener;

    private TelephonyManager mPhoneManager;

    public static SignalManager getInstance() {
        if (mInstance == null) {
            mInstance = new SignalManager();
        }

        return mInstance;
    }

    private SignalManager() {
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
