package com.dudu.network.event;

import android.content.Context;

import com.dudu.network.utils.Bicker;
import com.dudu.network.utils.BusinessMessageEnum;
import com.dudu.network.utils.DeviceIDUtil;
import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2015/12/5.
 * Description : 3.5.2 统计Pottal弹出次数
 */
public class Portal extends MessagePackage{
    private String messageId;
    private String obeId;
    private String method;

    private String  portalCount;//Portal记录数

    public Portal() {
    }

    public Portal(Context context, String portalCount) {
        this.portalCount = portalCount;
        messageId = Bicker.getBusinessCode(BusinessMessageEnum.PORTAL_DATA.getCode());
        obeId = DeviceIDUtil.getIMEI(context);
        method = MessageMethod.PORTAL;
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
        try {
            JSONObject jsonObject = new JSONObject(messageJsonString);
            messageId = jsonObject.getString("messageId");
            obeId =  jsonObject.getString("obeId");
            method = jsonObject.getString("method");
            portalCount =  jsonObject.getString("portalCount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toJsonString() {
        JSONObject sendJsonObject =  new JSONObject();
        try {
            sendJsonObject.put("messageId", messageId);
            sendJsonObject.put("obeId", obeId);
            sendJsonObject.put("method", method);
            sendJsonObject.put("portalCount", portalCount);
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
