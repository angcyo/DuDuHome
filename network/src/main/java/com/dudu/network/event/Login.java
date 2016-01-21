package com.dudu.network.event;

import com.dudu.commonlib.CommonLib;
import com.dudu.network.utils.Bicker;
import com.dudu.network.utils.BusinessMessageEnum;
import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2016/1/20.
 * Description : 登录消息
 */
public class Login extends MessagePackage{
    private String messageId;
    private String method;
    private String obeId;

    public Login() {
        messageId = Bicker.getBusinessCode(BusinessMessageEnum.LOGIN_DATA.getCode());
        method = MessageMethod.LOGIN;
        obeId = CommonLib.getInstance().getObeId();
    }

    @Override
    public void setMessageId(String messageId) {
        this.messageId = messageId;
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
        return true;
    }

    @Override
    public boolean isNeedEncrypt() {
        return true;
    }

    @Override
    public void createFromJsonString(String messageJsonString) {

    }

    @Override
    public String toJsonString() {
        JSONObject sendJsonObject =  new JSONObject();
        try {
            sendJsonObject.put("messageId", messageId);
            sendJsonObject.put("obeId", obeId);
            sendJsonObject.put("method", method);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sendJsonObject.toString();
    }

    @Override
    public boolean isNeedCache() {
        return false;
    }
}
