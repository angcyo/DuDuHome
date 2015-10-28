package com.dudu.android.libble;

import android.content.Context;

import org.scf4a.Event;

import de.greenrobot.event.EventBus;

public class BleConnectMain {

    private static BleConnectMain ourInstance = new BleConnectMain();
    private BleScanner mBleScanner;
    private BleManager bleManager;

    private BleConnectMain() {
    }

    public static BleConnectMain getInstance() {
        return ourInstance;
    }

    public void onEventMainThread(Event.StartScanner event) {
        if (null == mBleScanner) {
            mBleScanner = new BleScanner();
        }
        mBleScanner.startScan();
    }

    public void onEventMainThread(Event.StopScanner event) {
        mBleScanner.stopScan();
    }

    public void onEventMainThread(Event.Connect event) {
        if (event.getType() == Event.ConnectType.BLE) {
            bleManager.setAuth(event.isAuth());
            if (!bleManager.connect(event.getMac())) {
                EventBus.getDefault().post(new Event.Disconnected(Event.ErrorCode.ConnectInvokeFail));
            }
        }
    }

    public void onEventMainThread(Event.DisConnect event) {
        if (event.getType() == Event.ConnectType.BLE) {
            bleManager.disconnect();
        }
    }

    public void init(Context context) {
        EventBus.getDefault().register(ourInstance);
        if (null == bleManager) {
            bleManager = new BleManager(context);
        }
    }

    public void uninit() {
        EventBus.getDefault().unregister(ourInstance);
    }

}
