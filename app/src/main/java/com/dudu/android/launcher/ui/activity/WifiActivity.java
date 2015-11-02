package com.dudu.android.launcher.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseTitlebarActivity;
import com.dudu.android.launcher.ui.view.TasksCompletedView;

/**
 * Created by Administrator on 2015/10/30.
 */
public class WifiActivity extends BaseTitlebarActivity {

    private TasksCompletedView mTaskCompleteView;

    @Override
    public int initContentView() {
        return R.layout.activity_wifi_layout;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        mTaskCompleteView = (TasksCompletedView) findViewById(R.id.tasks_completed);
    }

    @Override
    public void initListener() {
    }

    @Override
    public void initDatas() {
        mTaskCompleteView.setProgress(50);
    }

    public void onBackPressed(View v) {
        finish();
    }

}
