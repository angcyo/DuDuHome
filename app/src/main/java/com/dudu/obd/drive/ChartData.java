package com.dudu.obd.drive;

import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.obd.gsensor.MotionData;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengjun on 2015/11/19.
 * Description :
 */
public class ChartData {
    private ISaveGsensorData mISaveGsensorData = new SaveGsensorDataEntity();

    public  ArrayList<String> xVals;
    public  ArrayList<Entry> yValsAcceSensorx;
    public  ArrayList<Entry> yValsAcceSensory;
    public  ArrayList<Entry> yValsAcceSensorz ;

    public ChartData() {
        init();

    }

    public void init() {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yValsAcceSensorx = new ArrayList<Entry>();
        ArrayList<Entry> yValsAcceSensory = new ArrayList<Entry>();
        ArrayList<Entry> yValsAcceSensorz = new ArrayList<Entry>();

        List<MotionData> motionDataList = mISaveGsensorData.getFromFile(FileUtils.getGsensorDataStorageDir().toString()+"/"+ "GsensorData.txt");
        int size = motionDataList.size();
        for (int i = 0; i < size; i++) {
            xVals.add(i+"");
            yValsAcceSensorx.add(new Entry(motionDataList.get(i).mX, i));
            yValsAcceSensory.add(new Entry(motionDataList.get(i).mY, i));
            yValsAcceSensorz.add(new Entry(motionDataList.get(i).mZ, i));
        }
    }
}
