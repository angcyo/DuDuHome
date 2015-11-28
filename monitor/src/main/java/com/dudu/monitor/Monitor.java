package com.dudu.monitor;

import android.content.Context;
import android.hardware.Sensor;

import com.amap.api.location.AMapLocation;
import com.dudu.monitor.repo.ObdManage;
import com.dudu.monitor.repo.SensorManage;
import com.dudu.monitor.repo.location.LocationManage;
import com.dudu.monitor.valueobject.FlamoutData;
import com.dudu.monitor.valueobject.LocationInfo;
import com.dudu.monitor.valueobject.ObdData;
import com.dudu.monitor.valueobject.SensorData;

import java.util.List;

/**
 * Created by dengjun on 2015/11/25.
 * Description :
 */
public class Monitor {
    private static  Monitor instance = null;
    private Context mContext;

    private LocationManage mLocationManage;
    private SensorManage mSensorManage;
    private ObdManage mObdManage;

    public static  Monitor getInstance(Context context){
        if (instance == null){
            synchronized (Monitor.class){
                if (instance == null){
                    instance = new Monitor(context);
                }
            }
        }
        return instance;
    }

    private Monitor(Context context){
        mContext = context;
        mLocationManage = LocationManage.getInstance();
//        mSensorManage = SensorManage.getInstance(context);
//        mObdManage = ObdManage.getInstance();
    }

    public void startWork(){
        mLocationManage.startLocation(mContext);
    }

    public void stopWork(){
        mLocationManage.stopLocation();
//        mSensorManage.release();
//        mObdManage.release();
    }

    //获取高德定位未过滤位置数据
    public AMapLocation getCurrentLocation(){
       return  mLocationManage.getCurrentLoction();
    }

    //获取当前的位置信息，已过滤
    public LocationInfo getCurLocation(){
        return mLocationManage.getCurLocation();
    }

    public List<SensorData> getSensorDataList(int sensorType){
        if (sensorType == Sensor.TYPE_ACCELEROMETER){
            return mSensorManage.getmAcceSensorList();
        }else if (sensorType == Sensor.TYPE_GYROSCOPE){
            return mSensorManage.getmGyroscopSensorList();
        }
        return null;
    }

    //获取当前车速
    public int getCurSpeed(){
       return mObdManage.getCurSpeed();
    }

    //获取当前转速
    public float getCurRpm(){
        return mObdManage.getCurRpm();
    }

}
