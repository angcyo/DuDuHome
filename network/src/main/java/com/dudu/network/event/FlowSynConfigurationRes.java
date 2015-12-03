package com.dudu.network.event;

import com.dudu.network.utils.StringTools;
import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2015/12/1.
 * Description : 流量策略配置同步
 */
public class FlowSynConfigurationRes extends MessagePackage{
    private String messageId;
    private String method;
    //响应结果
    private String resultCode = "";

    private String uploadLimit;
    private String trafficControl;
    private String downloadLimit;
    private String lifeType;
    private String upLimitMaxValue;
    private String  downLimitMaxValue;
    private String  dailyMaxValue;
    private String  monthMaxValue;
    private String  highArlamValue;
    private String  middleArlamValue;
    private String  lowArlamValue;
    private String  freeArriveValue;
    private String  freeAddValue;
    private String freeAddTimes;
    private String  portalVersion;
    private String portalAddress;

    @Override
    public void setMessageId(String messageId) {

    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public boolean isNeedWaitResponse() {
        return false;
    }

    @Override
    public boolean isNeedEncrypt() {
        return false;
    }

    @Override
    public void createFromJsonString(String messageJsonString) {
        try {
            JSONObject jsonObject = new JSONObject(messageJsonString);
            messageId = jsonObject.getString("messageId");
            resultCode =  jsonObject.getString("resultCode");
            method = jsonObject.getString("method");

            if (!jsonObject.has("result"))
                return;
            JSONObject result = new JSONObject(jsonObject.getString("result"));

            uploadLimit= StringTools.GetStringValue("uploadLimit", result);
            trafficControl= StringTools.GetStringValue("trafficControl", result);
            downloadLimit= StringTools.GetStringValue("downloadLimit", result);
            lifeType= StringTools.GetStringValue("lifeType", result);
            upLimitMaxValue= StringTools.GetStringValue("upLimitMaxValue", result);
            downLimitMaxValue= StringTools.GetStringValue("downLimitMaxValue", result);
            dailyMaxValue= StringTools.GetStringValue("dailyMaxValue", result);
            monthMaxValue= StringTools.GetStringValue("monthMaxValue", result);
            highArlamValue= StringTools.GetStringValue("highArlamValue", result);
            middleArlamValue= StringTools.GetStringValue("middleArlamValue", result);
            lowArlamValue= StringTools.GetStringValue("lowArlamValue", result);
            freeArriveValue= StringTools.GetStringValue("freeArriveValue", result);
            freeAddValue= StringTools.GetStringValue("freeAddValue", result);
            freeAddTimes= StringTools.GetStringValue("freeAddTimes", result);
            portalVersion= StringTools.GetStringValue("portalVersion", result);
            portalAddress= StringTools.GetStringValue("portalAddress", result);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toJsonString() {
        return null;
    }


    public String getUploadLimit() {
        return uploadLimit;
    }

    public String getTrafficControl() {
        return trafficControl;
    }

    public String getDownloadLimit() {
        return downloadLimit;
    }

    public String getLifeType() {
        return lifeType;
    }

    public String getUpLimitMaxValue() {
        return upLimitMaxValue;
    }

    public String getDownLimitMaxValue() {
        return downLimitMaxValue;
    }

    public String getDailyMaxValue() {
        return dailyMaxValue;
    }

    public String getMonthMaxValue() {
        return monthMaxValue;
    }

    public String getHighArlamValue() {
        return highArlamValue;
    }

    public String getMiddleArlamValue() {
        return middleArlamValue;
    }

    public String getLowArlamValue() {
        return lowArlamValue;
    }

    public String getFreeArriveValue() {
        return freeArriveValue;
    }

    public String getFreeAddValue() {
        return freeAddValue;
    }

    public String getFreeAddTimes() {
        return freeAddTimes;
    }

    public String getPortalVersion() {
        return portalVersion;
    }

    public String getPortalAddress() {
        return portalAddress;
    }
}
