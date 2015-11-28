package com.dudu.android.launcher.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.db.DbHelper;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;
import com.dudu.android.launcher.ui.view.TasksCompletedView;
import com.dudu.conn.SendMessage;

import java.util.Calendar;

/**
 * Created by Administrator on 2015/10/30.
 */
public class WifiActivity extends BaseTitlebarActivity {

    private TasksCompletedView mTaskCompleteView;
    private TextView mUseText;
    private TextView mUnuseText;

    private DbHelper mDbHelper;

    private Long totalFlow = Long.valueOf(500);

    @Override
    public int initContentView() {
        return R.layout.activity_wifi_layout;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        setContext(this);
        mTaskCompleteView = (TasksCompletedView) findViewById(R.id.tasks_completed);
        mUseText = (TextView) findViewById(R.id.ues_text);
        mUnuseText = (TextView) findViewById(R.id.unuesd_text);

        mDbHelper = DbHelper.getDbHelper(WifiActivity.this);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initDatas() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); //获取当前年份
        int mMonth = c.get(Calendar.MONTH) + 1;//获取当前月份
        long total = (long)(mDbHelper.calculateForMonth(mYear, mMonth, 1) / 1024) ;
        long surplusFlow = totalFlow - total;
        mUseText.append("      " + total + "M");
        if (surplusFlow < 0) {
            mUnuseText.append("      " + 0  + "M");
        } else {
            mUnuseText.append("      " + surplusFlow  + "M");
        }
        int progress = Math.round((total * 100 / totalFlow));
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
