package com.dudu.network.event;

import com.dudu.network.utils.StringTools;
import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2015/12/1.
 * Description :使用流量上传响应
 */
public class FlowUploadResponse extends MessagePackage{
    //消息ID
    private String messageId = "";
    //响应结果
    private String resultCode = "";
    //业务方法名
    private String method = "";
    //该月剩余总流量
    private float remainingFlow = 1048576;
    /* 流量开关*/
    private  int trafficControl = -1;
    /* 当日流量超限异常*/
    private int exceptionState = 0;
    /*每月流量告警 */
    private int trafficState = -1;
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
            if (resultCode.equals("400") || !jsonObject.has("result"))
                return;

            JSONObject resultJson = new JSONObject(jsonObject.getString("result"));
            String remainFlow = StringTools.GetStringValue("remainingFlow", resultJson);
            if (remainFlow == null){
                remainingFlow = Float.valueOf("1048576");
            }else {
                remainingFlow = Float.valueOf(remainFlow);
            }

            String trafficControl = StringTools.GetStringValue("trafficControl", resultJson);
            if (trafficControl != null){
                this.trafficControl = Integer.valueOf(trafficControl.trim());
            }

            String exceptionState = StringTools.GetStringValue("exceptionState", resultJson);
            if (exceptionState != null){
                this.exceptionState = Integer.valueOf(exceptionState.trim());
            }

            String trafficState = StringTools.GetStringValue("trafficState", resultJson);
            if (trafficState != null){
                this.exceptionState = Integer.valueOf(trafficState.trim());
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

            sendJsonObject.put("result", new JSONObject().put("remainingFlow", String.valueOf(remainingFlow)));
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

    public int getTrafficControl() {
        return trafficControl;
    }

    public int getExceptionState() {
        return exceptionState;
    }

    public int getTrafficState() {
        return trafficState;
    }
}
