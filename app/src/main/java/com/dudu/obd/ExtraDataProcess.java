package com.dudu.obd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExtraDataProcess {

	/***
	 * 生成上传的GPS JSON对象
	 * @param gpsData
	 * @param deviceID
	 * @return
	 */
	public JSONObject getUpLoadGpsData(JSONArray gpsData,String deviceID){
		JSONObject json = new JSONObject();
		try {
			json.put("method", "coordinates");
			json.put("obeId",deviceID );
			json.put("lals", gpsData);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 生成上传的OBD数据 JSON对象
	 * @param obdData
	 * @param deviceID
	 * @return
	 */
	public JSONObject getUpLoadOBDData(JSONArray obdData,String deviceID){
		JSONObject json = new JSONObject();
		try {
			json.put("method", "obdDatas");
			json.put("obeId", deviceID);
			json.put("obds", obdData);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 
	 * @param flameOutdata
	 * @param deviceID
	 * @return
	 */
	public JSONObject getUpLoadFlameoutData(JSONArray flameOutdata,String deviceID){
		JSONObject json = new JSONObject();
		try {
			json.put("method", "driveDatas");
			json.put("obeId", deviceID);
			json.put("drives", flameOutdata);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
}
