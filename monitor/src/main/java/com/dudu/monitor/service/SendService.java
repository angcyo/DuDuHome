package com.dudu.monitor.service;

import com.dudu.monitor.repo.SensorManage;
import com.dudu.monitor.repo.location.LocationManage;
import com.dudu.monitor.valueobject.LocationInfo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by dengjun on 2015/11/25.
 * Description :
 */
public class SendService {

    private ScheduledExecutorService sendServiceThreadPool = null;

    private Thread sendServiceThread = new Thread(){
        @Override
        public void run() {
            try {
                LocationManage.getInstance().getLocationInfoList().clear();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    public SendService() {
        sendServiceThreadPool = Executors.newScheduledThreadPool(1);
    }

    public void startService(){
        sendServiceThreadPool.scheduleAtFixedRate(sendServiceThread, 5, 30, TimeUnit.SECONDS);
    }

    public void stopSendService(){
        if (sendServiceThreadPool != null && !sendServiceThreadPool.isShutdown()) {
            sendServiceThreadPool.shutdown();
            sendServiceThreadPool = null;
        }
    }
}
