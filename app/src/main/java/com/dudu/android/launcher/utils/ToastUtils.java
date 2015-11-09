package com.dudu.android.launcher.utils;

import android.content.Context;
import android.widget.Toast;
import com.dudu.android.launcher.LauncherApplication;


public class ToastUtils {

	public static void showToast(int msgId) {
		Toast.makeText(LauncherApplication.mApplication, msgId, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(String message) {
		Toast.makeText(LauncherApplication.mApplication, message, Toast.LENGTH_SHORT).show();
	}

	public static void showTip(String message) {
		Toast.makeText(LauncherApplication.mApplication, message, Toast.LENGTH_SHORT).show();
	}

	public static void showTip(Context mContext, String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}
	
}
