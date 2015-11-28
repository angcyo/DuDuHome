package com.dudu.init;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.service.FloatBackButtonService;
import com.dudu.android.launcher.service.MonitorService;
import com.dudu.android.launcher.service.NewMessageShowService;
import com.dudu.android.launcher.utils.Utils;
import com.dudu.android.launcher.utils.WifiApAdmin;
import com.dudu.navi.NavigationManager;
import com.dudu.obd.OBDDataService;
import com.dudu.voice.semantic.VoiceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.android.SystemPropertiesProxy;

/**
 * Created by 赵圣琪 on 2015/11/24.
 */
public class InitManager {

    private static InitManager mInstance;

    private Activity mActivity;

    private Logger logger;

    private int log_step = 0;

    private InitManager(Activity activity) {
        logger = LoggerFactory.getLogger("init.manager");
        mActivity = activity;
    }

    public static InitManager getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new InitManager(activity);
        }

        return mInstance;
    }

    public void init() {
        if (Utils.isDemoVersion(mActivity)) {
            initAfterBTFT();
            return;
        }

        checkBTFT();
    }

    /**
     * 开启语音对话框服务
     */
    private void startFloatMessageService() {
        Intent intent = new Intent(mActivity, NewMessageShowService.class);
        mActivity.startService(intent);

        /**
         * 悬浮按钮服务
         */
        Intent i = new Intent(mActivity, FloatBackButtonService.class);
        mActivity.startService(i);
    }

    /**
     * 开启OBD服务
     */
    private void startOBDService() {
        com.dudu.android.hideapi.SystemPropertiesProxy.getInstance().set(mActivity,
                "persist.sys.gps", "start");
        Intent intent = new Intent(mActivity, OBDDataService.class);
        mActivity.startService(intent);
    }

    private void startMonitorService() {
        Intent intent = new Intent(mActivity, MonitorService.class);
        mActivity.startService(intent);
    }

    /**
     * 打开蓝牙
     */
    private void openBlueTooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) mActivity.getSystemService(
                Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
        }
    }

    /**
     * 工厂检测
     */
    private void checkBTFT() {
        SystemPropertiesProxy sps = SystemPropertiesProxy.getInstance();
        boolean need_bt = !"1".equals(sps.get("persist.sys.bt", "0"));
        boolean need_ft = !"1".equals(sps.get("persist.sys.ft", "0"));
        Intent intent;
        PackageManager packageManager = mActivity.getPackageManager();
        intent = packageManager.getLaunchIntentForPackage("com.qualcomm.factory");
        if ((need_bt || need_ft) && intent != null) {
            //close wifi ap for ft test
            WifiApAdmin.closeWifiAp(mActivity);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mActivity.startActivity(intent);
        } else {
            // 关闭ADB调试端口
            if (!Utils.isDemoVersion(mActivity)) {
                com.dudu.android.hideapi.SystemPropertiesProxy.getInstance().set(mActivity,
                        "persist.sys.usb.config", "charging");
            }

            initAfterBTFT();
        }
    }

    private void initAfterBTFT() {

        logger.debug("[init][{}]开启语音监听", log_step++);
        VoiceManager.getInstance().startWakeup();

        logger.debug("[init][{}]打开蓝牙", log_step++);
        openBlueTooth();

        logger.debug("[init][{}]启动监听服务", log_step++);
        startMonitorService();

        logger.debug("[init][{}]启动语音悬浮框服务", log_step++);
        startFloatMessageService();

        logger.debug("[init][{}]启动OBD服务", log_step++);
        startOBDService();


        logger.debug("[init][{}]检查sim卡状态", log_step++);
        Utils.checkSimCardState(mActivity);

        logger.debug("[init][{}]打开热点", log_step++);
        WifiApAdmin.initWifiApState(mActivity);


        NavigationManager.getInstance(LauncherApplication.getContext()).initNaviManager();
    }

}
