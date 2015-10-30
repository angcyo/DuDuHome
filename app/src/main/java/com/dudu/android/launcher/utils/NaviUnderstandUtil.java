package com.dudu.android.launcher.utils;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dudu.map.MapManager;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.VoiceManager;

public class NaviUnderstandUtil {

	private static String TAG = "NaviUnderstand";

	private static Context mContext;

	// 解析导航相关语义
	public static void handleNavi(String semantic,Context context){
		mContext = context;
		JSONObject naviSemantic;
		try {
			naviSemantic = new JSONObject(semantic);
			JSONObject slotsObject = naviSemantic
					.getJSONObject("slots");
			String optionType = slotsObject.getJSONObject(
					"option").getString("type");
			
			Activity activity = ActivitiesManager
					.getInstance().getTopActivity();
				Bundle bundle = new Bundle();
				if(slotsObject.has("action")) {
					String actionType = slotsObject.getJSONObject(
							"action").getString("type");
					switch (actionType) {
					case Constants.OPEN:
						switch (optionType) {
						case Constants.NAVI_PREVIEW:
							openPriview(activity, bundle);
							break;
						case Constants.NAVI_TRAFFIC:
						case Constants.REALTIME_TRAFFIC:
						case Constants.NAVI_TRAFFIC_BROADCAST:
							openTraffic(activity, bundle);
							break;
						case Constants.RERURN_JOURNEY:
							goBack(activity, bundle);
							break;
						default:
							noticeInvalid(mContext);
							break;
					}
					break;
					case Constants.NAVI_LISTEN:
						switch (optionType) {
							case Constants.NAVI_TRAFFIC:
							case Constants.REALTIME_TRAFFIC:
							case Constants.NAVI_TRAFFIC_BROADCAST:
								openTraffic(activity, bundle);
								break;
						}
						break;
					case Constants.CLOSE:
					case Constants.EXIT:
						switch (optionType) {
							case Constants.NAVI_TRAFFIC:
							case Constants.REALTIME_TRAFFIC:
							case Constants.NAVI_TRAFFIC_BROADCAST:
								closeTraffic(activity, bundle);
								break;
							case Constants.NAVI_PREVIEW:
								closePriview(activity, bundle);
								break;
							case Constants.RERURN_JOURNEY:
								
								break;
							default:
								break;
						}
						break;
					case Constants.NAVI_LOOK:
						switch (optionType) {
							case Constants.NAVI_PREVIEW:
								openPriview(activity, bundle);
								break;
							case Constants.NAVI_TRAFFIC:
							case Constants.REALTIME_TRAFFIC:
								openTraffic(activity, bundle);
								break;
							default:
								break;
						}
					    break;
					  default:
						noticeInvalid(mContext);
						break;
					}
				}else{
					switch (optionType) {
						case Constants.NAVI_PREVIEW:
							openPriview(activity, bundle);
							break;
						case Constants.NAVI_TRAFFIC:
						case Constants.REALTIME_TRAFFIC:
						case Constants.NAVI_TRAFFIC_BROADCAST:
							openTraffic(activity, bundle);
							break;
						case Constants.RERURN_JOURNEY:
							goBack(activity, bundle);
							break;
						default:
							noticeInvalid(mContext);
							break;
					}
				}
				
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private static void noticeInvalid(Context context){
		VoiceManager.getInstance().startSpeaking(
				ToastUtils.getRandomString(context),
				SemanticConstants.TTS_START_UNDERSTANDING);
	}
	
	// 打开路况
	private static void openTraffic(Activity activity,Bundle bundle){
//		if(MapManager.getInstance().isNavi()){
//			if (activity != null
//					&& activity instanceof NaviCustomActivity) {
//				((NaviCustomActivity) activity)
//						.trafficInfo();
//			} else {
//				Intent intent = new Intent();
//				intent.setClass(mContext,
//						NaviCustomActivity.class);
//				bundle.putString("type",
//						Constants.NAVI_TRAFFIC);
//				intent.putExtras(bundle);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				mContext.startActivity(intent);
//			}
//		}else{
//			noticeInvalid(mContext);
//		}
	}
	// 关闭路况
	private static void closeTraffic(Activity activity,Bundle bundle) {
//		if(MapManager.getInstance().isNavi()){
//			if (activity != null
//					&& activity instanceof NaviCustomActivity) {
//				((NaviCustomActivity) activity)
//						.closeTraffic();
//			} else {
//				Intent intent = new Intent();
//				intent.setClass(mContext,
//						NaviCustomActivity.class);
//				bundle.putString("type",
//						Constants.CLOSE+Constants.NAVI_TRAFFIC);
//				intent.putExtras(bundle);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				mContext.startActivity(intent);
//			}
//		}else{
//			noticeInvalid(mContext);
//		}
	}
	// 全程预览
	private static void openPriview(Activity activity,Bundle bundle) {
//		if(MapManager.getInstance().isNavi()){
//			if (activity != null
//					&& activity instanceof NaviCustomActivity) {
//				((NaviCustomActivity) activity)
//						.mapPriview();
//			} else {
//				Intent intent = new Intent();
//				intent.setClass(mContext,
//						NaviCustomActivity.class);
//				bundle.putString("type",
//						Constants.NAVI_PREVIEW);
//				intent.putExtras(bundle);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				mContext.startActivity(intent);
//			}
//		}else{
//			noticeInvalid(mContext);
//		}
	}
	private static void closePriview(Activity activity,Bundle bundle){
//		if(MapManager.getInstance().isNavi()){
//			if (activity != null
//					&& activity instanceof NaviCustomActivity) {
//				((NaviCustomActivity) activity)
//						.closePriview();
//			} else {
//				Intent intent = new Intent();
//				intent.setClass(mContext,
//						NaviCustomActivity.class);
//				bundle.putString("type",
//						Constants.CLOSE+Constants.NAVI_PREVIEW);
//				intent.putExtras(bundle);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				mContext.startActivity(intent);
//			}
//		}else{
//			noticeInvalid(mContext);
//		}
	}
	private static void goBack(Activity activity,Bundle bundle){
//		if(MapManager.getInstance().isNavi()){
//			if (activity != null
//					&& activity instanceof NaviCustomActivity) {
//				((NaviCustomActivity) activity)
//						.goBack();
//			} else {
//				Intent intent = new Intent();
//				intent.setClass(mContext,
//						NaviCustomActivity.class);
//				bundle.putString("type",
//						Constants.RERURN_JOURNEY);
//				intent.putExtras(bundle);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				mContext.startActivity(intent);
//			}
//		}else{
//			noticeInvalid(mContext);
//		}
	}
}
