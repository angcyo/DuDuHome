package com.dudu.voice.semantic.parser;

import com.aispeech.common.JSONResultParser;
import com.dudu.voice.VoiceManager;
import com.dudu.voice.semantic.bean.CmdBean;
import com.dudu.voice.semantic.bean.DefaultBean;
import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.constant.SemanticConstant;
import com.dudu.voice.speech.SpeechManagerImpl;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 赵圣琪 on 2015/12/28.
 */
public class CloudJsonParser extends SemanticParser {

    @Override
    public SemanticBean getSemanticBean(String result) {

        mResultParser = new JSONResultParser(result);

        final String text = mResultParser.getInput();

        if (isSelfChecking(text)) {
            return getSelfCheckingBean(text);
        }

        mSemantics = mResultParser.getSemantics();

        if (mSemantics != null) {
            switch (getDomain()) {
                case SemanticConstant.DOMAIN_MAP:
                    return MapParser.parseMapBean(result);

                case SemanticConstant.DOMAIN_CAR_CONTROL:
                    return CarControlParser.parseCarControlBean(mSemantics, text);

                case SemanticConstant.DOMAIN_PHONE:
                    return PhoneParser.parsePhoneBean(mSemantics, text);

                case SemanticConstant.DOMAIN_WEATHER:
                    return WeatherParser.parseWeatherBean(mSemantics, text);
            }
        }

        return SemanticBean.getDefaultBean(text);
    }

    private String getDomain() {
        try {
            return mSemantics.getJSONObject("request").getString("domain");
        } catch (JSONException e) {
            logger.error("解析json出错： " + e.getMessage());
        }

        return "";
    }

    private static boolean isSelfChecking(String text) {
        if (text.length() == 6 && (text.contains("车辆字井") ||
                text.contains("车辆自检") || text.contains("车辆自己"))) {
            return true;
        }

        return false;
    }

    private static CmdBean getSelfCheckingBean(String text) {
        String action = text.substring(0, 2);
        CmdBean bean = new CmdBean();
        bean.setHasResult(true);
        bean.setText(action + "车辆自检");
        bean.setService(SemanticConstant.SERVICE_CMD);
        bean.setAction(action);
        bean.setTarget(SemanticConstant.SELF_CHECKING_CN);
        return bean;
    }

}
