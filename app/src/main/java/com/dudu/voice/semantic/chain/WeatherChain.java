package com.dudu.voice.semantic.chain;

import android.text.TextUtils;

import com.dudu.android.launcher.utils.CmdType;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.WeatherUtils;
import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.bean.WeatherBean;
import com.dudu.weather.CapitalUtil;
import com.dudu.weather.WeatherManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * Created by lxh on 2016/2/17.
 */
public class WeatherChain extends SemanticChain implements WeatherManager.OnWeatherDataListener {

    private WeatherManager mWeatherManager;

    private String mProvince;

    private String mCity;

    private String mDate;

    private String mCityDealed;

    private String mTimeDealed;

    private Logger logger;

    public WeatherChain() {
        logger = LoggerFactory.getLogger("voice.weather");

        mWeatherManager = new WeatherManager();

        mWeatherManager.setOnWeatherDataListener(this);
    }

    @Override
    public boolean matchSemantic(String service) {
        return CmdType.SERVICE_WEATHER.equals(service);
    }

    @Override
    public boolean doSemantic(SemanticBean semantic) {
        WeatherBean bean = (WeatherBean) semantic;

        initTimeAndCity(bean);

        try {
            mWeatherManager.requestChinaGlobalWeather(mCityDealed, mTimeDealed);
        } catch (UnsupportedEncodingException e) {
            onWeatherFailure();
        }

        return true;
    }

    @Override
    public void onWeatherSuccess() {
        logger.debug(mWeatherManager.getSpeakWord());
        StringBuilder wordBuilder = new StringBuilder();

        String[] words = mWeatherManager.getSpeakWord().split(",");
        for (String word : words) {
            wordBuilder.append(word)
                    .append("\n");
        }

        String playText = wordBuilder.toString();
        mVoiceManager.startSpeaking(playText.substring(0, playText.length() - 1));
    }

    @Override
    public void onWeatherFailure() {
        mVoiceManager.startSpeaking(Constants.ERROR_GET_WEATHER);
    }

    private void initTimeAndCity(WeatherBean weather) {
        mProvince = weather.getProvince();
        mCity = weather.getCity();
        mDate = weather.getDate();

        if (!TextUtils.isEmpty(mCity)) {//城市不为空
            logger.debug("城市不为空");
            mCityDealed = WeatherUtils.getQueryCity(weather.getCity());
        } else {//城市为空
            logger.debug("城市为空");
            if (!TextUtils.isEmpty(mProvince)) {//省份不为空
                logger.debug("省份不为空");
                mCityDealed = CapitalUtil.getCapital(WeatherUtils.getQueryProvince(mProvince));
            } else {//省份为空
                logger.debug("省份为空");
                mCityDealed = WeatherUtils.getQueryCity(weather.getCity());
            }
        }

        mTimeDealed = WeatherUtils.getQueryTime(mDate);
        logger.debug("查询时间：" + mTimeDealed + " 查询城市：" + mCityDealed);
    }

}
