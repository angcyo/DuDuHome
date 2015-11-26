package com.dudu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * SharedPreferences工具类
 */
public class SharedPreferencesUtil {

    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferences getSharedPreferences(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static void putStringValue(Context context, String name, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(context, name).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void putStringValue(Context context, String key, String value) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringValue(Context context, String key, String defValue) {
        return  getDefaultSharedPreferences(context).getString(key, defValue);
    }

    public static String getStringValue(Context context, String name, String key, String defValue) {
        return getSharedPreferences(context, name).getString(key, defValue);
    }

    public static void putBooleanValue(Context context, String name, String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(context, name).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void putBooleanValue(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getDefaultSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBooleanValue(Context context, String key, boolean defValue) {
        return  getDefaultSharedPreferences(context).getBoolean(key, defValue);
    }

    public static boolean getBooleanValue(Context context, String name, String key, boolean defValue) {
        return getSharedPreferences(context, name).getBoolean(key, defValue);
    }

}
