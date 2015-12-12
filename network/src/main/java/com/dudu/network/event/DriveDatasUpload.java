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


    public DriveDatasUpload() {
    }

    public DriveDatasUpload(String obeId, JSONObject flamountDataJsonObject) {
        this.obeId = obeId;
        messageId = Bicker.getBusinessCode(BusinessMessageEnum.OBD_DATA.getCode());
        flamountData = flamountDataJsonObject;
        method = MessageMethod.DRIVEDATAS;
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
            method = jsonObject.getString("method");

            flamountData = new JSONObject();
            flamountData.put("maxrpm", jsonObject.getString("maxrpm"));
            flamountData.put("minrpm", jsonObject.getString("minrpm"));
            flamountData.put("maxspd", jsonObject.getString("maxspd"));
            flamountData.put("avgspd", jsonObject.getString("avgspd"));
            flamountData.put("maxacl", jsonObject.getString("maxacl"));

            flamountData.put("mileT", jsonObject.getString("mileT"));//协议中是浮点，后面再敲定
            flamountData.put("fuelT", jsonObject.getString("fuelT"));
            flamountData.put("miles", jsonObject.getString("miles"));
            flamountData.put("fuels", jsonObject.getString("fuels"));

            flamountData.put("times", jsonObject.getString("times"));
            flamountData.put("starts", jsonObject.getString("starts"));
            flamountData.put("power", jsonObject.getString("power"));

            flamountData.put("createTime", jsonObject.getString("createTime"));

            flamountData.put("hotCarTime", jsonObject.getString("hotCarTime"));
            flamountData.put("idleTime", jsonObject.getString("idleTime"));

            flamountData.put("idleFuelConsumption", jsonObject.getString("idleFuelConsumption"));//协议中是浮点，后面再敲定


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

            sendJsonObject.put("maxrpm", flamountData.getString("maxrpm"));
            sendJsonObject.put("minrpm", flamountData.getString("minrpm"));
            sendJsonObject.put("maxspd", flamountData.getString("maxspd"));
            sendJsonObject.put("avgspd", flamountData.getString("avgspd"));
            sendJsonObject.put("maxacl", flamountData.getString("maxacl"));

            sendJsonObject.put("mileT", flamountData.getString("mileT"));//协议中是浮点，后面再敲定
            sendJsonObject.put("fuelT", flamountData.getString("fuelT"));
            sendJsonObject.put("miles", flamountData.getString("miles"));
            sendJsonObject.put("fuels", flamountData.getString("fuels"));

            sendJsonObject.put("times", flamountData.getString("times"));
            sendJsonObject.put("starts", flamountData.getString("starts"));
            sendJsonObject.put("power", flamountData.getString("power"));

            sendJsonObject.put("createTime", flamountData.getString("createTime"));

            sendJsonObject.put("hotCarTime", flamountData.getString("hotCarTime"));
            sendJsonObject.put("idleTime", flamountData.getString("idleTime"));

            sendJsonObject.put("idleFuelConsumption", flamountData.getString("idleFuelConsumption"));//协议中是浮点，后面再敲定

            //暂时先不处理
//            sendJsonObject.put("hotCarTime", "0");
//            sendJsonObject.put("idleTime", "0");
//
//            sendJsonObject.put("idleFuelConsumption", "0");//协议中是浮点，后面再敲定

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
