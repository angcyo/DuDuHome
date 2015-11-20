package com.dudu.obd.drive;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;

import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.obd.gsensor.MotionData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengjun on 2015/11/18.
 * Description :
 */
public class GsensorChartData {
//    private static  GsensorChartData instance = null;

    private int capacity = 100;
    private int currentSize;

    ArrayList<String> xVals;
    ArrayList<Entry> yValsAcceSensorx;
    ArrayList<Entry> yValsAcceSensory;
    ArrayList<Entry> yValsAcceSensorz ;

//    LineDataSet acceSensorxSet;
//    LineDataSet acceSensorySet;
//    LineDataSet acceSensorzSet;

    private Context mContext;
    private Handler handler;

    private LineChart mChart;

    /*public static  GsensorChartData getInstance(){
        if (instance == null){
            synchronized (GsensorChartData.class){
                if (instance == null){
                    instance = new GsensorChartData();
                }
            }
        }
        return instance;
    }*/

    public GsensorChartData(Context context, LineChart lineChart) {
    mChart = lineChart;
        handler = new Handler();

        initLineChart(context);
    }

    public void init(int capacity){
        this.capacity = capacity;
        xVals = new ArrayList<String>(capacity);
        yValsAcceSensorx = new ArrayList<Entry>(capacity);
        yValsAcceSensory = new ArrayList<Entry>(capacity);
        yValsAcceSensorz = new ArrayList<Entry>(capacity);


        LogUtils.d("G", "初始化");
    }

    private void  initLineChart(Context context){
        XAxis xAxis = mChart.getXAxis();
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis); // add x-axis limit line

        Typeface tf = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");

        LimitLine ll1 = new LimitLine(42f, "Upper Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(1f, 1f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);

        LimitLine ll2 = new LimitLine(-42f, "Lower Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(1f, 1f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setTypeface(tf);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaxValue(50f);
        leftAxis.setAxisMinValue(-50f);
        leftAxis.setStartAtZero(false);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);


        mChart.setData(new LineData());
    }



    private LineDataSet createLineDataSet(List<Entry> dataEntryList, int color, String lable){
        LineDataSet set = new LineDataSet(dataEntryList,  lable);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(color);
        set.setCircleColor(color);
        set.setLineWidth(1f);
        set.setCircleSize(3f);
        set.setDrawCircleHole(false);
        set.setValueTextSize(9f);
        set.setFillAlpha(65);
        set.setFillColor(color);

        return set;
    }


    public void addAcceSensorData(List<MotionData> motionDataList){
        if (xVals == null)
            init(100);
        xVals.clear();
        yValsAcceSensorx.clear();
        yValsAcceSensory.clear();
        yValsAcceSensorz.clear();

        int size = motionDataList.size();
        for (int i = 0; i < size; i++) {
            xVals.add(i+"");
            yValsAcceSensorx.add(new Entry(motionDataList.get(i).mX, i));
            yValsAcceSensory.add(new Entry(motionDataList.get(i).mY, i));
            yValsAcceSensorz.add(new Entry(motionDataList.get(i).mZ, i));
        }
    }

    private LineData generateData(){
//        LineData lineData = mChart.getLineData();
//        lineData.removeDataSet(0);
//        lineData.removeDataSet(1);
//        lineData.removeDataSet(2);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(createLineDataSet(yValsAcceSensorx, Color.RED, "acceSensorxSet"));
        dataSets.add(createLineDataSet(yValsAcceSensory, Color.YELLOW, "acceSensorySet"));
        dataSets.add(createLineDataSet(yValsAcceSensorz, Color.BLUE, "acceSensorzSet"));

        LineData data = new LineData(xVals, dataSets);
        return data;
    }

    public void invalidate(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                mChart.clear();
                mChart.setData(generateData());
                mChart.notifyDataSetChanged();
                mChart.invalidate();
            }
        });
    }



    public LineChart getmChart() {
        return mChart;
    }

    public void setmChart(LineChart mChart) {
        this.mChart = mChart;
    }
}
