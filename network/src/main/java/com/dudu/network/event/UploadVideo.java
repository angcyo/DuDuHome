package com.dudu.network.event;

import android.content.Context;

import com.dudu.network.utils.Bicker;
import com.dudu.network.utils.BusinessMessageEnum;
import com.dudu.network.utils.DeviceIDUtil;
import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dengjun on 2015/12/16.
 * Description :  视频上传事件
 */
public class UploadVideo extends MessagePackage {
    private String messageId;
    private  String method;
    private String obeId;
    private String isStopUploadVideo = "true";
    private String hostController = "http://192.168.0.50:8080/";

    public UploadVideo() {
    }

    public UploadVideo(Context context) {
        messageId = Bicker.getBusinessCode(BusinessMessageEnum.UPLOAD_VIDEO.getCode());
        method = MessageMethod.UPLOADVIDEO;
        obeId = DeviceIDUtil.getIMEI(context);
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
            obeId =  jsonObject.getString("obeId");
            method = jsonObject.getString("method");
            isStopUploadVideo = jsonObject.getString("isStopUploadVideo");
            hostController = jsonObject.getString("hostController");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toJsonString() {
        return null;
    }

    @Override
    public boolean isNeedCache() {
        return false;
    }

    public String getObeId() {
        return obeId;
    }

    public String getIsStopUploadVideo() {
        return isStopUploadVideo;
    }

    public String getHostController() {
        return hostController;
    }
}
