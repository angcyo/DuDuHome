package com.dudu.conn;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.dudu.android.launcher.utils.DeviceIDUtil;
import com.dudu.obd.FlamoutData;
import com.google.gson.Gson;
import com.iflytek.cloud.Setting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lxh on 2015/11/7.
 */
public class SendMessage {

    private Connection conn;

    private static SendMessage sendMessage;

    public static final String OBEID = "obeId";

    private static final String METHOD = "method";

    private static final String VERSION_CODE = "";

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
            conn.sendMessage(sendJson.toString(), true);
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
            sendJson.put(OBEID,obeId );
            sendJson.put("obds", obdDatajson);
            conn.sendMessage(sendJson.toString(), true);
            sendJson.put(VERSION_CODE, versionCode);
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
        conn.sendMessage(gson.toJson(flameoutData),true);
        sendGPSDatas(gpsDatas);
        return conn.isAlive();
    }

    public boolean sendActiveDeviceData(String data){

        activemap = new HashMap<>();
        activemap.put("ro.board.platform","msm8916");
        activemap.put("ro.build.fingerprint","qcom/msm8916_32/msm8916_32:4.4.4/KTU84P/eng.duxiaodong.20151105:user/test-keys");
        activemap.put("ro.product.manufacturer","DuDuSmartTech");
        activemap.put("ro.product.model","T-ONE");
        activemap.put("ro.serialno", DeviceIDUtil.getAndroidID(mContext));
        activemap.put("sim.seralno",DeviceIDUtil.getSimSerialNumber(mContext));
        activemap.put("launcher.version", versionCode);
        activemap.put(OBEID, obeId);
        activemap.put(METHOD, ConnMethod.METHOD_ACTIVEDEVICE);
        putActiveVersion();
        JSONObject jsonObject = new JSONObject(activemap);
        conn.sendMessage(jsonObject.toString(), true);
        return conn.isAlive();

    }

    private void  getVersionName() {
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = mContext.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            versionCode = packInfo.versionName;

        } catch (Exception e) {

        }
    }
    private void putActiveVersion(){
        int obeType = 0;

        if(versionCode.contains("T"))
            obeType = 1;
        if(versionCode.contains("D"))
            obeType = 2;
        if(versionCode.contains("I"))
            obeType = 3;
        if(versionCode.contains("P"))
            obeType = 4;
        if(versionCode.contains("E"))
            obeType = 5;

        activemap.put("obeType",obeType+"");
    }
}
