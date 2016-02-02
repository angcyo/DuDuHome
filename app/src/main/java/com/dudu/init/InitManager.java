package com.dudu.init;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.service.BluetoothService;
import com.dudu.android.launcher.service.CheckUserService;
import com.dudu.android.launcher.service.FloatBackButtonService;
import com.dudu.android.launcher.service.MainService;
import com.dudu.android.launcher.service.MonitorService;
import com.dudu.android.launcher.utils.AgedUtils;
import com.dudu.android.launcher.utils.CarStatusUtils;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.IPConfig;
import com.dudu.android.launcher.utils.SharedPreferencesUtils;
import com.dudu.android.launcher.utils.StatusBarManager;
import com.dudu.android.launcher.utils.Utils;
import com.dudu.android.launcher.utils.WifiApAdmin;
import com.dudu.event.DeviceEvent;
import com.dudu.navi.NavigationManager;
import com.dudu.video.VideoManager;
import com.dudu.voice.VoiceManagerProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import ch.qos.logback.core.android.SystemPropertiesProxy;
import de.greenrobot.event.EventBus;
import rx.functions.Action1;

/**
 * Created by 赵圣琪 on 2015/11/24.
 */
public class InitManager {

    private static InitManager mInstance;

    private Logger logger;

    private int log_step = 0;

    private boolean finished = false;

    private Context mContext;

    private HandlerThread mInitThread;

    private Handler mInitHandler;

    private InitManager() {
        logger = LoggerFactory.getLogger("init.manager");

        mContext = LauncherApplication.getContext();

        mInitThread = new HandlerThread("init thread");
        mInitThread.start();

        mInitHandler = new Handler(mInitThread.getLooper());
    }

    public static InitManager getInstance() {
        if (mInstance == null) {
            mInstance = new InitManager();
        }

        return mInstance;
    }

    public boolean init() {
        mInitHandler.post(new Runnable() {
            @Override
            public void run() {
                initOthers();
            }
        });

        return true;
    }

    public void unInit() {
        VoiceManagerProxy.getInstance().stopUnderstanding();
        VoiceManagerProxy.getInstance().stopSpeaking();

        mInitThread.quitSafely();
    }

    /**
     * 开启悬浮按钮服务
     */
    private void startFloatButtonService() {
        Intent i = new Intent(mContext, FloatBackButtonService.class);
        mContext.startService(i);
    }

    /**
     * 开启用户激活状态检查服务
     */
    private void startCheckUserService() {
        if (!SharedPreferencesUtils.getBooleanValue(mContext, Constants.KEY_USER_IS_ACTIVE, false)) {
            CheckUserService service = CheckUserService.getInstance(mContext);
            service.startService();
        }
    }

    /**
     * 开启OBD服务
     */
    private void startOBDService() {
        Intent intent = new Intent(mContext, MainService.class);
        mContext.startService(intent);
    }

    private void startMonitorService() {
        Intent intent = new Intent(mContext, MonitorService.class);
        mContext.startService(intent);
    }

    private void startBluetoothService() {
        Intent intent = new Intent(mContext, BluetoothService.class);
        mContext.startService(intent);
    }

    /**
     * 打开蓝牙
     */
    private void openBlueTooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(
                Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
        }
    }

    /**
     * 工厂检测,暂不做检测
     */
    private boolean checkBTFT() {
        SystemPropertiesProxy sps = SystemPropertiesProxy.getInstance();
        boolean need_bt = !"1".equals(sps.get("persist.sys.bt", "0"));
        boolean need_ft = !"1".equals(sps.get("persist.sys.ft", "0"));
        Intent intent;
        PackageManager packageManager = mContext.getPackageManager();
        intent = packageManager.getLaunchIntentForPackage("com.qualcomm.factory");
        if (intent != null) {
            //close wifi ap for ft test
            WifiApAdmin.closeWifiAp(mContext);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            return false;
        } else {
            initOthers();
            return true;
        }
    }

    private void initOthers() {
        // 关闭ADB调试端口
        if (!Utils.isDemoVersion(mContext)) {
            com.dudu.android.hideapi.SystemPropertiesProxy.getInstance().set(mContext,
                    "persist.sys.usb.config", "charging");
        }

        //卸载残留的老化软件
        AgedUtils.uninstallAgedApk(mContext);

        rx.Observable.timer(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(final Long aLong) {
                        logger.debug("[init][{}]打开热点", log_step++);
                        WifiApAdmin.initWifiApState(mContext);

                    }
                });
        rx.Observable.timer(10, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(final Long aLong) {
                        logger.debug("[init][{}]打开蓝牙", log_step++);
                        openBlueTooth();
                    }
                });
        rx.Observable.timer(15, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(final Long aLong) {
                        logger.debug("[init][{}]启动OBD服务", log_step++);
                        com.dudu.android.hideapi.SystemPropertiesProxy.getInstance().set(mContext,
                                "sys.gps", "start");
                        startOBDService();
                        finished = true;
                    }
                });

        logger.debug("[init][{}]启动监听服务", log_step++);
        startMonitorService();

        logger.debug("[init][{}]启动悬浮返回按钮服务", log_step++);
        startFloatButtonService();

        logger.debug("[init][{}]开启蓝牙电话服务", log_step++);
        startBluetoothService();

        logger.debug("[init][{}]打开用户激活状态检查", log_step++);
        startCheckUserService();

        logger.debug("[init][{}]检查sim卡状态", log_step++);
        Utils.checkSimCardState(mContext);

        FileUtils.clearVideoFolder();

        NavigationManager.getInstance(LauncherApplication.getContext()).initNaviManager();

        StatusBarManager.getInstance().registerSignalListener();

        screenOff();

        IPConfig.getInstance(mContext).init();

        //VideoManager.getInstance().init();

        VoiceManagerProxy.getInstance().onInit();
    }

    public boolean isFinished() {
        return finished;
    }

    private void screenOff() {
        if (!CarStatusUtils.isCarOnline()) {
            logger.debug("[init][{}] 熄火状态下关闭屏幕", log_step++);
            EventBus.getDefault().post(new DeviceEvent.Screen(DeviceEvent.OFF));
        }
    }

}
