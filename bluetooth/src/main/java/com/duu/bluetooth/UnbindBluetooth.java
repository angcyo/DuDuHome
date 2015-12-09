package com.duu.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by lxh on 2015/12/9.
 */
public class UnbindBluetooth {

    public static void unbind() {
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bindDevices = mAdapter.getBondedDevices();
        if (bindDevices.size() > 0) {
            Iterator<BluetoothDevice> iterator = bindDevices.iterator();
            while (iterator.hasNext()) {
                try {
                    BluetoothDevice bluetoothDevice = iterator.next();
                    ClsUtils.removeBond(bluetoothDevice.getClass(),bluetoothDevice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
