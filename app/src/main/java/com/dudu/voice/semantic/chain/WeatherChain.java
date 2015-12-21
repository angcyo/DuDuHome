package com.dudu.voice.semantic.chain;

import android.text.TextUtils;
import android.util.Log;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.bean.WeatherEntity;
import com.dudu.android.launcher.bean.WeatherSlots;
import com.dudu.android.launcher.utils.CmdType;
import com.dudu.android.launcher.utils.GsonUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.TimeUtils;
import com.dudu.event.DeviceEvent;
import com.dudu.monitor.utils.LocationUtils;
import com.dudu.voice.semantic.SemanticConstants;
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
 * Created by 赵圣琪 on 2015/10/30.
 */
public class WeatherChain extends SemanticChain {

    private static final String TAG = "WeatherChain";

    private TextUnderstander mTextUnderstander;

    @Override
    public boolean matchSemantic(String service) {
        return CmdType.SERVICE_WEATHER.equals(service);
    }

    @Override
    public boolean doSemantic(String json) {
        handleWeatherSemantic(json);
        return true;
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

        if (TextUtils.isEmpty(city)) {
            startWeatherUnderstanding(LocationUtils.getInstance(
                    LauncherApplication.getContext()).getCurrentCity() + dateOrig
                    + "天气怎么样?");
            return;
        }

        String date = slots.getDateTime().getDate();
        if (date.equals("CURRENT_DAY")) {
            date = TimeUtils.format(TimeUtils.format);
        }

        String result = null;
        try {
            result = parseWeatherResult(json, city, date, dateOrig);
        } catch (JSONException e) {
            LogUtils.e(TAG, e.getMessage());
        }

        if (!TextUtils.isEmpty(result)) {
            mVoiceManager.startSpeaking(result, SemanticConstants.TTS_START_UNDERSTANDING);
        } else {
            mVoiceManager.startSpeaking("抱歉，未能获取天气信息。", SemanticConstants.TTS_START_UNDERSTANDING);
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
                        //四舍五入取整
                        double lowRange = Double.parseDouble(lowStringRange.substring(0, lowStringRange.length() - 1));
                        double highRange = Double.parseDouble(highStringRange.substring(0, highStringRange.length() - 1));
                        String temperature = String.valueOf(new BigDecimal(String.valueOf((lowRange + highRange) / 2)).setScale(0, BigDecimal.ROUND_HALF_UP));
                        EventBus.getDefault().post(new DeviceEvent.Weather(weather.getString("weather"), temperature));
                        return weatherText;
                    }
                }
            }
        }

        return null;
    }

    // 以文本的形式获取天气
    private void startWeatherUnderstanding(String text) {
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
                        mVoiceManager.startSpeaking("抱歉，未能获取天气信息。", SemanticConstants.TTS_START_UNDERSTANDING);
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

    private void stopWeatherUnderstanding() {
        if (mTextUnderstander != null) {
            if (mTextUnderstander.isUnderstanding()) {
                mTextUnderstander.cancel();
            }
            mTextUnderstander.destroy();
        }
    }

}
