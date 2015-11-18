package com.dudu.android.launcher.utils;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by lxh on 2015/11/17.
 */
public class WeakLock {

    // 熄屏
    public static void weakLock(Context context){

        final DevicePolicyManager policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, AdminReceiver.class);

        if (policyManager.isAdminActive(componentName)) {
            policyManager.lockNow();
        } else {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            // 权限列表
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            context.startActivity(intent);
            policyManager.lockNow();

        }

    }

    // 亮屏
    public static void weakUpScreen(Context context){

       PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE,  context.getClass().getCanonicalName());
        wakeLock.acquire();
        wakeLock.setReferenceCounted(false);
    }



    public static class AdminReceiver extends DeviceAdminReceiver{

    }
}
