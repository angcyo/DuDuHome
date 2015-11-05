package com.dudu.android.launcher.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

@SuppressLint("CommitPrefEdits")
public class SharedPreferencesUtil {



	/**
	 * 保存缓存信息
	 * 
	 * @param mContext
	 */
	public static void savePreferences(Context mContext, String name,
			String value) {
		SharedPreferences.Editor editor = mContext.getSharedPreferences(
				"dudu_reader", Activity.MODE_PRIVATE).edit();
		editor.putString(name, value);
		editor.commit();
	}

	/**
	 * 获取缓存信息
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getPreferences(Context mContext, String name,
			String defaultValue) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"dudu_reader", Activity.MODE_PRIVATE);
		return sharedPreferences.getString(name, defaultValue);
	}

	/**
	 * 获取缓存信息
	 * 
	 * @param mContext
	 */
	public static String getPreferences(Context mContext, String name) {
		return getPreferences(mContext, name, "-1");
	}
}
