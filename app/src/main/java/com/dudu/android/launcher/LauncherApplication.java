package com.dudu.android.launcher;

import android.app.Application;
import android.os.StrictMode;

import com.dudu.android.launcher.exception.CrashHandler;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.NetworkUtils;
import com.dudu.commonlib.CommonLib;

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
    }

}
