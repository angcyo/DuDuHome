package com.dudu.network.event;

import android.content.Context;

import com.dudu.network.utils.Bicker;
import com.dudu.network.utils.BusinessMessageEnum;
import com.dudu.network.utils.DeviceIDUtil;
import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2015/11/30.
 * Description :
 */
public class LocationInfoUpload extends MessagePackage{
    private String messageId;
    private String obeId;
    private JSONArray lals;
    private String method;

    public LocationInfoUpload() {
    }

    public LocationInfoUpload(String obeId, JSONArray locationInfoJsonArray) {
        messageId = Bicker.getBusinessCode(BusinessMessageEnum.GPS_DATA.getCode());
        this.obeId = obeId;
        lals = locationInfoJsonArray;
        method = MessageMethod.COORDINATES;
    }

    public LocationInfoUpload(Context context, JSONArray locationInfoJsonArray) {
        messageId = Bicker.getBusinessCode(BusinessMessageEnum.GPS_DATA.getCode());
        this.obeId = DeviceIDUtil.getIMEI(context);
        lals = locationInfoJsonArray;
        method = MessageMethod.COORDINATES;
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
            lals =  jsonObject.getJSONArray("lals");
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
            sendJsonObject.put("lals", lals);
            sendJsonObject.put("method", method);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sendJsonObject.toString();
    }

    @Override
    public boolean isNeedCache() {
        return true;
    }
}
