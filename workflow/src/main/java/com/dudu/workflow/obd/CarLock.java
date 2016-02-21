package com.dudu.workflow.obd;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

import com.dudu.android.libble.BleConnectMain;

import org.scf4a.ConnSession;
import org.scf4a.Event;
import org.scf4a.EventWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;

public class CarLock {
    private static Logger log = LoggerFactory.getLogger("CarLock");

    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mWriteChara;
    private BluetoothDevice mBluetoothDevice;

    public void init(Context context) {
        ConnSession.getInstance();
        BleConnectMain.getInstance().init(context);
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new Event.StartScanner(Event.ConnectType.BLE));
    }

    public void uninit(Context context) {
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(Event.BackScanResult event) {
        if (event.getType() != Event.ConnectType.BLE)
            return;
        BluetoothDevice device = event.getDevice();
        log.debug("ble try Connect {}[{}]", device.getName(), device.getAddress());
        EventBus.getDefault().post(new Event.Connect(device.getAddress(), Event.ConnectType.BLE, false));
    }


    public void onEvent(Event.BLEInit event) {
        log.debug("ble BLEInit");
        mBluetoothGatt = event.getBluetoothGatt();
        mWriteChara = event.getWriteChara();

        mBluetoothDevice = event.getDevice();
        final String devAddr = mBluetoothDevice.getAddress();
    }

    public static void lockCar() {
        log.debug("lockCar");
        byte[] bytes = {0x12, 0x34, 0x56, 0x78};
        EventBus.getDefault().post(new EventWrite.Data2Write(bytes, EventWrite.TYPE.Data));
    }

    public static void unlockCar() {
        log.debug("unlockCar");
        byte[] bytes = {0x23, 0x45, 0x67, (byte) 0x89};
        EventBus.getDefault().post(new EventWrite.Data2Write(bytes, EventWrite.TYPE.Data));
    }
}
