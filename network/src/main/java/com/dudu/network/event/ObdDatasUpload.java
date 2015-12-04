package com.dudu.network.event;

import com.dudu.network.utils.Bicker;
import com.dudu.network.utils.BusinessMessageEnum;
import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2015/12/1.
 * Description :
 */
public class ObdDatasUpload extends MessagePackage{
    private String messageId;
    private String obeId;
    private JSONArray obds;
    private String method;


    public ObdDatasUpload() {
    }

    public ObdDatasUpload(String obeId, JSONArray obdDataJsonArray) {
        messageId = Bicker.getBusinessCode(BusinessMessageEnum.OBD_DATA.getCode());
        this.obeId = obeId;
        this.obds = obdDataJsonArray;
        method = MessageMethod.OBDDATAS;
    }

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
            obds =  jsonObject.getJSONArray("obds");
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
            sendJsonObject.put("obds", obds);
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
