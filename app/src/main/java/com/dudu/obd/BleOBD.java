package com.dudu.obd;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

import com.dudu.android.libble.BleConnectMain;
import com.dudu.event.BleStateChange;

import org.scf4a.ConnSession;
import org.scf4a.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.functions.Action1;

public class BleOBD {
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mWriteChara;
    private BluetoothDevice mBluetoothDevice;
    private Logger log;
    private boolean isConnected = false;
    private PrefixReadL1 readL1;

    public BleOBD() {

        log = LoggerFactory.getLogger("ble.odb.old");
        readL1 = new PrefixReadL1();
    }

    public void initOBD(Context context) {
        log.debug("initOBD");
        ConnSession.getInstance();
        BleConnectMain.getInstance().init(context);
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        EventBus.getDefault().unregister(readL1);
        EventBus.getDefault().register(readL1);

    }

    public void uninitOBD() {
        log.debug("initOBD");
        ConnSession.getInstance().uninit();
        BleConnectMain.getInstance().uninit();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().unregister(readL1);
    }

    public void onEvent(Event.BackScanResult event) {
        BluetoothDevice device = event.getDevice();
        log.debug("Try Connect {}[{}]", device.getName(), device.getAddress());
        EventBus.getDefault().post(new Event.Connect(device.getAddress(), Event.ConnectType.BLE, false));
    }


    public void onEvent(Event.BLEInit event) {
        log.debug("ble BLEInit");
        mBluetoothGatt = event.getBluetoothGatt();
        mWriteChara = event.getWriteChara();

        mBluetoothDevice = event.getDevice();
        final String devAddr = mBluetoothDevice.getAddress();
    }

    public void onEvent(Event.Disconnected event) {
        log.debug("ble Disconnected");
        isConnected = false;
        EventBus.getDefault().post(new BleStateChange(BleStateChange.BLEDISCONNECTED));
        Observable.timer(1, TimeUnit.MINUTES)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (!isConnected)
                            EventBus.getDefault().post(new Event.StartScanner(Event.ConnectType.BLE));
                    }
                });

    }

    public void onEvent(Event.BTConnected event) {
        log.debug("ble BTConnected");
        isConnected = true;
        EventBus.getDefault().post(new BleStateChange(BleStateChange.BLECONNECTED));
    }
}
