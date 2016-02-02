package com.dudu.voice.semantic.parser;

import com.aispeech.common.JSONResultParser;
import com.dudu.voice.semantic.bean.CmdBean;
import com.dudu.voice.semantic.bean.PhoneBean;
import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.bean.VolumeBean;
import com.dudu.voice.semantic.constant.SemanticConstant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/12/28.
 */
public class NativeJsonParser extends SemanticParser {

    private static final float MIN_ATH_THRESHOLD = 0.6f;

    @Override
    public SemanticBean getSemanticBean(String result) {
        mResultParser = new JSONResultParser(result);

        mSemantics = mResultParser.getSem();

        if (mResultParser.getConf() >= MIN_ATH_THRESHOLD) {
            String text = mResultParser.getRec();

            switch (getDomain()) {
                case SemanticConstant.SERVICE_VOLUME:
                    return parseVolumeBean(text);
                case SemanticConstant.SERVICE_CMD:
                    return parseCmdBean(text);
                case SemanticConstant.SERVICE_PHONE:
                    return parsePhoneBean(text);
            }
        }

        return null;
    }

    private String getDomain(){
        try {
            return mSemantics.getString("domain");
        } catch (JSONException e) {
            logger.error("解析json出错： " + e.getMessage());
        }

        return "";
    }

    private VolumeBean parseVolumeBean(String text) {
        VolumeBean bean = new VolumeBean();
        bean.setHasResult(true);
        bean.setText(text);
        bean.setService(mSemantics.optString(SemanticConstant.DOMAIN));
        bean.setOperation(mSemantics.optString(SemanticConstant.ACTION));
        return bean;
    }

    private CmdBean parseCmdBean(String text) {
        CmdBean bean = new CmdBean();
        bean.setHasResult(true);
        bean.setText(text);
        bean.setService(SemanticConstant.SERVICE_CMD);
        bean.setTarget(mSemantics.optString(SemanticConstant.TARGET));
        bean.setAction(mSemantics.optString(SemanticConstant.ACTION));
        return bean;
    }

    private PhoneBean parsePhoneBean(String text) {
        PhoneBean bean = new PhoneBean();
        bean.setHasResult(true);
        bean.setText(text);
        bean.setService(SemanticConstant.SERVICE_PHONE);
        if (!mSemantics.isNull(SemanticConstant.PERSON)) {
            bean.setContactName(mSemantics.optString(SemanticConstant.PERSON));
        }

        if (!mSemantics.isNull(SemanticConstant.NUMBER)) {
            bean.setPhoneNumber(mSemantics.optString(SemanticConstant.NUMBER));
        }

        return bean;
    }

}
