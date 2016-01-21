package com.dudu.network.event;

import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2016/1/21.
 * Description : 登录响应消息
 */
public class LoginRes extends MessagePackage {
    //消息ID
    private String messageId = "";
    //响应结果
    private String resultCode = "";
    //业务方法名
    private String method = "";


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
            resultCode =  jsonObject.getString("resultCode");
            method = jsonObject.getString("method");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toJsonString() {
        return "";
    }

    @Override
    public boolean isNeedCache() {
        return false;
    }

    public String getResultCode() {
        return resultCode;
    }
}
