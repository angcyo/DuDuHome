package com.dudu.android.launcher.utils;

import android.util.Log;

public class LogUtils {

	public static void e(String tag, String msg){
		if(Constants.DEBUG){
			Log.e(tag, msg);
		}
	}
	
	public static void i(String tag, String msg){
		if(Constants.DEBUG){
			Log.i(tag, msg);
		}
	}
	
	public static void d(String tag, String msg){
		if(Constants.DEBUG){
			Log.d(tag, msg);
		}
	}
	
	public static void w(String tag, String msg){
		if(Constants.DEBUG){
			Log.w(tag, msg);
		}
	}
	
	public static void v(String tag, String msg){
		if(Constants.DEBUG){
			Log.v(tag, msg);
		}
	}
	
}
