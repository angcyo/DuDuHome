package com.dudu.conn;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.dudu.android.launcher.utils.DeviceIDUtil;
import com.dudu.obd.FlamoutData;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pc on 2015/11/7.
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
        obeId = DeviceIDUtil.getAndroidID(context);
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
        conn.sendMessage(gson.toJson(flameoutData),true);
        sendGPSDatas(gpsDatas);
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


}
