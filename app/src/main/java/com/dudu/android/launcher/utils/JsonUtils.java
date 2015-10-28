package com.dudu.android.launcher.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.dudu.android.launcher.bean.Rsphead;

public class JsonUtils {

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
			e.printStackTrace();
		}
		return null;
	}
	
	public static String parseIatResult(String json, String name) {
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);
			return joResult.optString(name);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
}
