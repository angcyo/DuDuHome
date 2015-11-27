package com.dudu.navi.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonUtil {

	/**
	 * map转换为json String数据
	 * 
	 * @param body
	 * @param reqhead
	 */
	public static String objectToJson(Object obj) {
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		return json;
	}

	/**
	 * json数据转换为具体的object
	 * 
	 * @param jsonString
	 * @param obj
	 * @return
	 */
	public static Object jsonToObject(String jsonString,
			Class<? extends Object> clas) {
		Gson gson = new Gson();
		return gson.fromJson(jsonString, clas);
	}

	/**
	 * json数据转换为具体的List<Object>
	 * 
	 * @param jsonString
	 * @param obj
	 * @return
	 */
	public static List<Object> jsonToLists(String jsonString) {
		// json转为带泛型的list
		Gson gson = new Gson();
		List<Object> retList = gson.fromJson(jsonString,
				new TypeToken<List<Object>>() {
				}.getType());
		return retList;
	}

	public static <T> T getObject(String jsonString, Class<T> cls) {
		T t = null;
		try {
			Gson gson = new Gson();
			t = gson.fromJson(jsonString, cls);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	public static <T> List<T> getListObject(String jsonString, Class<T> cls) {
		List<T> list = new ArrayList<T>();
		try {
			Gson gson = new Gson();
			list = gson.fromJson(jsonString, new TypeToken<List<T>>() {
			}.getType());
		} catch (Exception e) {
		}
		return list;
	}

	public static List<Map<String, Object>> listKeyMaps(String jsonString) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			Gson gson = new Gson();
			list = gson.fromJson(jsonString,
					new TypeToken<List<Map<String, Object>>>() {
					}.getType());
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;
	}
}
