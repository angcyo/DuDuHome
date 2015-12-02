package com.dudu.android.launcher.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.db.DbHelper;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;
import com.dudu.android.launcher.ui.view.TasksCompletedView;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.SharedPreferencesUtil;
import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2015/10/30.
 */
public class WifiActivity extends BaseTitlebarActivity {

    private TasksCompletedView mTaskCompleteView;
    private TextView mUsedFlowView;
    private TextView mRemainingFlowView;

    private DbHelper mDbHelper;

    private DecimalFormat mDecimalFormat = new DecimalFormat("0.00");

    private float mTotalFlow = 0;

    private float remainingFlow= 0;

    private static final String DEFAULT_FLOW_VALUE="1024";

    @Override
    public int initContentView() {
        return R.layout.activity_wifi_layout;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setContext(this);
        mTaskCompleteView = (TasksCompletedView) findViewById(R.id.tasks_completed);
        mUsedFlowView = (TextView) findViewById(R.id.used_text);
        mRemainingFlowView = (TextView) findViewById(R.id.remaining_flow_text);

        mDbHelper = DbHelper.getDbHelper(WifiActivity.this);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initDatas() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;

       float usedFlow = mDbHelper.calculateForMonth(year, month, 1) / 1024;
        //float remainingFlow = mTotalFlow - usedFlow;
        remainingFlow = Float.parseFloat(SharedPreferencesUtil.getStringValue(this, Constants.KEY_REMAINING_FLOW, DEFAULT_FLOW_VALUE))/1024;

        mTotalFlow=Float.parseFloat(SharedPreferencesUtil.getStringValue(this,Constants.KEY_MONTH_MAX_VALUE,DEFAULT_FLOW_VALUE))/1024;

        mUsedFlowView.setText(getString(R.string.used_flow, mDecimalFormat.format(usedFlow)));

        if (remainingFlow <= 0) {
            mRemainingFlowView.setText(getString(R.string.remaining_flow, 0));
        } else {
            mRemainingFlowView.setText(getString(R.string.remaining_flow,
                    mDecimalFormat.format(remainingFlow)));
        }

        int progress = Math.round((remainingFlow * 100 / mTotalFlow));
        if (progress > 100) {
            mTaskCompleteView.setProgress(100);
        } else {
            String message="";
            if(progress>20){
                //流量使用正常
                message=getString(R.string.use_flow_normal);
            }else if (progress>15){
                //流量低级预警
                message=getString(R.string.use_flow_low_alarm);
            }else if(progress>10){
                //流量中级预警
                message=getString(R.string.use_flow_middle_alarm);
            }else if(progress>5){
                //流量高级预警
                message=getString(R.string.use_flow_high_alarm);
            }else {
                //关闭流量
                message=getString(R.string.use_close_flow);
            }
            showFlowToast(message);
            mTaskCompleteView.setProgress(progress);
        }

    }

    public void onBackPressed(View v) {
        finish();
    }
    private void showFlowToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}
