package com.dudu.android.launcher.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Switch;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.obd.drive.ChartData;
import com.dudu.obd.drive.GsensorLineChart;
import com.dudu.obd.gsensor.GsensorManage;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class GsensorChartActivity extends Activity implements View.OnClickListener {

    public LineChart mChart;
    private Button backButton;
    private Button realButton;
    private Button fileButton;
    private Button nextButton;

    GsensorLineChart gsensorLineChart = null;
    GsensorManage gsensorManage = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gsensor_chart);
        LogUtils.d("Gsensor", "onCreate");

        initView();

        gsensorLineChart = new GsensorLineChart(mChart, this);
        gsensorManage = GsensorManage.getInstance(this);
        gsensorManage.setiGsensorData(gsensorLineChart);
    }

    private void initView(){
        mChart = (LineChart) findViewById(R.id.chart);
        backButton = (Button)findViewById(R.id.back);
        realButton = (Button)findViewById(R.id.real);
        fileButton = (Button)findViewById(R.id.file);
        nextButton = (Button)findViewById(R.id.next);

        backButton.setOnClickListener(this);
        realButton.setOnClickListener(this);
        fileButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        nextButton.setVisibility(View.GONE);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        gsensorManage.release();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.real:
                gsensorLineChart.showData(true);
                break;
            case R.id.file:
                gsensorLineChart.showData(false);
                break;
            case R.id.next:
                break;
            default:
                break;
        }
    }
}
