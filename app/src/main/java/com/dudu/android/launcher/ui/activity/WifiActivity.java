package com.dudu.android.launcher.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.db.DbHelper;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;
import com.dudu.android.launcher.ui.view.TasksCompletedView;

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

    private float mTotalFlow = 500;

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
        float remainingFlow = mTotalFlow - usedFlow;

        mUsedFlowView.setText(getString(R.string.used_flow, mDecimalFormat.format(usedFlow)));

        if (remainingFlow <= 0) {
            mRemainingFlowView.setText(getString(R.string.remaining_flow, 0));
        } else {
            mRemainingFlowView.setText(getString(R.string.remaining_flow,
                    mDecimalFormat.format(remainingFlow)));
        }

        int progress = Math.round((usedFlow * 100 / mTotalFlow));
        if (progress > 100) {
            mTaskCompleteView.setProgress(100);
        } else {
            mTaskCompleteView.setProgress(progress);
        }

    }

    public void onBackPressed(View v) {
        finish();
    }

}
