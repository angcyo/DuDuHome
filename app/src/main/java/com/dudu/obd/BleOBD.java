package com.dudu.obd;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.libble.BleConnectMain;

import org.scf4a.ConnSession;
import org.scf4a.Event;
import org.scf4a.EventRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

import de.greenrobot.event.EventBus;

public class BleOBD {
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mWriteChara;
    private BluetoothDevice mBluetoothDevice;
    private PrefixReadL1 readL1;
    private Logger log;

    public BleOBD() {
        readL1 = new PrefixReadL1();
        log = LoggerFactory.getLogger("odb.ble");
    }

    public void initOBD() {
        ConnSession.getInstance();
        BleConnectMain.getInstance().init(LauncherApplication.getContext());
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        EventBus.getDefault().unregister(readL1);
        EventBus.getDefault().register(readL1);
        EventBus.getDefault().post(new Event.StartScanner());
//		superOBD.init();
    }

    public void onEventMainThread(Event.BackScanResult event) {
        BluetoothDevice device = event.getDevice();
        log.debug("Try Connect {}[{}]", device.getName(), device.getAddress());
        EventBus.getDefault().post(new Event.Connect(device.getAddress(), Event.ConnectType.BLE, false));
    }


    public void onEventMainThread(Event.BLEInit event) {
        mBluetoothGatt = event.getBluetoothGatt();
        mWriteChara = event.getWriteChara();

        mBluetoothDevice = event.getDevice();
        final String devAddr = mBluetoothDevice.getAddress();
    }

    public void onEventBackgroundThread(EventRead.L1ReadDone event) {
        final byte[] data = event.getData();

        try {
            log.debug("Receive OBD Data: = {}", new String(data, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
