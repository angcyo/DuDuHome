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

}
