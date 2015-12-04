package com.dudu.network.event;

import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2015/12/1.
 * Description :3.5.1 更新Portal 响应
 */
public class PortalUpdateRes extends MessagePackage {
    private String messageId;
    private String method;

    private String group_name;
    private String remote_filename;

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

            group_name = jsonObject.getString("potalAddress").split(",")[0];
            remote_filename =  jsonObject.getString("potalAddress").split(",")[1];
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

    public String getGroup_name() {
        return group_name;
    }

    public String getRemote_filename() {
        return remote_filename;
    }
}
