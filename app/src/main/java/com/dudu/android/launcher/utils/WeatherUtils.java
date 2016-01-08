package com.dudu.android.launcher.utils;

import android.text.TextUtils;
import android.util.Log;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.bean.WeatherEntity;
import com.dudu.android.launcher.bean.WeatherSlots;
import com.dudu.event.DeviceEvent;
import com.dudu.monitor.utils.LocationUtils;
import com.dudu.voice.semantic.SemanticConstants;
import com.dudu.voice.semantic.VoiceManager;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/1/8.
 */
public class WeatherUtils {
    private TextUnderstander mTextUnderstander;
    private static WeatherUtils mInstance;

    public static WeatherUtils getInstance() {
        if (mInstance == null) {
            mInstance = new WeatherUtils();
        }
        return mInstance;
    }

    public void startWeatherUnderstanding(String text) {
        int ret;
        mTextUnderstander = TextUnderstander.createTextUnderstander(LauncherApplication.getContext(),
                null);
        if (mTextUnderstander.isUnderstanding()) {
            mTextUnderstander.cancel();
        } else {
            ret = mTextUnderstander.understandText(text, new TextUnderstanderListener() {

                @Override
                public void onResult(UnderstanderResult understanderResult) {
                    String text = understanderResult.getResultString();
                    if (!TextUtils.isEmpty(text)) {
                        handleWeatherSemantic(text);
                    } else {
                        LogUtils.e("Weather", "获取天气失败");
                    }

                    stopWeatherUnderstanding();
                }

                @Override
                public void onError(SpeechError speechError) {
                    stopWeatherUnderstanding();
                }
            });
            if (ret != 0) {
                LogUtils.e("Weather", "开启语义理解失败");
            }
        }
    }

    private void handleWeatherSemantic(String json) {
        String semantic = JsonUtils.parseIatResult(json,
                "semantic");

        WeatherEntity weather = (WeatherEntity) GsonUtil
                .jsonToObject(semantic, WeatherEntity.class);

        WeatherSlots slots = weather.getSlots();

        String dateOrig = slots.getDateTime().getDateOrig() != null ?
                slots.getDateTime().getDateOrig() : "";

        String city = slots.getLocation().getCityAddr();
        String date = slots.getDateTime().getDate();
        if (date.equals("CURRENT_DAY")) {
            date = TimeUtils.format(TimeUtils.format);
        }
        try {
            parseWeatherResult(json, city, date, dateOrig);
        } catch (JSONException e) {

        }


    }

    private String parseWeatherResult(String json, String city, String date, String dateOrig) throws JSONException {
        JSONObject root = new JSONObject(json);
        if (!root.isNull("data")) {
            JSONObject data = root.getJSONObject("data");
            if (!data.isNull("result")) {
                JSONArray result = data.getJSONArray("result");

                String weatherText;
                String range;
                for (int i = 0; i < result.length(); i++) {
                    JSONObject weather = result.getJSONObject(i);

                    if (weather.get("date").equals(date)) {
                        String lowStringRange = weather.getString("tempRange").split("~")[1];
                        String highStringRange = weather.getString("tempRange").split("~")[0];
                        range = lowStringRange + "~" + highStringRange;
                        weatherText = city + dateOrig + "天气 ：" + "\n"
                                + weather.getString("weather") + "\n温度" + range
                                + "\n" + weather.getString("wind");
                        if (dateOrig.equals("今天") && LocationUtils.getInstance(LauncherApplication.getContext()).getCurrentCity().contains(city)) {
                            //四舍五入取整
                            double lowRange = Double.parseDouble(lowStringRange.substring(0, lowStringRange.length() - 1));
                            double highRange = Double.parseDouble(highStringRange.substring(0, highStringRange.length() - 1));
                            String temperature = String.valueOf(new BigDecimal(String.valueOf((lowRange + highRange) / 2)).setScale(0, BigDecimal.ROUND_HALF_UP));
                            EventBus.getDefault().post(new DeviceEvent.Weather(weather.getString("weather"), temperature));
                        }
                        return weatherText;
                    }
                }
            }
        }
        return null;
    }

    private void stopWeatherUnderstanding() {
        if (mTextUnderstander != null) {
            if (mTextUnderstander.isUnderstanding()) {
                mTextUnderstander.cancel();
            }
            mTextUnderstander.destroy();
        }
    }
}
