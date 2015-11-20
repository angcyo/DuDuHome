package com.dudu.obd.gsensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.MyDate;

/**
 * Created by  dengjun on 2015/11/16.
 * description:
 */
public class GsensorManage implements SensorEventListener {
    private static GsensorManage instance = null;

    private SensorManager mSensorManager;
    private Sensor mAcceSensor; // 加速度传感器;
    private Sensor mGyroscopSensor; // 陀螺仪

    private IGsensorData iGsensorData = null;



    public static  GsensorManage getInstance(Context context){
        if (instance == null){
            synchronized (GsensorManage.class){
                if (instance == null){
                    instance = new GsensorManage(context);

                }
            }
        }
        return instance;
    }

    private GsensorManage(Context context) {
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        mAcceSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscopSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mAcceSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscopSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();


        MotionData motionData = new MotionData();
        motionData.mX = event.values[0];
        motionData.mY = event.values[1];
        motionData.mZ = event.values[2];
        motionData.mCurrentTime = MyDate.date(System.currentTimeMillis());


//        LogUtils.d("GsensorManage", "motionData.mX = " + motionData.mX
//                + "  motionData.mY = " + motionData.mY
//                + "  motionData.mZ = " + motionData.mZ
//                + "  motionData.mCurrentTime = " + motionData.mCurrentTime);

       if(iGsensorData != null){
           iGsensorData.onGsensorData(motionData, type);
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

    public void setiGsensorData(IGsensorData iGsensorData) {
        this.iGsensorData = iGsensorData;
    }
}
