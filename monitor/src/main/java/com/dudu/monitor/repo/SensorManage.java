package com.dudu.monitor.repo;

import android.content.Context;
import android.hardware.*;
import android.hardware.Sensor;

import com.dudu.monitor.valueobject.SensorData;
import com.dudu.monitor.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dengjun on 2015/11/25.
 * Description :
 */
public class SensorManage  implements SensorEventListener {
    private static SensorManage instance = null;

    private SensorManager mSensorManager;
    private Sensor mAcceSensor; // 加速度传感器;
    private Sensor mGyroscopSensor; // 陀螺仪

    private List<SensorData>  mGyroscopSensorList;
    private List<SensorData>  mAcceSensorList;

    public static  SensorManage getInstance(Context context){
        if (instance == null){
            synchronized (SensorManage.class){
                if (instance == null){
                    instance = new SensorManage(context);
                }
            }
        }
        return instance;
    }

    private SensorManage(Context context) {
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        mAcceSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscopSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mSensorManager.registerListener(this, mAcceSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscopSensor, SensorManager.SENSOR_DELAY_NORMAL);

        mGyroscopSensorList = Collections.synchronizedList(new ArrayList<SensorData>());
        mAcceSensorList = Collections.synchronizedList(new ArrayList<SensorData>());
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();

        SensorData  sensorData = new SensorData();
        sensorData.mX = event.values[0];
        sensorData.mY = event.values[1];
        sensorData.mZ = event.values[2];
        sensorData.mCurrentTime = TimeUtils.getDateString("yyyyMMddHHmmss");

        if (sensorType == Sensor.TYPE_ACCELEROMETER){
            mAcceSensorList.add(sensorData);
        }else if (sensorType == Sensor.TYPE_GYROSCOPE){
            mGyroscopSensorList.add(sensorData);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /* 释放资源*/
    public void release(){
        mSensorManager.unregisterListener(this);
        instance = null;
    }

    public List<SensorData> getmAcceSensorList() {
        return mAcceSensorList;
    }

    public List<SensorData> getmGyroscopSensorList() {
        return mGyroscopSensorList;
    }
}
