package com.dudu.android.launcher.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.dudu.android.launcher.utils.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import wld.btphone.bluetooth.aidl.PbapService;

/**
 * Created by Administrator on 2016/1/20.
 */
public class BluetoothService extends Service {

    private BluetoothAdapter mAdapter;

    private PbapService mPbapService;

    private List<String> mDevices;

    private Logger logger;

    @Override
    public void onCreate() {
        super.onCreate();
        logger = LoggerFactory.getLogger("phone.BluetoothService");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BLUETOOTH_SHOW_CONNECT_FAIL);
        intentFilter.addAction(Constants.BLUETOOTH_SHOW_WAITDIALOG);
        intentFilter.addAction(Constants.BLUETOOTH_PULL_PHONE_BOOK);
        intentFilter.addAction(Constants.BLUETOOTH_ACL_DISCONNECTED);
        intentFilter.addAction(Constants.ACTION_NEW_BLUETOOTH_DEVICE);
        intentFilter.addAction(Constants.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(mBluetoothPhoneReceiver, intentFilter);

        initService();
    }

    private void initService() {
        mDevices = new ArrayList<>();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> bondedDevices = mAdapter.getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {
            logger.debug("已配对的设备： " + device.getAddress());
            mDevices.add(device.getAddress());
        }

        Intent intent = new Intent("wld.btphone.bluetooth.ProfileService");
//        bindService(intent, mPbapServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ServiceConnection mPbapServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPbapService = PbapService.Stub.asInterface(service);
            logger.debug("连接蓝牙电话底层服务成功...");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPbapService = null;
        }
    };

    private BroadcastReceiver mBluetoothPhoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            logger.debug("接收到蓝牙连接状态改变广播：" + action);

            if (action.equals(Constants.BLUETOOTH_SHOW_CONNECT_FAIL)) {

            } else if (action.equals(Constants.BLUETOOTH_SHOW_WAITDIALOG)) {

            } else if (action.equals(Constants.BLUETOOTH_ACL_DISCONNECTED)) {

            } else if (action.equals(Constants.BLUETOOTH_PULL_PHONE_BOOK)) {

            } else if (action.equals(Constants.ACTION_CONNECTION_STATE_CHANGED)) {
                int prevState = intent.getIntExtra(Constants.EXTRA_PREVIOUS_STATE, 0);
                int state = intent.getIntExtra(Constants.EXTRA_STATE, 0);
                logger.debug("prevState: " + prevState + " state: " + state);
                if (state == BluetoothProfile.STATE_CONNECTED) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        logger.debug("device name: " + device.getName() + " device address: " +
                                device.getAddress() + " device type: " + device.getType());
                        setDevice(device.getAddress());

                        logger.debug("开始获取蓝牙通讯录...");
                        pullPhoneBook();
                    }
                }
            }
        }
    };

    private void setDevice(String address) {
        try {
            mPbapService.setDeviceStub(address);
        } catch (RemoteException e) {
            logger.error("蓝牙通讯录设置设备失败...");
        }
    }

    private void pullPhoneBook() {
        try {
            mPbapService.PullphonebookStub();
        } catch (RemoteException e) {
            logger.error("蓝牙获取通讯录失败...");
        }
    }

}
