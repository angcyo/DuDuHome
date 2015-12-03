package com.dudu.network.event;

import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONArray;
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
//    private float  remainingFlow;
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

            uploadLimit = result.getString("uploadLimit");
            trafficControl = result.getString("trafficControl");
            downloadLimit = result.getString("downloadLimit");
            lifeType = result.getString("lifeType");
            upLimitMaxValue = result.getString("upLimitMaxValue");
            downLimitMaxValue = result.getString("downLimitMaxValue");
            dailyMaxValue = result.getString("dailyMaxValue");
            monthMaxValue = result.getString("monthMaxValue");
            highArlamValue = result.getString("highArlamValue");
            middleArlamValue = result.getString("middleArlamValue");
            lowArlamValue = result.getString("lowArlamValue");
            freeArriveValue = result.getString("freeArriveValue");
            freeAddValue = result.getString("freeAddValue");
            freeAddTimes = result.getString("freeAddTimes");
            portalVersion = result.getString("portalVersion");
            portalAddress = result.getString("portalAddress");

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
