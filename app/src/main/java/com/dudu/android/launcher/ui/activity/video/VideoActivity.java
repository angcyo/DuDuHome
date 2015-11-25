package com.dudu.android.launcher.ui.activity.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.service.RecordBindService;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.ToastUtils;

import java.io.File;

public class VideoActivity extends BaseNoTitlebarAcitivity {

    private VideoPreviewReceiver mPreviewReceiver;

    private RecordBindService mRecordService;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (mRecordService != null) {
                mRecordService.updatePreviewSize(1, 1);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!FileUtils.isTFlashCardExists()) {
            ToastUtils.showToast(R.string.video_sdcard_removed_alert);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initRecordService();
    }

    private void initRecordService() {
        LauncherApplication application = ((LauncherApplication) getApplication());
        if (application != null) {
            mRecordService = application.getRecordService();
            if (mRecordService != null) {
                mRecordService.updatePreviewSize(854, 480);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRecordService != null) {
            mRecordService.updatePreviewSize(1, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterPreviewReceiver();

        if (mRecordService != null) {
            mRecordService.stopRecord();
            mRecordService.stopRecordTimer();
        }
    }

    @Override
    public int initContentView() {
        return R.layout.video_activity;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
    }

    @Override
    public void initListener() {

    }

    public void onBackPressed(View v) {
        finish();
    }

    @Override
    public void initDatas() {
        registerPreviewReceiver();
    }

    private void registerPreviewReceiver() {
        mPreviewReceiver = new VideoPreviewReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.VIDEO_PREVIEW_BROADCAST);
        registerReceiver(mPreviewReceiver, intentFilter);
    }

    private void unregisterPreviewReceiver() {
        if (mPreviewReceiver != null) {
            unregisterReceiver(mPreviewReceiver);
        }
    }

    private class VideoPreviewReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
            mHandler.sendEmptyMessageDelayed(0, 500);
        }
    }

}
