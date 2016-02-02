package com.dudu.voice.semantic.parser;

import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.bean.WeatherBean;
import com.dudu.voice.semantic.constant.SemanticConstant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 赵圣琪 on 2016/1/7.
 */
public class WeatherParser {

    public static SemanticBean parseWeatherBean(JSONObject semantic, String text) {
        try {
            if (!semantic.isNull(SemanticConstant.REQUEST)) {
                JSONObject request = semantic.getJSONObject(SemanticConstant.REQUEST);

                WeatherBean bean = new WeatherBean();
                bean.setHasResult(true);
                bean.setText(text);
                bean.setService(SemanticConstant.SERVICE_WEATHER);

                if (!request.isNull(SemanticConstant.PARAM)) {
                    JSONObject param = request.getJSONObject(SemanticConstant.PARAM);
                    if (!param.isNull(SemanticConstant.DATE_CN)) {
                        bean.setDate(param.optString(SemanticConstant.DATE_CN));
                    }

                    if (!param.isNull(SemanticConstant.PROVINCE_CN)) {
                        bean.setProvince(param.optString(SemanticConstant.PROVINCE_CN));
                    }

                    if (!param.isNull(SemanticConstant.CITY_CN)) {
                        bean.setCity(param.optString(SemanticConstant.CITY_CN));
                    }

                    if (!param.isNull(SemanticConstant.AREA_CN)) {
                        bean.setArea(param.optString(SemanticConstant.AREA_CN));
                    }
                }

                return bean;
            }
        } catch (JSONException e) {
            // 忽略
        }

        return SemanticBean.getDefaultBean(text);
    }
}
