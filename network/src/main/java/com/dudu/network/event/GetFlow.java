package com.dudu.network.event;

import android.content.Context;

import com.dudu.network.utils.Bicker;
import com.dudu.network.utils.BusinessMessageEnum;
import com.dudu.network.utils.DeviceIDUtil;
import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2015/12/1.
 * Description : 3.4.1 流量查询
 */
public class GetFlow extends MessagePackage {
    private String messageId;
    private String obeId;
    private String method;

    public GetFlow(Context context) {
        this.obeId = DeviceIDUtil.getIMEI(context);
        messageId = Bicker.getBusinessCode(BusinessMessageEnum.GET_FLOW_DATA.getCode());
        method = MessageMethod.GETFLOW;
    }

    @Override
    public void setMessageId(String messageId) {

    }

    @Override
    public String getMessageId() {
        return messageId;
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sendJsonObject.toString();
    }
}
