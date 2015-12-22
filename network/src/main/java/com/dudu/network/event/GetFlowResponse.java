package com.dudu.network.event;

import com.dudu.network.utils.StringTools;
import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.PriorityQueue;

/**
 * Created by dengjun on 2015/12/1.
 * Description :
 */
public class GetFlowResponse extends MessagePackage{
    //消息ID
    private String messageId = "";
    //响应结果
    private String resultCode = "";
    //业务方法名
    private String method = "";
    //该月剩余总流量
    private float remainingFlow;

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
            resultCode =  jsonObject.getString("resultCode");
            method = jsonObject.getString("method");
//            remainingFlow = Float.valueOf(new JSONObject(jsonObject.getString("result")).getString("remainingFlow"));
            if (resultCode.equals("400") || !jsonObject.has("result"))
                return;

            JSONObject resultJson = new JSONObject(jsonObject.getString("result"));
            String remainFlow = StringTools.GetStringValue("remainingFlow", resultJson);
            if (remainFlow == null){
                remainingFlow = Float.valueOf("1048576");
            }else {
                remainingFlow = Float.valueOf(remainFlow);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toJsonString() {
        JSONObject sendJsonObject =  new JSONObject();
        try {
            sendJsonObject.put("messageId", messageId);
            sendJsonObject.put("resultCode", resultCode);
            sendJsonObject.put("method", method);
            sendJsonObject.put("remainingFlow", String.valueOf(remainingFlow));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sendJsonObject.toString();
    }

    @Override
    public boolean isNeedCache() {
        return false;
    }

    public float getRemainingFlow(){
        return remainingFlow;
    }

    public String getResultCode() {
        return resultCode;
    }
}
