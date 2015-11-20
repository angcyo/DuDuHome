package com.dudu.obd.drive;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.support.v4.content.ContextCompat;

import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.MyDate;
import com.dudu.obd.gsensor.IGsensorData;
import com.dudu.obd.gsensor.MotionData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dengjun on 2015/11/18.
 * Description :
 */
public class GsensorLineChart implements IGsensorData {

//    private LineChart mChart;

    private List<MotionData> mAcceList;

    public List<MotionData> mReadList;

    private ISaveGsensorData mISaveGsensorData;
    private GsensorChartData mGsensorChartData;

    private boolean isReal = true;

    String filePath;

    public GsensorLineChart(LineChart lineChart, Context context) {
//        mChart = lineChart;
        mGsensorChartData = new GsensorChartData(context, lineChart);
//        mGsensorChartData.setmChart(lineChart);
        mGsensorChartData.init(100);

        mISaveGsensorData = new SaveGsensorDataEntity();

        mAcceList = Collections.synchronizedList(new ArrayList<MotionData>());

        filePath = FileUtils.getGsensorDataStorageDir().toString()+"/"+ "Gd-"+ MyDate.getDateForLog()+".txt";
    }


    @Override
    public void onGsensorData(MotionData motionData, int type) {
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
                mAcceList.add(motionData);
                break;
//            case Sensor.TYPE_GYROSCOPE:
//                mGyrList.add(motionData);
//                break;
            default:
                break;
        }
        if (mAcceList.size() == 100/* && isReal == true*/){
            LogUtils.d("Gsensor", "实时数据");
            mISaveGsensorData.saveToFile(mAcceList, filePath);
            if (isReal){
                mGsensorChartData.addAcceSensorData(mAcceList);
                mGsensorChartData.invalidate();
            }


//            mReadList = mISaveGsensorData.getFromFile(filePath);

            mAcceList.clear();

        }

    }


    public void showData(boolean isRealData){
        if (!isRealData){
            this.isReal = false;
            LogUtils.d("Gensor", "文件数据");
            mReadList = mISaveGsensorData.getFromFile(filePath);
            if (mReadList != null){
//                mGsensorChartData.getmChart().clear();
//                mGsensorChartData.getmChart().invalidate();
                mGsensorChartData.init(mReadList.size());
                mGsensorChartData.addAcceSensorData(mReadList);

                mGsensorChartData.invalidate();
                mReadList.clear();
            }

        }else {
            mGsensorChartData.getmChart().clear();
            mGsensorChartData.getmChart().invalidate();
            this.isReal = true;
            LogUtils.d("Gensor", "实时数据---"+ this.isReal);
        }
    }

//    public  synchronized void refleshData(List<MotionData> motionDataList){
//        mGsensorChartData.addAcceSensorData(motionDataList);
//        mGsensorChartData.invalidate();
//    }
}
