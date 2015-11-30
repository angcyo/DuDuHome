package com.dudu.network.event;

import com.dudu.network.utils.Bicker;
import com.dudu.network.utils.BusinessMessageEnum;
import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2015/12/1.
 * Description : 采集驾驶习惯数据，熄火的时候上传。
 */
public class DriveDatasUpload extends MessagePackage{
    private String messageId;
    private String obeId;
    private JSONObject flamountData;
    private String method;

    public DriveDatasUpload(String obeId, JSONObject flamountDataJsonObject) {
        this.obeId = obeId;
        messageId = Bicker.getBusinessCode(BusinessMessageEnum.OBD_DATA.getCode());
        flamountData = flamountDataJsonObject;
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

    }

    @Override
    public String toJsonString() {
        JSONObject sendJsonObject =  new JSONObject();
        try {
            sendJsonObject.put("messageId", messageId);
            sendJsonObject.put("obeId", obeId);
            sendJsonObject.put("method", method);

            sendJsonObject.put("maxrpm", flamountData.getInt("maxrpm"));
            sendJsonObject.put("minrpm", flamountData.getInt("minrpm"));
            sendJsonObject.put("maxspd", flamountData.getInt("maxspd"));
            sendJsonObject.put("avgspd", flamountData.getInt("avgspd"));
            sendJsonObject.put("maxacl", flamountData.getInt("maxacl"));

            sendJsonObject.put("mileT", flamountData.getDouble("mileT"));//协议中是浮点，后面再敲定
            sendJsonObject.put("fuelT", flamountData.getDouble("fuelT"));
            sendJsonObject.put("miles", flamountData.getDouble("miles"));
            sendJsonObject.put("fuels", flamountData.getDouble("fuels"));

            sendJsonObject.put("times", flamountData.getInt("times"));
            sendJsonObject.put("starts", flamountData.getInt("starts"));
            sendJsonObject.put("power", flamountData.getInt("power"));

            sendJsonObject.put("createTime", flamountData.getString("createTime"));

            sendJsonObject.put("hotCarTime", flamountData.getInt("hotCarTime"));
            sendJsonObject.put("idleTime", flamountData.getInt("idleTime"));

            sendJsonObject.put("idleFuelConsumption", flamountData.getDouble("idleFuelConsumption"));//协议中是浮点，后面再敲定

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sendJsonObject.toString();
    }
}
