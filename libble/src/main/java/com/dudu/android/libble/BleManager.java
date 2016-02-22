package com.dudu.android.libble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;

import org.scf4a.Event;
import org.scf4a.EventRSSI;
import org.scf4a.EventRead;
import org.scf4a.EventWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleManager {

    //报文元素定义
    public final static UUID[] UUIDS_ON_JDQ = {
            UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"),  // server
            UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"),  //write
            UUID.fromString("d44bc439-abfd-45a2-b575-925416129601"),  // notify
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"),  // description
    };

    public final static UUID[] UUIDS_ON_THREAD = {
            UUID.fromString("0000fff6-0000-1000-8000-00805f9b34fb"),  // server
            UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb"),  // write
            UUID.fromString("0000fffa-0000-1000-8000-00805f9b34fb"),  // notify
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"),  // description
    };

    /**
     * XFA
     */
    private static boolean JDQ_connect = false;   // 连接的是否为JDQ设备  默认为false
    /**
     * THREAD
     */
    private static boolean Thread_connect = false;   // 连接的是否为K200设备  默认为false
    private static BluetoothGattCharacteristic mWriteChara;
    private static BluetoothGattCharacteristic mNotifyChara;
    private static ArrayList<BluetoothGattCharacteristic> mNotifyCharaList = new ArrayList<BluetoothGattCharacteristic>();
    private final Logger log;
    private Context mContext;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private int mConnectionState = BluetoothProfile.STATE_DISCONNECTED;
    private int mNotifyCharaListIndex = 0;
    private String mMacAddress;
    private boolean mNotifyEnabled = false;
    private boolean isAuth;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            log.info("onConnectionStateChange status:{}, connect status:{}", status, newState);
            if (isUndocumentedErrorStatus(status)) {
                log.error("UndocumentedErrorStatus:{}", status);
                fixUndocumentedBleStatusProblem();
                return;
            }
            if (status != GATT_SUCCESS) return;

            mConnectionState = newState;
            if (newState == BluetoothProfile.STATE_CONNECTED && mBluetoothGatt != null) {
                log.info("BTConnected to GATT server.");
                mBluetoothGatt.discoverServices();
            } else /*if (newState == BluetoothProfile.STATE_DISCONNECTED)*/ {
                log.info("Disconnected from GATT server.");
                EventBus.getDefault().post(new Event.Disconnected(Event.ErrorCode.DeviceDisConnected));
                close();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                log.debug("onServicesDiscovered waiting for init complete");
                boolean isinitok = initCharacteristics();
                if (!isinitok) {
                    disconnect();
                }
            } else {
                log.error("onServicesDiscovered error: {}.", status);
                disconnect();
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            updateValueForNotification(mBluetoothGatt, characteristic);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            log.debug("onCharacteristicRead status:{}. ", status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            log.debug("onCharacteristicWrite status: {}", status);
            //失败不发送失败事件,避免导致重试并发,仅使用超时重试一条路径
            if (status == BluetoothGatt.GATT_SUCCESS) {
                EventBus.getDefault().post(new EventWrite.L0WriteDone());
            }
            // FIXME: 写入失败，也同样断开再连接
            else if (isUndocumentedErrorStatus(status)) {
                fixUndocumentedBleStatusProblem();
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            log.debug("onDescriptorWrite status: {}", status);
            if (status == GATT_SUCCESS) {
                //enable all notify
                setNextNotify(true);
            } else {
                disconnect();
            }
        }

        @Override
        public void onReadRemoteRssi(final BluetoothGatt gatt, final int rssi, final int status) {
            EventBus.getDefault().post(new EventRSSI(rssi));
        }
    };

    public BleManager(final Context context) {
        mContext = context;
        isAuth = false;
        log = LoggerFactory.getLogger("ble.mag");
    }

    public boolean connect(final String address) {
        this.mMacAddress = address;
        return reconnect();
    }

    private boolean reconnect() {
        logSysInfo();

        BluetoothManager btmag = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if (btmag == null) {
            return false;
        }
        mBluetoothAdapter = btmag.getAdapter();
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null || mMacAddress == null) {
            log.info("BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        //此处会有IllegalArgumentException
        try {
            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mMacAddress);
        } catch (IllegalArgumentException e) {
            log.error("address : " + mMacAddress + "is invalid", e);
            mBluetoothDevice = null;
        }
        if (mBluetoothDevice == null) {
            log.error("Device not found.  Unable to connect.");
            return false;
        }
        log.debug("Trying to create a new connection. address={}.", mMacAddress);
        mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, true, mGattCallback);
        int retry = 10;
        while (mBluetoothGatt == null && retry-- > 0) {
            log.debug("mBluetoothGatt == null, try again!");
            mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, true, mGattCallback);
        }

        if (mBluetoothGatt == null) {
            log.error("mBluetoothGatt == null");
        }

        mConnectionState = BluetoothProfile.STATE_CONNECTING;
        return true;
    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            log.error("BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public void close() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    public boolean writeData(byte[] data) {
        if (mBluetoothGatt == null) {
            log.error("BluetoothGatt not initialized !");
            return false;
        }

        if (data == null) {
            log.error("write data is null !");
            return false;
        }

        if (mWriteChara == null) {
            log.error("mWriteChara is null !");
            return false;
        }
//        EventBus.getDefault().post(new EventWrite.Data2Write(data, EventWrite.TYPE.Data));
//        return true;
        mWriteChara.setValue(data);
        return mBluetoothGatt.writeCharacteristic(mWriteChara);
    }

    public int getConnectionState() {
        return mConnectionState;
    }

    private void resetQppField() {
        mWriteChara = null;
        mNotifyChara = null;

        mNotifyCharaList.clear();
        mNotifyEnabled = false;
        mNotifyCharaListIndex = 0;
    }

    private boolean initCharacteristics() {
        resetQppField();
        BluetoothGattService gattService = null;

        int retry = 10;
        while (gattService == null && retry-- > 0) {
            for (BluetoothGattService service : mBluetoothGatt.getServices()) {
                log.debug("getServices = {}.", service.getUuid());
                if (service.getUuid().equals(UUIDS_ON_JDQ[0])) {
                    JDQ_connect = true;
                    gattService = service;
                    break;
                }
                if (service.getUuid().equals(UUIDS_ON_THREAD[0])) {
                    Thread_connect = true;
                    gattService = service;
                    break;
                }
            }

            if (gattService == null) {
                log.debug("gattService == null, try again!");
                refresh(mBluetoothGatt);
            }
        }

        if (gattService == null) {
            log.error("gattService == null");
            return false;
        }

        List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
        BluetoothGattCharacteristic chara;
        if (JDQ_connect) {      // 连接的是K100设备
            for (int i = 0; i < gattCharacteristics.size(); i++) {
                chara = gattCharacteristics.get(i);
                if (chara.getUuid().toString().equals(UUIDS_ON_JDQ[1].toString())) {
                    mWriteChara = chara;
                    mWriteChara.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                } else if (chara.getUuid().toString().equals(UUIDS_ON_JDQ[2].toString())) {
                    mNotifyChara = chara;
                    mNotifyCharaList.add(mNotifyChara);
                }
            }
        } else if (Thread_connect) {     // 连接的是K200设备
            for (int i = 0; i < gattCharacteristics.size(); i++) {
                chara = gattCharacteristics.get(i);
                if (chara.getUuid().toString().equals(UUIDS_ON_THREAD[1].toString())) {
                    mWriteChara = chara;
                    mWriteChara.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                } else if (chara.getUuid().toString().equals(UUIDS_ON_THREAD[2].toString())) {
                    mNotifyChara = chara;
                    mNotifyCharaList.add(mNotifyChara);
                }
            }
        }
        //enable notifications failed or init descriptor write operation failed
//        if (!setCharacteristicNotification(mNotifyCharaList.get(0), true)) return false;
//        mNotifyCharaListIndex++;

        EventBus.getDefault().post(new Event.BTConnected(mBluetoothDevice.getName(), mBluetoothDevice.getAddress(), Event.ConnectType.BLE));
        return true;
    }

    private boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        //Enable or disable notifications/indications for a given characteristic.
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        try {
            BluetoothGattDescriptor descriptor = null;
            if (JDQ_connect) {
                descriptor = characteristic.getDescriptor(UUIDS_ON_JDQ[3]);
            } else if (Thread_connect) {
                descriptor = characteristic.getDescriptor(UUIDS_ON_THREAD[3]);
            }
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                boolean initWriteDes = mBluetoothGatt.writeDescriptor(descriptor);
                if (!initWriteDes) {
                    log.error("descriptor init write operation failed");
                }
                return initWriteDes;
            } else {
                log.error("descriptor is null");
                return false;
            }
        } catch (IllegalArgumentException e) {
            log.error("descriptor is null", e);
        }
        return false;
    }

    private boolean setNextNotify(boolean enabled) {
        if (mNotifyCharaListIndex == mNotifyCharaList.size()) {
            mNotifyEnabled = true;
            EventBus.getDefault().post(new Event.BLEInit(mBluetoothDevice, mBluetoothGatt, mWriteChara));
            //添加验证之后，需要验证通过后才发出Connected事件，否则直接发出Connected事件
            if (isAuth) {
                EventBus.getDefault().post(new Event.BTConnected(mBluetoothDevice.getName(), mBluetoothDevice.getAddress(), Event.ConnectType.BLE));
            }
            return true;
        }
        return setCharacteristicNotification(mNotifyCharaList.get(mNotifyCharaListIndex++), enabled);
    }

    private void updateValueForNotification(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic characteristic) {
        if (bluetoothGatt == null || characteristic == null) {
            log.error("BluetoothGatt or BluetoothGattCharacteristic is null");
            return;
        }
        if (!mNotifyEnabled) {
            log.error("CharacteristicNotification not enabled");
            EventBus.getDefault().post(new Event.DisConnect(Event.ConnectType.BLE));
            return;
        }
        final byte[] data = characteristic.getValue();
        if (data == null) return;
        if (data.length == 0) return;
        try {
            log.trace("ble receive Data = \n{}", new String(data, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.warn("data parse exception", e);
        }
        EventBus.getDefault().post(new EventRead.L0ReadDone(data));
    }

    private boolean isUndocumentedErrorStatus(int status) {
        return status == 133 || status == 137 || status == 257;
    }

    private void fixUndocumentedBleStatusProblem() {
        disconnect();
//        reconnect();
    }

    private boolean refresh(BluetoothGatt gatt) {
        try {
            Method localMethod = gatt.getClass().getMethod("refresh");
            if (localMethod != null) {
                return (Boolean) localMethod.invoke(gatt);
            }
        } catch (Exception localException) {
            log.error("An exception occurred while performing: refresh", localException.getCause());
        }
        return false;
    }

    private void logSysInfo() {
        log.trace("{} {}", Build.MODEL, Build.VERSION.RELEASE);
    }

    public void setAuth(final boolean auth) {
        this.isAuth = auth;
    }

}
