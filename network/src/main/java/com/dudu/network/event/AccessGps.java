package com.dudu.network.event;

import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2015/11/30.
 * Description : 4.1.1 GPS导航 微信接口
 */
public class AccessGps extends MessagePackage{
    private String messageId;
    private String method;

    private String obeId;
    private String openid;
    private double Lon;
    private double Lat;

    @Override
    public void setMessageId(String messageId) {

    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getMethod() {
        return method;
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
            method = jsonObject.getString("method");

            obeId = jsonObject.getString("obeId");
            openid = jsonObject.getString("openid");
            Lon = Double.valueOf(jsonObject.getString("Lon"));
            Lat = Double.valueOf(jsonObject.getString("Lat"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toJsonString() {
        return null;
    }

    @Override
    public boolean isNeedCache() {
        return false;
    }
}
