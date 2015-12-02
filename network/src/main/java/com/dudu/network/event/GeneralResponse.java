package com.dudu.network.event;

import android.widget.Switch;

import com.dudu.network.utils.Bicker;
import com.dudu.network.utils.BusinessMessageEnum;
import com.dudu.network.utils.DuduLog;
import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2015/11/29.
 * Description :
 */
public class GeneralResponse extends MessagePackage {
    //消息ID
    private String messageId = "";
    //响应结果
    private String resultCode = "";
    //业务方法名
    private String method = "";

    public GeneralResponse() {
    }

    //响应服务器发来的数据，响应中无需加method字段，调用此构造函数
    public GeneralResponse(String resultCode, String method, String messageId) {
        this.resultCode = resultCode;
        this.method = method;
        this.messageId = messageId;
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sendJsonObject.toString();
    }



    private void proMethod(String method){
        switch(method){
            case MessageMethod.ACCESS:
                messageId = Bicker.getBusinessCode(BusinessMessageEnum.ACCESS_SWITCH.getCode());
                break;
            case MessageMethod.SWITCHFLOW:
                messageId = Bicker.getBusinessCode(BusinessMessageEnum.FLOW_SWITCH.getCode());
                break;
            case MessageMethod.DATAOVERSTEPALARM:
                messageId = Bicker.getBusinessCode(BusinessMessageEnum.FLOW_ARLAM.getCode());
                break;
            case MessageMethod.DATAEXCEPTIONALARM:
                messageId = Bicker.getBusinessCode(BusinessMessageEnum.FLOW_EXCEPTION.getCode());
                break;
            case MessageMethod.UPDATEPORTAL:
                messageId = Bicker.getBusinessCode(BusinessMessageEnum.UPDATE_PORTAL.getCode());
                break;
            case MessageMethod.LOGS:
                messageId = Bicker.getBusinessCode(BusinessMessageEnum.UPDATE_LOGS.getCode());
                break;
            default:
                break;
        }
    }


    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }
}
