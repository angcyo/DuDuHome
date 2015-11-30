package com.dudu.network.event;

import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2015/11/30.
 * Description :4.2.1 流量开关
 */
public class SwitchFlow extends MessagePackage{
    private String messageId;
    private  int trafficControl;
    private  String method;

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
            trafficControl =  jsonObject.getInt("trafficControl");
            method = jsonObject.getString("method");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toJsonString() {
        return null;
    }

    public int getTrafficControl() {
        return trafficControl;
    }
}
