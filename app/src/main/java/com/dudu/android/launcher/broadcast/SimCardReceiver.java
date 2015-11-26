package com.dudu.android.launcher.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.dudu.android.launcher.utils.DialogUtils;

public class SimCardReceiver extends BroadcastReceiver {

    private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_SIM_STATE_CHANGED)) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int state = tm.getSimState();
            switch (state) {
                case TelephonyManager.SIM_STATE_READY:
                    Log.e("SimCardReceiver", "sim card ready...");
                    DialogUtils.dismissWithoutSimCardDialog(context);
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                case TelephonyManager.SIM_STATE_ABSENT:
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    Log.e("SimCardReceiver", "sim card not ready...");
                    DialogUtils.showWithoutSimCardDialog(context);
                    break;
            }
        }
    }
}
