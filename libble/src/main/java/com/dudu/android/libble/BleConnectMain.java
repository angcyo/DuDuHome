package com.dudu.android.libble;

import android.bluetooth.BluetoothProfile;
import android.content.Context;

import org.scf4a.Event;
import org.scf4a.EventWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.functions.Action1;

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
        if (event.getType() != Event.ConnectType.BLE) return;
        if (null == mBleScanner) {
            mBleScanner = new BleScanner();
        }
        log.debug("Start BLE Scanner");
        mBleScanner.startScan();

        Observable.timer(BleScanner.SCAN_PERIOD, TimeUnit.MINUTES)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (bleManager.getConnectionState() != BluetoothProfile.STATE_CONNECTED) {
                            log.debug("BLE scan time out");
                            mBleScanner.stopScan();
                            EventBus.getDefault().post(new Event.Disconnected(Event.ErrorCode.ScanInvokeFail));
                        }
                    }
                });

    }

    public boolean isBleConnected(){
        if(bleManager!=null) {
            return bleManager.getConnectionState() == BluetoothProfile.STATE_CONNECTED;
        }
        return false;
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

    public void onEvent(EventWrite.Data2Write event) {
        bleManager.writeData(event.data);
    }
}
