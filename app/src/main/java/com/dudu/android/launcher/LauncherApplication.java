package com.dudu.android.launcher;

import android.app.Application;
import android.content.Intent;

import com.dudu.android.launcher.exception.CrashHandler;
import com.dudu.android.launcher.service.NewMessageShowService;
import com.dudu.android.launcher.service.RecordBindService;
import com.dudu.android.launcher.utils.NetworkUtils;
import com.iflytek.cloud.Setting;

public class LauncherApplication extends Application {

    public static LauncherApplication mApplication;

    private boolean mReceivingOrder = false;

    private RecordBindService mRecordService;

    public static LauncherApplication getContext() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        Setting.showLogcat(false);

        CrashHandler crashHandler = CrashHandler.getInstance();

        // 注册crashHandler
        crashHandler.init(getApplicationContext());

        startFloatMessageService();

        //将htdocs压缩包解压
        NetworkUtils.writePortalConfig(this);
    }

    private void startFloatMessageService() {
        Intent intent = new Intent(LauncherApplication.this, NewMessageShowService.class);
        startService(intent);
    }

    public RecordBindService getRecordService() {
        return mRecordService;
    }

    public void setRecordService(RecordBindService service) {
        mRecordService = service;
    }

    public boolean isReceivingOrder() {
        return mReceivingOrder;
    }

    public void setReceivingOrder(boolean receivingOrder) {
        mReceivingOrder = receivingOrder;
    }

}
