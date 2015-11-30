package com.dudu.network.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

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


    public static  String  getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            DuduLog.e("读版本异常", e);
            return "";
        }
    }

}
