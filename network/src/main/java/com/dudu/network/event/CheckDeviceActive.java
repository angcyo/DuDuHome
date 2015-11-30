package com.dudu.network.event;

import android.content.Context;

import com.dudu.android.hideapi.SystemPropertiesProxy;
import com.dudu.network.utils.Bicker;
import com.dudu.network.utils.BusinessMessageEnum;
import com.dudu.network.utils.DeviceIDUtil;
import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dengjun on 2015/12/1.
 * Description :3.3.2 检查设备是否激活
 */
public class CheckDeviceActive extends MessagePackage {
    private String messageId;
    private String method;

    private Map<String,String> activemap;
    private String versionCode;

    public CheckDeviceActive() {
    }

    public CheckDeviceActive(Context context) {
        messageId = Bicker.getBusinessCode(BusinessMessageEnum.ACTIVATION_STATUS_DATA.getCode());
        method = MessageMethod.ACTIVATIONSTATUS;

        SystemPropertiesProxy sps = SystemPropertiesProxy.getInstance();
        activemap = new HashMap<>();
        activemap.put("messageId", messageId);
        activemap.put("method",method);

        activemap.put("ro.build.fingerprint", sps.get("ro.build.fingerprint", "UNKNOWN"));
        activemap.put("ro.serialno", DeviceIDUtil.getAndroidID(context));
        activemap.put("ro.product.model", sps.get("ro.product.model", "UNKNOWN"));
        activemap.put("sim.seralno", DeviceIDUtil.getSimSerialNumber(context));
        activemap.put("launcher.version", DeviceIDUtil.getVersionName(context));
//        activemap.put(OBEID,obeId);
        activemap.put("obeId", DeviceIDUtil.getIMEI(context));
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
        JSONObject jsonObject = new JSONObject(activemap);
        return jsonObject.toString();
    }
}
