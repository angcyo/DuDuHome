package com.dudu.android.launcher.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.VoiceManager;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;

public class WeatherUtil {

	private static TextUnderstander mTextUnderstander;

	public static void parseWeather(String str1, String str2, Context context) {
		try {
			String city;
			JSONObject w_semantic = new JSONObject(str1);
			JSONObject w_short = w_semantic.getJSONObject("slots");
			JSONObject w_date = w_short.getJSONObject("datetime");
			String date = w_date.getString("date");
			if (date.equals("CURRENT_DAY")) {
				date = TimeUtils.format(TimeUtils.format);
			}
			String dateOrig = w_date.has("dateOrig") ? w_date
					.getString("dateOrig") : "";
			JSONObject location = w_short.getJSONObject("location");
			if (!location.has("cityAddr")) {
				startTextUnderstand(LocationUtils.getInstance(context).getCurrentCity()+ dateOrig
						+ "天气怎么样", context);
				return;
			}
			city = location.getString("cityAddr");
			JSONObject weatherStr = new JSONObject(str2);
			JSONObject weather = weatherStr.getJSONObject("data");
			JSONArray weatherArr = weather.getJSONArray("result");
			String w_text = "";
			String tempRange;
			for (int i = 0; i < weatherArr.length(); i++) {
				JSONObject w = new JSONObject(weatherArr.get(i).toString());
				if (w.get("date").equals(date)) {
					tempRange = w.getString("tempRange").split("~")[1] + "~"
							+ w.getString("tempRange").split("~")[0];
					w_text = city + dateOrig + "天气 ：" + "\n"
							+ w.getString("weather") + "\n温度" + tempRange
							+ "\n" + w.getString("wind");
					break;
				}
			}
			if (TextUtils.isEmpty(w_text)) {
				JSONObject w2 = new JSONObject(weatherArr.get(1).toString());
				tempRange = w2.getString("tempRange").split("~")[1] + "~"
						+ w2.getString("tempRange").split("~")[0];
				w_text = city + dateOrig + "天气 ：" + "\n"
						+ w2.getString("weather") + "\n温度：" + tempRange + "\n"
						+ w2.getString("wind");
			}
			VoiceManager.getInstance().startSpeaking(w_text, SemanticConstants.TTS_START_UNDERSTANDING);
		} catch (JSONException e) {
			VoiceManager.getInstance().startSpeaking("获取天气信息失败。",
					SemanticConstants.TTS_START_UNDERSTANDING);
		}
	}

	// 以文本的形式获取天气
	private static void startTextUnderstand(String text, Context context) {
		int ret;
		mTextUnderstander = TextUnderstander.createTextUnderstander(context,
				textUnderstanderListener);
		if (mTextUnderstander.isUnderstanding()) {
			mTextUnderstander.cancel();
		} else {
			ret = mTextUnderstander.understandText(text, textListener);
			if (ret != 0) {
				Log.d("Weather", "开启语义理解失败");
			}
		}
	}

	/**
	 * 初始化监听器（文本到语义）。
	 */
	private static InitListener textUnderstanderListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d("Weather", "textUnderstanderListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
			}
		}
	};
	private static TextUnderstanderListener textListener = new TextUnderstanderListener() {

		@Override
		public void onResult(final UnderstanderResult result) {
			try {
				String text = result.getResultString();
				if (!TextUtils.isEmpty(text)) {
					String semantic = JsonUtils
							.parseIatResult(text, "semantic");
					JSONObject w_semantic = new JSONObject(semantic);
					JSONObject w_short = w_semantic.getJSONObject("slots");
					JSONObject w_date = w_short.getJSONObject("datetime");
					String date = w_date.getString("date");
					if (date.equals("CURRENT_DAY")) {
						date = TimeUtils.format(TimeUtils.format);
					}
					String dateOrig = w_date.has("dateOrig") ? w_date
							.getString("dateOrig") : "";
					JSONObject location = w_short.getJSONObject("location");
					String city = location.getString("cityAddr");
					JSONObject weatherStr = new JSONObject(text);
					JSONObject weather = weatherStr.getJSONObject("data");
					JSONArray weatherArr = weather.getJSONArray("result");
					String w_text = "";
					String tempRange;
					for (int i = 0; i < weatherArr.length(); i++) {
						JSONObject w = new JSONObject(weatherArr.get(i)
								.toString());
						if (w.get("date").equals(date)) {
							tempRange = w.getString("tempRange").split("~")[1]
									+ "~"
									+ w.getString("tempRange").split("~")[0];
							w_text = city + dateOrig + "天气 ：" + "\n"
									+ w.getString("weather") + "\n温度"
									+ tempRange + "\n" + w.getString("wind");
							break;
						}
					}
					if (TextUtils.isEmpty(w_text)) {
						JSONObject w2 = new JSONObject(weatherArr.get(1)
								.toString());
						tempRange = w2.getString("tempRange").split("~")[1]
								+ "~" + w2.getString("tempRange").split("~")[0];
						w_text = city + dateOrig + "天气 ：" + "\n"
								+ w2.getString("weather") + "\n温度：" + tempRange
								+ "\n" + w2.getString("wind");
					}

					VoiceManager.getInstance().startSpeaking(w_text,
							SemanticConstants.TTS_START_UNDERSTANDING);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (mTextUnderstander.isUnderstanding())
				mTextUnderstander.cancel();
			mTextUnderstander.destroy();
		}

		@Override
		public void onError(SpeechError error) {

		}
	};
}
