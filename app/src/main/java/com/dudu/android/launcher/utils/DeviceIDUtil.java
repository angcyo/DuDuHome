package com.dudu.android.launcher.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.UUID;

/**
 * Created by lxh on 2015/11/6.
 */
public class DeviceIDUtil {

    /**
     * Android_ID
     * @param context
     * @return
     */
    public static String getAndroidID(Context context){

       String androidId = ""+android.provider.Settings.Secure.getString(context.getContentResolver(),
               android.provider.Settings.Secure.ANDROID_ID);
        return  androidId;
    }


    /**
     * SIM 卡的序列号
     * @param context
     * @return
     */
    public static String getSimSerialNumber(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimSerialNumber();

    }

    /**
     * IMEI
     * @param context
     * @return
     */
    public static String getIMEI(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }
}
