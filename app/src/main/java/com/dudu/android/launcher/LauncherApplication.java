package com.dudu.android.launcher;

import android.app.Application;
import android.os.StrictMode;

import com.dudu.android.launcher.exception.CrashHandler;
import com.dudu.android.launcher.service.RecordBindService;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.NetworkUtils;
import com.dudu.commonlib.CommonLib;
import com.dudu.init.InitManager;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

public class LauncherApplication extends Application {

    public static LauncherApplication mApplication;

    private boolean mReceivingOrder = false;

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

        CommonLib.getInstance().init(this);

        if (Constants.DEBUG_STRICT_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyDialog()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }

        CrashHandler crashHandler = CrashHandler.getInstance();

        // 注册crashHandler
        crashHandler.init(getApplicationContext());

        NetworkUtils.writePortalConfig(this);

        StringBuffer param = new StringBuffer();
        param.append("appid=" + Constants.XUFEIID);
        param.append(",");
        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        SpeechUtility.createUtility(this, param.toString());

        Setting.showLogcat(false);
    }

    public boolean isReceivingOrder() {
        return mReceivingOrder;
    }

    public void setReceivingOrder(boolean receivingOrder) {
        mReceivingOrder = receivingOrder;
    }

}
