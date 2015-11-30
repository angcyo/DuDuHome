package com.dudu.monitor.service;

import android.content.Context;

import com.dudu.monitor.repo.SensorManage;
import com.dudu.monitor.repo.location.LocationManage;
import com.dudu.monitor.valueobject.LocationInfo;
import com.dudu.network.NetworkManage;
import com.dudu.network.event.LocationInfoUpload;
import com.dudu.network.utils.DeviceIDUtil;
import com.dudu.network.utils.DuduLog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scf4a.Event;

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

    private Thread sendServiceThread = new Thread(){
        @Override
        public void run() {
            DuduLog.d("monitor 发送服务");
            try {
                sendLocationInfo();
            }catch (Exception e){
                e.printStackTrace();
                DuduLog.e("monitor发送服务异常", e);
            }
        }
    };

    public SendService(Context context) {
        sendServiceThreadPool = Executors.newScheduledThreadPool(1);
        gson =  new Gson();
        obdId = DeviceIDUtil.getIMEI(context);
    }

    public void startSendService(){
        sendServiceThreadPool.scheduleAtFixedRate(sendServiceThread, 5, 30, TimeUnit.SECONDS);
    }

    public void stopSendService(){
        if (sendServiceThreadPool != null && !sendServiceThreadPool.isShutdown()) {
            sendServiceThreadPool.shutdown();
            sendServiceThreadPool = null;
        }
    }


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
            DuduLog.e("monitor-发送位置信息异常", e);
        }
    }
}
