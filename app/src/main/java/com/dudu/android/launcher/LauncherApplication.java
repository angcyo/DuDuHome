package com.dudu.android.launcher;

import android.app.Application;
import android.content.Intent;

import com.dudu.android.launcher.exception.CrashHandler;
import com.dudu.android.launcher.service.NewMessageShowService;
import com.dudu.android.launcher.service.RecordBindService;
import com.dudu.android.launcher.ui.dialog.ErrorMessageDialog;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.NetworkUtils;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LauncherApplication extends Application {

    public static LauncherApplication mApplication;

    private boolean mReceivingOrder = false;

    private RecordBindService mRecordService;

    private Logger logger;

    public static LauncherApplication getContext() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        logger = LoggerFactory.getLogger("init.application");
        logger.debug("正在初始化application");

        Setting.showLogcat(false);

        CrashHandler crashHandler = CrashHandler.getInstance();

        // 注册crashHandler
        crashHandler.init(getApplicationContext());

        //将htdocs压缩包解压
        NetworkUtils.writePortalConfig(this);
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
