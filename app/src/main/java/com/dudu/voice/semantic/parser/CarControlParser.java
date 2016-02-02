package com.dudu.voice.semantic.parser;

import com.dudu.android.launcher.utils.Constants;
import com.dudu.voice.semantic.bean.CmdBean;
import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.bean.VolumeBean;
import com.dudu.voice.semantic.constant.SemanticConstant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 赵圣琪 on 2015/12/28.
 */
public class CarControlParser {

    public static SemanticBean parseCarControlBean(JSONObject semantic, String text) {
        try {
            if (!semantic.isNull(SemanticConstant.REQUEST)) {
                JSONObject request = semantic.getJSONObject(SemanticConstant.REQUEST);
                if (!request.isNull(SemanticConstant.PARAM)) {
                    JSONObject param = request.getJSONObject(SemanticConstant.PARAM);
                    if (!param.isNull(SemanticConstant.VOLUME_CN)) {
                        VolumeBean bean = new VolumeBean();
                        bean.setService(SemanticConstant.SERVICE_VOLUME);
                        bean.setHasResult(true);
                        bean.setText(text);
                        bean.setOperation(param.optString(SemanticConstant.VOLUME_CN));
                        return bean;
                    }

                    CmdBean bean = new CmdBean();
                    bean.setService(SemanticConstant.SERVICE_CMD);
                    bean.setHasResult(true);
                    bean.setText(text);


                    if (text.contains(SemanticConstant.RECORD_CN)) {
                        bean.setAction(param.optString(SemanticConstant.ACTION_CN));
                        bean.setTarget(SemanticConstant.RECORD_CN);
                        return bean;
                    }

                    if (text.contains(Constants.SPEECH)) {
                        bean.setTarget(Constants.SPEECH);
                        return bean;
                    }

                    if (text.contains(Constants.NAVIGATION) || text.contains(SemanticConstant.DOMAIN_MAP)) {
                        bean.setAction(param.optString(SemanticConstant.ACTION_CN));
                        bean.setTarget(param.optString(SemanticConstant.OBJECT_CN));
                        return bean;
                    }

                    if (text.contains(Constants.BACK)) {
                        bean.setTarget(Constants.BACK);
                        return bean;
                    }

                    if (text.contains(Constants.EXIT)) {
                        bean.setTarget(Constants.EXIT);
                        return bean;
                    }



                    bean.setAction(param.optString(SemanticConstant.ACTION));
                    bean.setTarget(param.optString(SemanticConstant.TARGET));
                    return bean;
                }
            }
        } catch (JSONException e) {
            // 忽略
        }

        return SemanticBean.getDefaultBean(text);
    }


}
