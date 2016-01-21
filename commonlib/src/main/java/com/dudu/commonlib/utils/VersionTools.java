package com.dudu.commonlib.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.dudu.commonlib.CommonLib;

/**
 * Created by dengjun on 2016/1/21.
 * Description :
 */
public class VersionTools {
    public static String getAppVersion(Context context){
        PackageInfo packageInfo = null;
        String versionName = "";
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
