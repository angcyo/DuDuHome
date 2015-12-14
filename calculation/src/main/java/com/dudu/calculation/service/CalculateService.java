package com.dudu.calculation.service;

import android.content.Context;
import android.hardware.Sensor;

import com.dudu.calculation.valueobject.DriveBehaviorType;
import com.dudu.monitor.Monitor;
import com.dudu.monitor.event.CarDriveSpeedState;
import com.dudu.monitor.event.CarStatus;
import com.dudu.monitor.utils.LocationFilter;
import com.dudu.monitor.valueobject.LocationInfo;
import com.dudu.monitor.valueobject.SensorData;
import com.dudu.network.NetworkManage;
import com.dudu.network.event.LocationInfoUpload;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * Created by dengjun on 2015/12/2.
 * Description :
 */
public class CalculateService {
    private ScheduledExecutorService calculateThreadPool = null;
    private Context mContext;
    private Gson gson;

    JSONArray driveLocationInfoArray;
    private Logger log;

    private int carState;

    //计算线程
    private Thread calculateThread = new Thread(){
        @Override
        public void run() {
//            log.debug("calculateThread");
            try {
                calclulateHardturn();

            } catch (Exception e) {
                log.error("异常 {}",e);
            }
        }
    };


    public CalculateService(Context mContext) {
        this.mContext = mContext;
        //核心保持一个线程，这样此模块中线程不用做同步处理
        calculateThreadPool = Executors.newScheduledThreadPool(1);
        driveLocationInfoArray = new JSONArray();
        gson =  new Gson();
        log = LoggerFactory.getLogger("calculate");

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
    }

    public void startService(){
        calculateThreadPool.scheduleAtFixedRate(calculateThread, 5, 1, TimeUnit.SECONDS);
    }

    public void stopService(){
        EventBus.getDefault().unregister(this);

        if (calculateThreadPool != null && !calculateThreadPool.isShutdown()) {
            calculateThreadPool.shutdown();
            calculateThreadPool = null;
        }
    }

    //计算急转弯
    public void calclulateHardturn(){
        List<SensorData> acceSensorDataList = Monitor.getInstance(mContext).getSensorDataList(Sensor.TYPE_ACCELEROMETER);
        if (!acceSensorDataList.isEmpty()){
            float x_sum = 0;
            for (int i = 0; i < acceSensorDataList.size(); i++) {
                x_sum += acceSensorDataList.get(i).mX;
            }
            float avg_x = Math.abs(x_sum / (acceSensorDataList.size())); // 加速度传感器的X轴平均值
            // 速度大于0 x轴方向的绝对值大雨0.5则认为发生了急转弯
//            log.debug("avg_x = "+ avg_x);
            if (Monitor.getInstance(mContext).getCurSpeed() > 0 && avg_x >= 0.5) {
                putLocationInfo(getAndSetTypeLocationInfo(DriveBehaviorType.TYPE_HARDTURN));
            }
            acceSensorDataList.clear();
        }

        List<SensorData> gSensorDataList = Monitor.getInstance(mContext).getSensorDataList(Sensor.TYPE_GYROSCOPE);
        if (!gSensorDataList.isEmpty()){
                   /* float x_sum = 0;
                    for (int i = 0; i < gSensorDataList.size(); i++) {
                        x_sum += gSensorDataList.get(i).mX;
                    }
                    float avg_x = Math.abs(x_sum / (gSensorDataList.size())); // 加速度传感器的X轴平均值
                    // 速度大于0 x轴方向的绝对值大雨0.5则认为发生了急转弯
                    if (Monitor.getInstance(mContext).getCurSpeed() > 0 && avg_x >= 0.5) {

                    }*/
            gSensorDataList.clear();
        }
    }

    //获取当前位置，并且设置驾驶行为类型
    private LocationInfo getAndSetTypeLocationInfo(int driveBehaviorType){
        LocationInfo locationInfo = new LocationInfo(Monitor.getInstance(mContext).getCurrentLocation());
        if (!LocationFilter.checkLatLon(locationInfo.getLat(),locationInfo.getLon()))
            return null;
        locationInfo.setType(driveBehaviorType);
        return locationInfo;
    }

    //把数据放到driveLocationInfoArray，数据满30个就发送
    private void putLocationInfo(LocationInfo locationInfo){
        try {
            if (locationInfo == null)
                return;
            if(locationInfo.getSpeeds() > 0 ){
                 driveLocationInfoArray.put(new JSONObject(gson.toJson(locationInfo)));
            }
//            log.debug("driveLocationInfoArray.length() = " + driveLocationInfoArray.length());
            if (driveLocationInfoArray.length() == 30)//满30个数据才给服务器发
            {
                NetworkManage.getInstance().sendMessage(new LocationInfoUpload(mContext, driveLocationInfoArray));
                driveLocationInfoArray = null;
                driveLocationInfoArray = new JSONArray();
            }
        } catch (JSONException e) {
            log.error("异常 {}",e);
        }
    }



    public void onEventBackgroundThread(CarStatus event) {
        carState = event.getCarStatus();
        log.info("onEvent CarStatus:{}", carState);
        if (carState == CarStatus.CAR_ONLINE){
            log.info("车辆运行");
            calculateThreadPool.schedule(fatigueDrivingThread, 4*60*60, TimeUnit.SECONDS);//4个小时
        }else{
            log.info("车辆熄火");
        }
    }

    //处理疲劳驾驶
    Thread fatigueDrivingThread = new Thread(){
        @Override
        public void run() {
            try {
                if(carState == 1)
                    calculateThreadPool.schedule(fatigueDrivingThread, 15 * 60, TimeUnit.SECONDS);//15分钟
                log.info("车辆运行超过4小时，疲劳驾驶");
                putLocationInfo(getAndSetTypeLocationInfo(DriveBehaviorType.TYPE_FATIGUEDRIVING));
            } catch (Exception e) {
                log.error("异常 {}", e);
            }
        }
    };


    public void onEventBackgroundThread(CarDriveSpeedState carDriveSpeedState) {
        try {
            switch (carDriveSpeedState.getSpeedState()){
                case DriveBehaviorType.TYPE_HARDACCL:
                    putLocationInfo(getAndSetTypeLocationInfo(DriveBehaviorType.TYPE_HARDACCL));
                    break;
                case DriveBehaviorType.TYPE_HARDBRAK:
                    putLocationInfo(getAndSetTypeLocationInfo(DriveBehaviorType.TYPE_HARDBRAK));
                    break;
                case DriveBehaviorType.TYPE_MISMATCH:
                    putLocationInfo(getAndSetTypeLocationInfo(DriveBehaviorType.TYPE_MISMATCH));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("异常 {}", e);
        }
    }
}
