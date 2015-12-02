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
 * Description : 3.4.2 使用流量上报
 */
public class FlowUpload extends MessagePackage {

    private String messageId;
    private String obeId;
    private String method;

    private float usedFlow;
    private String createTime;

    public FlowUpload(Context context, float usedFlow, String createTime) {
        this.obeId = DeviceIDUtil.getIMEI(context);
        this.usedFlow = usedFlow;
        this.createTime = createTime;
        messageId = Bicker.getBusinessCode(BusinessMessageEnum.FLOW_DATA.getCode());
        method = MessageMethod.FLOW;
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
            usedFlow = Float.valueOf(jsonObject.getString("usedFlow"));
            createTime = jsonObject.getString("createTime");
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
            sendJsonObject.put("usedFlow", String.valueOf(usedFlow));
            sendJsonObject.put("createTime", createTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sendJsonObject.toString();
    }
}
