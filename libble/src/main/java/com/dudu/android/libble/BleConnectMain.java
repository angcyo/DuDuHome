package com.dudu.android.libble;

import android.content.Context;

import org.scf4a.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;

public class BleConnectMain {

    private static BleConnectMain ourInstance = new BleConnectMain();
    private BleScanner mBleScanner;
    private BleManager bleManager;
    private Logger log;

    private BleConnectMain() {
        log = LoggerFactory.getLogger("ble.main");
    }

    public static BleConnectMain getInstance() {
        return ourInstance;
    }

    public void onEventMainThread(Event.StartScanner event) {
        if (null == mBleScanner) {
            mBleScanner = new BleScanner();
        }
        log.debug("Start BLE Scanner");
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
        EventBus.getDefault().unregister(ourInstance);
        EventBus.getDefault().register(ourInstance);
        if (null == bleManager) {
            bleManager = new BleManager(context);
        }
    }

    public void uninit() {
        EventBus.getDefault().unregister(ourInstance);
    }

}
