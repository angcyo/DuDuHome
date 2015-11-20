package com.dudu.obd.drive;

import com.dudu.obd.gsensor.MotionData;

import java.util.List;

/**
 * Created by dengjun on 2015/11/18.
 * Description :
 */
public interface ISaveGsensorData {
    public void


    saveToFile(List<MotionData> motionDataList, String filePath);

    public List<MotionData> getFromFile(String filePath);
}
