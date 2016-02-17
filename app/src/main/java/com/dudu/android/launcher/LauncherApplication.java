package com.dudu.android.launcher;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.dudu.android.launcher.exception.CrashHandler;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.NetworkUtils;
import com.dudu.commonlib.CommonLib;
import com.dudu.drivevideo.DriveVideo;
import com.dudu.video.VideoManager;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LauncherApplication extends Application {

    public static LauncherApplication mApplication;

    private Logger logger;

    public static LauncherApplication getContext() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        MultiDex.install(this);

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
        // 开启logcat输出，方便debug，发布时请关闭
        XGPushConfig.enableDebug(this, true);
        Context context = getApplicationContext();
        XGPushManager.registerPush(context, "13800138000", new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                logger.debug("注册成功");
            }

            @Override
            public void onFail(Object o, int i, String s) {
                logger.debug("注册失败" + s);
            }
        });

    }

}
