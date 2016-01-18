package com.dudu.android.launcher.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.dudu.android.hideapi.SystemPropertiesProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

public class WifiApAdmin {
    public static final String KEY_WIFI_AP_STATE = "wifi_ap_state";
    public static final String KEY_WIFI_AP_SSID = "wifi_ssid";
    public static final String KEY_WIFI_AP_PASSWORD = "wifi_password";

    public static final String DEFAULT_SSID = "DUDU";
    public static final String DEFAULT_PASSWORD = "88888888";

    private static final String WIFI_CONF_DIRECTORY = "nodogsplash";
    private static final String WIFI_CONF_NAME = "nodogsplash.conf";

    private static WifiManager mWifiManager = null;
    private static Logger log_init = LoggerFactory.getLogger("init.wifi.ap");

    /**
     * 设置WIFI热点密码
     *
     * @param password
     */
    public static boolean setWifiApPassword(Context context, String password) {
        log_init.debug("热点:设置密码");
        SharedPreferencesUtil.putStringValue(context, KEY_WIFI_AP_PASSWORD, password);
        return startWifiAp(context, SharedPreferencesUtil.getStringValue(context,
                KEY_WIFI_AP_SSID, DEFAULT_SSID), password, null);
    }

    private static boolean isWifiConfiged(Context context) {
        File dir = new File(FileUtils.getExternalStorageDirectory(), WIFI_CONF_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, WIFI_CONF_NAME);
        if (!file.exists()) {
            log_init.debug("热点配置文件不存在");
            return false;
        }

        log_init.debug("热点配置文件存在");
        return true;
    }

    private static boolean initWifiConfig(Context context) {
        File dir = new File(FileUtils.getExternalStorageDirectory(), WIFI_CONF_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        log_init.debug("创建热点配置文件");
        File file = new File(dir, WIFI_CONF_NAME);
        if (!file.exists()) {
            try {
                file.createNewFile();

                InputStream isAsset = context.getAssets().open(WIFI_CONF_NAME);

                FileUtils.copyFileToSd(isAsset, file);
            } catch (IOException e) {
                log_init.debug("创建热点配置文件：失败", e);
                return false;
            }
        }

        return true;
    }

    public static boolean initWifiApState(final Context context) {
        log_init.debug("热点初始化");
        if (isWifiConfiged(context)) {
            //如果开机已经打开ap，则不再打开
            if (isWifiApEnabled(context)) {
                log_init.debug("热点已经打开");
                startPortal(context);
                return true;
            }
        } else {
            initWifiConfig(context);
            if (isWifiApEnabled(context)) {
                log_init.debug("热点已经打开，需要先关闭");
                closeWifiAp(context);
            }
        }

        if (mWifiManager.isWifiEnabled()) {
            log_init.debug("2s后关闭wifi");
            rx.Observable.timer(2, TimeUnit.SECONDS)
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(final Long aLong) {
                            mWifiManager.setWifiEnabled(false);
                        }
                    });
        }
        log_init.debug("7s后打开热点");
        rx.Observable.timer(7, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(final Long aLong) {
                        if (Utils.isTaxiVersion()) {
                            startWifiAp(context, DEFAULT_SSID, "", null);
                        } else {
                            if (SharedPreferencesUtil.getBooleanValue(context, KEY_WIFI_AP_STATE, false)) {
                                String ssid = SharedPreferencesUtil.getStringValue(context, KEY_WIFI_AP_SSID, DEFAULT_SSID);
                                String password = SharedPreferencesUtil.getStringValue(context, KEY_WIFI_AP_PASSWORD, DEFAULT_PASSWORD);
                                startWifiAp(context, ssid, password, null);
                            }
                        }
                    }
                });

        return true;
    }

    public static void startPortal(final Context context) {
        log_init.debug("打开Portal服务");
        SystemPropertiesProxy.getInstance().set(context, "persist.sys.nodog", "start");
        rx.Observable.timer(10, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(final Long aLong) {
                        //TODO 检测protal是否启动成功
                    }
                });
    }

    public static boolean startWifiAp(Context context) {
        log_init.debug("读取配置，然后打开热点");
        if (Utils.isTaxiVersion()) {
            return startWifiAp(context, DEFAULT_SSID, "", null);
        } else {
            String ssid = SharedPreferencesUtil.getStringValue(context, KEY_WIFI_AP_SSID, DEFAULT_SSID);
            String password = SharedPreferencesUtil.getStringValue(context, KEY_WIFI_AP_PASSWORD, DEFAULT_PASSWORD);
            return startWifiAp(context, ssid, password, null);
        }
    }

    public static boolean startWifiAp(final Context context, String ssid,
                                      String password, WifiSettingStateCallback callback) {
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);

        if (!startWifiAp(ssid, password)) {
            log_init.error("打开热点调用失败，退出");
            return false;
        }

        log_init.debug("准备打开Portal");
        rx.Observable.timer(7, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(final Long aLong) {
                        if (isWifiApEnabled(context)) {
                            log_init.debug("打开热点成功，保存状态");
                            SharedPreferencesUtil.putBooleanValue(context, KEY_WIFI_AP_STATE, true);

                            startPortal(context);
                        } else {
                            log_init.error("打开热点失败，Portal也没启动，退出");
                        }
                    }
                });

        if (callback != null) {
            callback.onWifiStateChanged(true);
        }

        return true;
    }

    private static boolean startWifiAp(String ssid, String password) {
        log_init.debug("反射打开热点{}", ssid);
        Method method;
        try {
            method = mWifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            WifiConfiguration netConfig = new WifiConfiguration();
            netConfig.SSID = ssid;
            if (password == null || (password != null && password.isEmpty())) {
                log_init.debug("没有密码，清除掉配置");
                netConfig.allowedAuthAlgorithms.clear();
                netConfig.allowedProtocols.clear();
                netConfig.allowedKeyManagement.clear();
                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                netConfig.allowedPairwiseCiphers.clear();
                netConfig.allowedGroupCiphers.clear();

                netConfig.wepKeys[0] = "";
                netConfig.wepTxKeyIndex = 0;
            } else {
                log_init.debug("有密码，设置配置");
                netConfig.preSharedKey = password;
                netConfig.allowedAuthAlgorithms
                        .set(WifiConfiguration.AuthAlgorithm.OPEN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                netConfig.allowedKeyManagement
                        .set(WifiConfiguration.KeyMgmt.WPA_PSK);
                netConfig.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.CCMP);
                netConfig.allowedPairwiseCiphers
                        .set(WifiConfiguration.PairwiseCipher.TKIP);
                netConfig.allowedGroupCiphers
                        .set(WifiConfiguration.GroupCipher.CCMP);
                netConfig.allowedGroupCiphers
                        .set(WifiConfiguration.GroupCipher.TKIP);
            }

            return (Boolean) method.invoke(mWifiManager, netConfig, true);
        } catch (Exception e) {
            log_init.debug("反射打开热点异常", e);
        }

        return false;
    }

    public static void closeWifiAp(Context context) {
        log_init.debug("反射关闭热点");
        if (isWifiApEnabled(context)) {
            mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            Method setWifiApEnabled;
            try {
                setWifiApEnabled = mWifiManager.getClass().getMethod(
                        "setWifiApEnabled", WifiConfiguration.class, boolean.class);
                setWifiApEnabled.invoke(mWifiManager, null, false);
                SharedPreferencesUtil.putBooleanValue(context, KEY_WIFI_AP_STATE, false);
            } catch (Exception e) {
                log_init.debug("反射关闭热点异常", e);
            }
        }
    }

    public static boolean isWifiApEnabled(Context context) {
        log_init.debug("反射查询热点");
        try {
            if (mWifiManager == null) {
                mWifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
            }
            Method method = mWifiManager.getClass()
                    .getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);
        } catch (Exception e) {
            log_init.debug("反射查询热点异常", e);
        }

        return false;
    }

    public static void setMobileDataStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    }


    /**
     * wifi 密码设置后的回调
     */
    public interface WifiSettingStateCallback {
        void onWifiStateChanged(boolean open);
    }

}
