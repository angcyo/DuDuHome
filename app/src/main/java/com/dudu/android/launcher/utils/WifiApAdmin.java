package com.dudu.android.launcher.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiApAdmin {

    public static final String TAG = "WifiApAdmin";

    private static final int WIFI_AP_STATE_ENABLED = 3;
    private static final int WIFI_AP_STATE_FAILED = 4;
    private static String mSSID = "DuduHotSpot";
    private static String mPasswd = "88888888";
    private static final String WIFISSID = "wifi_ssid";
    private static final String WIFIPASSWORD = "wifi_password";

    private static WifiManager mWifiManager = null;

    private Context mContext = null;

    private WifiApAdmin(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);

    }

    private static WifiSettingStateCallback mWifiCallBack;

    /**
     * 设置WIFI热点密码
     *
     * @param password
     */
    public static boolean setWifiSharedKey(Context context, String password) {
        mPasswd = password;
        return startWifiAp(context, "", password,null);
    }

    /**
     * 设置WIFI热点名称
     *
     * @param ssid
     */
    public static boolean setWifiSSID(Context context, String ssid) {
        mSSID = ssid;
        return startWifiAp(context, ssid, WIFIPASSWORD, null);
    }

    public static boolean startWifiAp(final Context context) {
        String ssid = SharedPreferencesUtil.getPreferences(context,WIFISSID,"DuDuSpot");
        String password = SharedPreferencesUtil.getPreferences(context,WIFIPASSWORD,"88888888");
        if (Util.isTaxiVersion()) {
            SharedPreferencesUtil.savePreferences(context, WIFISSID, mSSID);
            SharedPreferencesUtil.savePreferences(context, WIFIPASSWORD, "");
        } else {
            SharedPreferencesUtil.savePreferences(context, WIFISSID, mSSID);
            SharedPreferencesUtil.savePreferences(context, WIFIPASSWORD, password);
        }
        return startWifiAp(context,ssid,password,null);
    }

    /**
     * @param context
     * @param ssid
     * @param passwd
     * @return
     */
    public static boolean startWifiAp(final Context context, String ssid,
                                      String passwd, WifiSettingStateCallback callback) {
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);

        mSSID = ssid;
        mPasswd = passwd;

        closeWifiAp(context);

        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }

        if (!doStratWifiAp(context))
            return false;

        // 保存wifi热点SSID和password
        SharedPreferencesUtil.savePreferences(context, WIFISSID, ssid);
        SharedPreferencesUtil.savePreferences(context, WIFIPASSWORD, passwd);

        MyTimerCheck timerCheck = new MyTimerCheck() {

            @Override
            public void doTimerCheckWork() {

                if (isWifiApEnabled(context)) {
                    Log.v(TAG, "Wifi enabled success!");
                    this.exit();
                } else {
                    Log.v(TAG, "Wifi enabled failed!");
                }
            }

            @Override
            public void doTimeOutWork() {
                this.exit();
            }
        };

        timerCheck.start(15, 1000);
        if (callback != null) {
            mWifiCallBack = callback;
            mWifiCallBack.onWifiStateChanged(true);
        }

        return true;
    }

    private static boolean doStratWifiAp(final Context context) {
        Method method1 = null;
        mSSID = SharedPreferencesUtil.getPreferences(context, WIFISSID, "DuduHotSpot");
        mPasswd = SharedPreferencesUtil.getPreferences(context, WIFIPASSWORD, "88888888");
        try {
            method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            WifiConfiguration netConfig = new WifiConfiguration();
            netConfig.SSID = mSSID;

            if (mPasswd.isEmpty()) {
                netConfig.allowedAuthAlgorithms.clear();
                netConfig.allowedGroupCiphers.clear();
                netConfig.allowedKeyManagement.clear();
                netConfig.allowedPairwiseCiphers.clear();
                netConfig.allowedProtocols.clear();
                netConfig.wepKeys[0] = "";
                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                netConfig.wepTxKeyIndex = 0;
            } else {
                netConfig.preSharedKey = mPasswd;
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
            return (Boolean) method1.invoke(mWifiManager, netConfig, true);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean closeWifiAp(Context context) {

        if (isWifiApEnabled(context)) {
            System.out.println("------close wifiAp");
            if (mWifiManager == null) {
                mWifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
            }

            try {
                Method method = mWifiManager.getClass().getMethod(
                        "getWifiApConfiguration");
                method.setAccessible(true);

                WifiConfiguration config = (WifiConfiguration) method
                        .invoke(mWifiManager);

                Method method2 = mWifiManager.getClass().getMethod(
                        "setWifiApEnabled", WifiConfiguration.class,
                        boolean.class);
                return (Boolean) method2.invoke(mWifiManager, config, false);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isWifiApEnabled(Context context) {
        try {
            if (mWifiManager == null) {
                mWifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
            }
            Method method = mWifiManager.getClass()
                    .getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);

        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * WIFI热点是否处于打开
     *
     * @return
     */
    public static boolean isWifiApOpen(Context context) {
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
        }
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApState");
            int state = (Integer) method.invoke(mWifiManager);
            return state == WIFI_AP_STATE_ENABLED;
        } catch (Exception e) {
            return false;
        }
    }

    public static int getWifiApState(Context context, WifiManager wifiManager) {
        if (wifiManager == null) {
            wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
        }
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            int i = (Integer) method.invoke(wifiManager);
            System.out.println("-------------wifi state:" + i);
            return i;
        } catch (Exception e) {
            return WIFI_AP_STATE_FAILED;
        }
    }

    /**
     * wifi 密码设置后的回调
     */
    public interface WifiSettingStateCallback {
        void onWifiStateChanged(boolean open);
    }

}
