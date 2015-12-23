package com.dudu.monitor.service;

import android.content.Context;

import com.dudu.android.hideapi.SystemPropertiesProxy;
import com.dudu.monitor.repo.ActiveDeviceManage;
import com.dudu.monitor.repo.ObdManage;
import com.dudu.monitor.repo.location.LocationManage;
import com.dudu.monitor.valueobject.LocationInfo;
import com.dudu.monitor.valueobject.ObdData;
import com.dudu.network.NetworkManage;
import com.dudu.network.event.ActiveDevice;
import com.dudu.network.event.CheckDeviceActive;
import com.dudu.network.event.DriveDatasUpload;
import com.dudu.network.event.LocationInfoUpload;
import com.dudu.network.event.ObdDatasUpload;
import com.dudu.network.event.Portal;
import com.dudu.network.utils.DeviceIDUtil;
import com.dudu.network.utils.DuduLog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by dengjun on 2015/11/25.
 * Description :
 */
public class SendService {

    private ScheduledExecutorService sendServiceThreadPool = null;
    private Gson gson;
    private String obdId = "";

    private Logger log;
    private Context mContext;

    private Thread sendServiceThread = new Thread(){
        @Override
        public void run() {
            log.info("monitor 发送服务");
            try {
//                activeDevice();

                sendLocationInfo();

                sendObdData();

                sendFlamoutData();

                sendPortalCount();
            }catch (Exception e){
                e.printStackTrace();
                log.error("monitor-发送服务异常" + e);
            }
        }
    };

    public SendService(Context context) {
        sendServiceThreadPool = Executors.newScheduledThreadPool(1);
        gson =  new Gson();
        obdId = DeviceIDUtil.getIMEI(context);
        mContext = context;
        log = LoggerFactory.getLogger("monitor");
    }

    public void startSendService(){
        activeDevice();
        sendServiceThreadPool.scheduleAtFixedRate(sendServiceThread, 4, 30, TimeUnit.SECONDS);
    }

    public void stopSendService(){
        if (sendServiceThreadPool != null && !sendServiceThreadPool.isShutdown()) {
            sendServiceThreadPool.shutdown();
            sendServiceThreadPool = null;
        }
    }

    //发送位置数据
    private void sendLocationInfo(){
        if (LocationManage.getInstance().getLocationInfoList().size() == 0)
            return;

        DuduLog.i("monitor-发送位置信息");
        JSONArray locationInfoArray = new JSONArray();
        try {
            for (int i = 0; i < LocationManage.getInstance().getLocationInfoList().size(); i++) {
                LocationInfo locationInfo = LocationManage.getInstance().getLocationInfoList().get(i);
//                DuduLog.d("monitor-位置信息："+ gson.toJson(locationInfo).toString() );
                if (locationInfo != null){
                    locationInfoArray.put(i, new JSONObject(gson.toJson(locationInfo)));
                }
            }
            if (locationInfoArray != null){
                NetworkManage.getInstance().sendMessage(new LocationInfoUpload(obdId, locationInfoArray));
            }
            LocationManage.getInstance().getLocationInfoList().clear();
        }catch (JSONException e) {
            log.error("monitor-发送位置信息异常：" + e);
        }
    }

    //发送obd数据
    private void sendObdData(){
        if (ObdManage.getInstance().getObdDataList().size() == 0)
            return;
        log.info("monitor-发送obd信息");
        JSONArray obdDataArray = new JSONArray();
        try{
            for (int i = 0; i < ObdManage.getInstance().getObdDataList().size(); i++){
                ObdData obdData = ObdManage.getInstance().getObdDataList().get(i);
//                log.debug("monitor-obd数据："+ gson.toJson(obdData).toString());
                if (obdData != null){
                    obdDataArray.put(i,new JSONObject(gson.toJson(obdData)));
                }
            }
            if (obdDataArray != null){
                NetworkManage.getInstance().sendMessage(new ObdDatasUpload(obdId,obdDataArray));
            }
            ObdManage.getInstance().getObdDataList().clear();
        }catch (JSONException e) {
            log.error("monitor-发送obd数据异常" + e);
//            DuduLog.e("monitor-发送obd数据异常", e);
        }
    }

    //发送熄火数据
    private  void sendFlamoutData(){
        if (ObdManage.getInstance().getFlamoutData() != null){
            log.info("monitor-发送obd-熄火-信息");
            LocationInfo locationInfo = new LocationInfo(LocationManage.getInstance().getCurrentLoction());
            JSONArray locationInfoArray = new JSONArray();
            try{
                if (locationInfo != null){
                    locationInfoArray.put(new JSONObject(gson.toJson(locationInfo)));
                }
                if (locationInfoArray != null){
                    NetworkManage.getInstance().sendMessage(new LocationInfoUpload(obdId, locationInfoArray));
                }

                NetworkManage.getInstance().sendMessage(new DriveDatasUpload(obdId,
                        new JSONObject(gson.toJson(ObdManage.getInstance().getFlamoutData()))));
                ObdManage.getInstance().setFlamoutData(null);
            }catch (JSONException e) {
                log.error("monitor-发送obd-熄火-信息 异常："+ e);
            }
        }
    }

    //设备激活
    private void activeDevice(){
        log.info("monitor-检查设备激活 activeFlag：{}", ActiveDeviceManage.getInstance(mContext).getActiveFlag());
//        if(ActiveDeviceManage.getInstance(mContext).getActiveFlag()!= ActiveDeviceManage.ACTIVE_OK) {
            NetworkManage.getInstance().sendMessage(new CheckDeviceActive(mContext));
//        }
    }

    //发送portal弹出次数
    private void sendPortalCount() {
        SystemPropertiesProxy.getInstance().set(mContext, "persist.sys.nodog", "views");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String portalCount = SystemPropertiesProxy.getInstance().get("persist.sys.views", "0");
        NetworkManage.getInstance().sendMessage(new Portal(mContext,portalCount));
    }
}
