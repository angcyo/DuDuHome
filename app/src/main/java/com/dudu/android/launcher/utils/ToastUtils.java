package com.dudu.android.launcher.utils;

import java.util.Random;

import android.content.Context;
import android.widget.Toast;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;

public class ToastUtils {

	public static void showToast(String message) {
		Toast.makeText(LauncherApplication.mApplication, message, Toast.LENGTH_SHORT).show();
	}

	public static void showTip(String msg) {
		Toast.makeText(LauncherApplication.mApplication, msg, Toast.LENGTH_SHORT).show();
	}

	public static void showTip(Context mContext, String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}

	public static String getRandomString(Context mContext) {
		String[] strs = mContext.getResources().getStringArray(R.array.cannot_understand_list);
		Random random = new Random();
		return strs[random.nextInt(strs.length)];
	}
	
}
