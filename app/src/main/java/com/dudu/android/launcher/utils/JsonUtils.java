package com.dudu.android.launcher.utils;

import com.dudu.android.launcher.bean.Rsphead;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class JsonUtils {

	private static final String TAG = "JsonUtils";

	public static Rsphead getRsphead(String json) {
		Rsphead head = new Rsphead();
		JSONTokener tokener = new JSONTokener(json);
		try {
			JSONObject joResult = new JSONObject(tokener);
			int rc = joResult.optInt("rc");
			head.setRc(joResult.optInt("rc"));
			if(rc == 0){
				head.setService(joResult.optString("service"));
			}
			head.setText(joResult.optString("text"));
			return head;
		} catch (JSONException e) {
			LogUtils.e(TAG, e.getMessage());
		}
		return null;
	}
	
	public static String parseIatResult(String json, String name) {
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);
			return joResult.optString(name);
		} catch (Exception e) {
			LogUtils.e(TAG, e.getMessage());
		} 
		return null;
	}

	public static String parseIatResultChoiseSize(String json) {
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);
			joResult = joResult.optJSONObject("slots");
			if(joResult != null){
				joResult = joResult.optJSONObject("choise");
				if(joResult != null) {
					return joResult.optString("size");
				}
			}
		} catch (Exception e) {
			LogUtils.e(TAG, e.getMessage());
		}

		return null;
	}

	public static String parseIatResultNearby(String json) {
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);
			joResult = joResult.optJSONObject("slots");
			if(joResult != null){
				joResult = joResult.optJSONObject("nearby");
				if(joResult != null){
					return joResult.optString("type");
				}
			}
		} catch (Exception e) {
			LogUtils.e(TAG, e.getMessage());
		}
		return null;
	}

	public static String getNearbyOptionType(String json){

		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);
			joResult = joResult.optJSONObject("slots");
			if(joResult != null){
				joResult = joResult.optJSONObject("option");
				if(joResult != null){
					return joResult.optString("type");
				}
			}
		} catch (Exception e) {
			LogUtils.e(TAG, e.getMessage());
		}
		return null;
	}

}
