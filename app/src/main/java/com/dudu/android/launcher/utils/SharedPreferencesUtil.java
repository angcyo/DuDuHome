package com.dudu.android.launcher.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

@SuppressLint("CommitPrefEdits")
public class SharedPreferencesUtil {

	/**
	 * 保存常用地址
	 * 
	 * @param mContext
	 * @param type
	 * @param address
	 */
	public static void saveFinalAddress(Context mContext, String type,
			String address) {
		SharedPreferences.Editor editor = mContext.getSharedPreferences(
				"dudu_reader", Activity.MODE_PRIVATE).edit();
		switch (type) {
		case Constants.HOME:
			editor.putString(Constants.HOME_TYPE, address);
			break;
		case Constants.HOMETOWN:
			editor.putString(Constants.HOMETOWN_TYPE, address);
			break;
		case Constants.COMPANY:
			editor.putString(Constants.COMPANY_TYPE, address);
			break;
		}

		editor.commit();
	}

	/**
	 * 获取常用地址
	 * 
	 * @param mContext
	 * @param type
	 * @return
	 */
	public static String getFinalAddress(Context mContext, String type) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				"dudu_reader", Activity.MODE_PRIVATE);
		return sharedPreferences.getString(type, "");
	}

	/**
	 * 保存缓存信息
	 * 
	 * @param mContext
	 * @param type
	 * @param address
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
	 * @param type
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
	 * @param type
	 * @return
	 */
	public static String getPreferences(Context mContext, String name) {
		return getPreferences(mContext, name, "-1");
	}
}
