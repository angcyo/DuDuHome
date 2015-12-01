package com.duu.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.scf4a.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import de.greenrobot.event.EventBus;

/**
 * Spp 蓝牙扫描
 *
 */
public class SppScanner {

    private BluetoothAdapter mBtAdapter;

    private Context mContext;

    public static boolean broadRegister = false;

    private Logger log;

    private String name;

    public SppScanner(Context context) {
        this.mContext = context;
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        log = LoggerFactory.getLogger("ble.scan");
        this.name = "OBDII";
    }

    /**
     * 蓝牙扫描时的广播接收器
     */
    private BroadcastReceiver BTDiscoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice btDevice = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (btDevice != null) {
                    //把蓝牙设备添加到list集合，重复的不添加
                    if(btDevice.getName()!=null&&btDevice.getName().equals(name)){
                        EventBus.getDefault().post(new Event.BackScanResult(btDevice));
                    }
                }
            }
        }
    };

    /**
     * 注册广播，并扫描附近的设备
     */
    public void initScan() {
        IntentFilter btDiscoveryFilter = new IntentFilter();
        btDiscoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(BTDiscoveryReceiver, btDiscoveryFilter);
        startScan();
    }

    /**
     * 搜索设备
     */
    public void startScan() {
        log.debug("startScan");
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        mBtAdapter.startDiscovery();
    }

    /**
     * 停止扫描
     */
    public void stopScan() {
        log.debug("stopScan");
        mContext.unregisterReceiver(BTDiscoveryReceiver);
    }

}
