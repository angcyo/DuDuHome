package com.dudu.network.event;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.dudu.android.hideapi.SystemPropertiesProxy;
import com.dudu.network.utils.Bicker;
import com.dudu.network.utils.BusinessMessageEnum;
import com.dudu.network.utils.DeviceIDUtil;
import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dengjun on 2015/11/30.
 * Description :设备激活消息类
 */
public class ActiveDevice extends MessagePackage{
    private String messageId;
    private String method;

    Map<String,String> activemap;

    String versionCode;

    private Context mContext;

    private static WifiManager mWifiManager = null;

    public ActiveDevice() {
    }

    public ActiveDevice(Context context) {
        mContext = context;
        messageId = Bicker.getBusinessCode(BusinessMessageEnum.DEVCIE_LOGIN_DATA.getCode());

        versionCode = DeviceIDUtil.getVersionName(context);

        SystemPropertiesProxy sps = SystemPropertiesProxy.getInstance();

        activemap = new HashMap<>();
        activemap.put("messageId", messageId);

        activemap.put("ro.board.platform",sps.get("ro.board.platform","UNKNOWN"));
        activemap.put("ro.build.fingerprint",sps.get("ro.build.fingerprint","UNKNOWN"));
        activemap.put("ro.product.manufacturer",sps.get("ro.product.manufacturer","UNKNOWN"));
        activemap.put("ro.product.model",sps.get("ro.product.model", "UNKNOWN"));
        activemap.put("ro.serialno", DeviceIDUtil.getAndroidID(context));
        activemap.put("sim.seralno",DeviceIDUtil.getSimSerialNumber(context));


        activemap.put("launcher.version", versionCode);
        activemap.put("method", MessageMethod.DEVICELOGIN);
        activemap.put("obeId", DeviceIDUtil.getIMEI(context));

        activemap.put("wifiStatus",getWifiApEnabled()+"");
        activemap.put("bluetoothstatus",getBluetoothStatus()+"");
        putActiveVersion();
    }

    @Override
    public void setMessageId(String messageId) {
//        this.messageId = messageId;
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
        /*try {
            JSONObject jsonObject = new JSONObject(messageJsonString);
            activemap = new M
            messageId = jsonObject.getString("messageId");

            method = jsonObject.getString("method");
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public String toJsonString() {
        JSONObject jsonObject = new JSONObject(activemap);
        return jsonObject.toString();
    }

    @Override
    public boolean isNeedCache() {
        return false;
    }


    private void putActiveVersion(){
        String obeType = "T1";

        if(versionCode.contains("T"))
            obeType = "T1";
        if(versionCode.contains("D"))
            obeType = "D1";
        if(versionCode.contains("I"))
            obeType = "I1";
        if(versionCode.contains("P"))
            obeType = "P1";
        if(versionCode.contains("E"))
            obeType = "E1";
        activemap.put("obeType",obeType);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private int getBluetoothStatus(){
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(
                Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter != null) {
            if(bluetoothAdapter.isEnabled())
            return 1;
        }
        return 0;
    }

    private int getWifiApEnabled() {
        try {
            if (mWifiManager == null) {
                mWifiManager = (WifiManager) mContext
                        .getSystemService(Context.WIFI_SERVICE);
            }
            Method method = mWifiManager.getClass()
                    .getMethod("isWifiApEnabled");
            method.setAccessible(true);
            if((Boolean) method.invoke(mWifiManager))
                return 1;
        } catch (Exception e) {
            return 0;
        }

        return 0;
    }
}
