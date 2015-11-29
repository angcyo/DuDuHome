package com.dudu.network.valueobject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2015/11/29.
 * Description :
 */
public class GeneralResponse extends MessagePackage{
    //消息ID
    private String messageId;
    //响应结果
    private String resultCode;
    //业务方法名
    private String method;


    @Override
    public void setMessageId(String messageId) {

    }

    @Override
    public String getMessageId() {
        return null;
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
        return null;
    }



}
