package com.dudu.obd.drive;

import android.hardware.Sensor;

import com.dudu.obd.gsensor.IGsensorData;
import com.dudu.obd.gsensor.MotionData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dengjun on 2015/11/17.
 * Description :驾驶行为分析类
 */
public class DriveBehaviorAnalyze implements IGsensorData{

    private List<MotionData> mAcceList;
    private List<MotionData> mGyrList;

    public DriveBehaviorAnalyze() {
        mAcceList = Collections.synchronizedList(new ArrayList<MotionData>());
        mGyrList = Collections.synchronizedList(new ArrayList<MotionData>());
    }

    @Override
    public void onGsensorData(MotionData motionData, int type) {
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
                mAcceList.add(motionData);
                break;
            case Sensor.TYPE_GYROSCOPE:
                mGyrList.add(motionData);
                break;
            default:
                break;
        }
    }



}
