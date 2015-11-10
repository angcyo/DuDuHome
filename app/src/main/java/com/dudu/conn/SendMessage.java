package com.dudu.conn;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.dudu.android.launcher.utils.DeviceIDUtil;
import com.dudu.android.launcher.utils.Encrypt;
import com.dudu.obd.FlamoutData;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.core.android.SystemPropertiesProxy;

/**
 * Created by lxh on 2015/11/7.
 */
public class SendMessage {

    private Connection conn;

    private static SendMessage sendMessage;

    public static final String OBEID = "obeId";

    private static final String METHOD = "method";

    private static final String VERSION_CODE = "launcher.version";

    private JSONObject sendJson;

    private Context mContext;

    private String versionCode ;

    private  Gson gson;

    private String obeId = "";

    Map<String,String> activemap;

    public static SendMessage getInstance(Context context){
         if(sendMessage==null)
             sendMessage = new SendMessage(context);
        return sendMessage;
    }

    public SendMessage(Context context){
        conn = Connection.getInstance(context);
        this.mContext = context;
        getVersionName();
        gson = new Gson();
        obeId = DeviceIDUtil.getIMEI(context);
    }

    public void sendData(String data){

//        if(ActiveDevice.getInstance(mContext).getActiveFlag()==ActiveDevice.ACTIVE_OK){
        try {
            conn.getlog().debug("sendData:{}",data);
            conn.sendMessage(Encrypt.AESEncrypt(sendJson.toString(),Encrypt.vi), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
    }

    /**
     * 发送gps 数据
     * @param gpsDatajson
     * @return
     */
    public boolean sendGPSDatas(JSONArray gpsDatajson){

        sendJson = new JSONObject();
        try {
            sendJson.put(METHOD, ConnMethod.METHOD_GPSDATA);
            sendJson.put(OBEID,obeId);
            sendJson.put("lals", gpsDatajson);
            sendJson.put(VERSION_CODE, versionCode);
            sendData(sendJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return conn.isAlive();
    }




    /**
     * 发送obd数据
     * @param obdDatajson
     * @return
     */
    public boolean sendOBDDatas(JSONArray obdDatajson){

        sendJson = new JSONObject();
        try {
            sendJson.put(METHOD, ConnMethod.METHOD_OBDDATA);
            sendJson.put(OBEID, obeId);
            sendJson.put("obds", obdDatajson);
            sendJson.put(VERSION_CODE, versionCode);
            sendData(sendJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return conn.isAlive();
    }

    /**
     * 发送熄火数据
     * @param flameoutData
     * @param gpsDatas
     * @return
     */
    public boolean sendFlameOutData(FlamoutData flameoutData,JSONArray gpsDatas){
        flameoutData.setObeId(obeId);
        flameoutData.setMethod(ConnMethod.METHOD_FLAMEOUTDATA);
        sendData(gson.toJson(flameoutData));
        sendGPSDatas(gpsDatas);
        return conn.isAlive();
    }

    public boolean sendActiveDeviceData(){

        SystemPropertiesProxy sps = SystemPropertiesProxy.getInstance();
        activemap = new HashMap<>();
        activemap.put("ro.board.platform",sps.get("ro.board.platform","UNKNOWN"));
        activemap.put("ro.build.fingerprint",sps.get("ro.build.fingerprint","UNKNOWN"));
        activemap.put("ro.product.manufacturer",sps.get("ro.product.manufacturer","UNKNOWN"));
        activemap.put("ro.product.model",sps.get("ro.product.model","UNKNOWN"));
        activemap.put("ro.serialno", DeviceIDUtil.getAndroidID(mContext));
        activemap.put("sim.seralno",DeviceIDUtil.getSimSerialNumber(mContext));
        activemap.put("launcher.version", versionCode);
        activemap.put(OBEID, obeId);
        activemap.put(METHOD, ConnMethod.METHOD_ACTIVEDEVICE);
        putActiveVersion();
        JSONObject jsonObject = new JSONObject(activemap);
        try {
            String msg = Encrypt.AESEncrypt(jsonObject.toString(),Encrypt.vi);
            conn.sendMessage(msg);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return conn.isAlive();
    }

    private void  getVersionName() {
        try {
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            versionCode = packInfo.versionName;

        } catch (Exception e) {

        }
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
}
