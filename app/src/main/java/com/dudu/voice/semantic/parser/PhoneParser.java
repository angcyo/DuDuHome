package com.dudu.voice.semantic.parser;

import com.dudu.voice.semantic.bean.PhoneBean;
import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.constant.SemanticConstant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/1/6.
 */
public class PhoneParser {

    public static SemanticBean parsePhoneBean(JSONObject semantic, String text) {
        try {
            if (!semantic.isNull(SemanticConstant.REQUEST)) {
                JSONObject request = semantic.getJSONObject(SemanticConstant.REQUEST);

                PhoneBean bean = new PhoneBean();
                bean.setHasResult(true);
                bean.setText(text);
                bean.setService(SemanticConstant.SERVICE_PHONE);

                if (!request.isNull(SemanticConstant.PARAM)) {
                    JSONObject param = request.getJSONObject(SemanticConstant.PARAM);
                    if (!param.isNull(SemanticConstant.CONTACTS_CN)) {
                        bean.setContactName(param.optString(SemanticConstant.CONTACTS_CN));
                    }

                    if (param.isNull(SemanticConstant.PHONE_NUMBER_CN)) {
                        bean.setPhoneNumber(param.optString(SemanticConstant.PHONE_NUMBER_CN));
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
