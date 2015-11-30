package com.dudu.network.event;

import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2015/12/1.
 * Description :3.3.2 检查设备是否激活 响应
 */
public class CheckDeviceActiveRes extends MessagePackage{
    //消息ID
    private String messageId = "";
    //响应结果
    private String resultCode = "";
    //业务方法名
    private String method = "";

    //表明是否激活
    private boolean isActive;

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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (resultCode.equals("200")){
            isActive = true;
        }else if (resultCode.equals("400")){
            isActive = false;
        }
    }

    @Override
    public String toJsonString() {
        return null;
    }

    public boolean isActive() {
        return isActive;
    }
}
