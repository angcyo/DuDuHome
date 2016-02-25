package com.dudu.android.launcher.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dudu.android.launcher.ui.activity.bluetooth.BtCallingActivity;
import com.dudu.android.launcher.ui.activity.bluetooth.BtDialActivity;
import com.dudu.android.launcher.ui.activity.bluetooth.BtInCallActivity;
import com.dudu.android.launcher.ui.activity.bluetooth.BtOutCallActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.LogUtils;

/**
 * Created by 赵圣琪 on 2016/1/18.
 */
public class BtCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.v("phone", "接收到蓝牙电话广播: " + action);

        if (action.equals(Constants.ACTION_BLUETOOTH_PHONE_INCALL)) {

            startBtPhoneActivity(context, BtInCallActivity.class, intent);

        } else if (action.equals(Constants.ACTION_BLUETOOTH_PHONE_OUTCALL)) {

            startBtPhoneActivity(context, BtOutCallActivity.class, intent);

        } else if (action.equals(Constants.ACTION_BLUETOOTH_PHONE_CONNECT)) {

            startBtPhoneActivity(context, BtCallingActivity.class, intent);

        } else if (action.equals(Constants.ACTION_BLUETOOTH_PHONE_END)) {
            ActivitiesManager.getInstance().closeTargetActivity(BtInCallActivity.class);
            ActivitiesManager.getInstance().closeTargetActivity(BtOutCallActivity.class);
            ActivitiesManager.getInstance().closeTargetActivity(BtCallingActivity.class);
            ActivitiesManager.getInstance().closeTargetActivity(BtDialActivity.class);
        }
    }

    private void startBtPhoneActivity(Context context,
                                      Class<? extends Activity> clzz, Intent intent) {
        Intent i = new Intent(context, clzz);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(Constants.EXTRA_PHONE_NUMBER, intent.getStringExtra("HFP_NUMBER"));
        i.putExtra(Constants.EXTRA_CONTACT_NAME,intent.getStringExtra("HFT_NAME"));
        context.startActivity(i);
    }
}
