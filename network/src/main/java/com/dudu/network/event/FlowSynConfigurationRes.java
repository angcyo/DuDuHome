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

    private float uploadLimit;
    private int trafficControl;
    private float downloadLimit;
    private  int lifeType;
    private float upLimitMaxValue;
    private float  downLimitMaxValue;
    private float  dailyMaxValue;
    private float  monthMaxValue;
    private int  highArlamValue;
    private int  middleArlamValue;
    private int  lowArlamValue;
    private float  freeArriveValue;
    private float  freeAddValue;
    private int freeAddTimes;
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

            JSONObject result = new JSONObject(jsonObject.getString("result"));

            uploadLimit = Float.valueOf(result.getString("uploadLimit"));
            trafficControl = Integer.valueOf(result.getString("trafficControl"));
            downloadLimit = Float.valueOf(result.getString("downloadLimit"));
            lifeType = Integer.valueOf(result.getString("lifeType"));
            upLimitMaxValue = Float.valueOf(result.getString("upLimitMaxValue"));
            downLimitMaxValue = Float.valueOf(result.getString("downLimitMaxValue"));
            dailyMaxValue = Float.valueOf(result.getString("dailyMaxValue"));
            monthMaxValue = Float.valueOf(result.getString("monthMaxValue"));
            highArlamValue = Integer.valueOf(result.getString("highArlamValue"));
            middleArlamValue = Integer.valueOf(result.getString("middleArlamValue"));
            lowArlamValue = Integer.valueOf(result.getString("lowArlamValue"));
            freeArriveValue = Float.valueOf(result.getString("freeArriveValue"));
            freeAddValue = Float.valueOf(result.getString("freeAddValue"));
            freeAddTimes = Integer.valueOf(result.getString("freeAddTimes"));
//            remainingFlow = Float.valueOf(result.getString("remainingFlow"));
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


    public float getUploadLimit() {
        return uploadLimit;
    }

    public int getTrafficControl() {
        return trafficControl;
    }

    public float getDownloadLimit() {
        return downloadLimit;
    }

    public int getLifeType() {
        return lifeType;
    }

    public float getUpLimitMaxValue() {
        return upLimitMaxValue;
    }

    public float getDownLimitMaxValue() {
        return downLimitMaxValue;
    }

    public float getDailyMaxValue() {
        return dailyMaxValue;
    }

    public float getMonthMaxValue() {
        return monthMaxValue;
    }

    public int getHighArlamValue() {
        return highArlamValue;
    }

    public int getMiddleArlamValue() {
        return middleArlamValue;
    }

    public int getLowArlamValue() {
        return lowArlamValue;
    }

    public float getFreeArriveValue() {
        return freeArriveValue;
    }

    public float getFreeAddValue() {
        return freeAddValue;
    }

    public int getFreeAddTimes() {
        return freeAddTimes;
    }

    /*public float getRemainingFlow() {
        return remainingFlow;
    }*/

    public String getPortalVersion() {
        return portalVersion;
    }

    public String getPotalAddress() {
        return portalAddress;
    }
}
