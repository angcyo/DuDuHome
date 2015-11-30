package com.dudu.network.event;

import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2015/11/30.
 * Description :4.4.1 发送日志文件
 */
public class LogSend extends MessagePackage {
    private String messageId;
    private  String url;
    private  String method;

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
            url =  jsonObject.getString("url");
            method = jsonObject.getString("method");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toJsonString() {
        return null;
    }

    public String getUrl() {
        return url;
    }
}
