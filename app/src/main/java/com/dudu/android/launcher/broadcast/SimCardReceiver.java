package com.dudu.android.launcher.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.dudu.android.launcher.utils.DialogUtils;
import com.dudu.android.launcher.utils.Utils;

public class SimCardReceiver extends BroadcastReceiver {

    private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_SIM_STATE_CHANGED)) {
            Utils.checkSimCardState(context);
        }
    }
}
