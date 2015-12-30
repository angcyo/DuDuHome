package com.dudu.android.launcher.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.dudu.android.launcher.LauncherApplication;

/**
 * Created by lxh on 15/12/30.
 */
public class CarStatusUtils {
    private static String CARSTATUS_SP = "CARSTATUS_SP";

    private static String CARSTATUS = "CARSTATUS";

    public static void saveCarStatus(boolean status){
        SharedPreferences sp = LauncherApplication.getContext()
                .getSharedPreferences(CARSTATUS_SP, Context.MODE_PRIVATE);

        sp.edit().putBoolean(CARSTATUS,status).commit();
    }

    public static boolean isCarOnline(){
        SharedPreferences sp = LauncherApplication.getContext()
                .getSharedPreferences(CARSTATUS_SP, Context.MODE_PRIVATE);
        return sp.getBoolean(CARSTATUS,true);
    }
}
