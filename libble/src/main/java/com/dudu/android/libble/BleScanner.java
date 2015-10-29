package com.dudu.android.libble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;

import org.scf4a.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import de.greenrobot.event.EventBus;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BleScanner {

    private static final int SCAN_PERIOD = 5000;  // 扫描时间
    private Logger log;

    private String mName;
    private BluetoothAdapter mBluetoothAdapter;
    private ScanCallback mScanCallback;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;

    public BleScanner() {
        this.mName = "AutoBot";
        log = LoggerFactory.getLogger(BleScanner.class);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(final int callbackType, final ScanResult result) {
                    EventBus.getDefault().post(new Event.BackScanResult(result.getDevice()));
                }
/*
                @Override
                public void onBatchScanResults(final List<ScanResult> results) {
                    for (ScanResult result : results) {
                        EventBus.getDefault().post(new Event.BackScanResult(result.getDevice()));
                    }
                }
*/
            };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    log.info("onLeScan {} ", device.toString());
                    if (mName != null && device.getName() != null && device.getName().contains(mName)) {
                        //noinspection deprecation
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        log.info("Find Device: {}.", mName);
                        EventBus.getDefault().post(new Event.BackScanResult(device));
                    }
                }
            };
        }
    }

    public BleScanner(String name) {
        this.mName = name;
        log = LoggerFactory.getLogger(BleScanner.class);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void startScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            List<ScanFilter> filters = new ArrayList<>();
//            filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(BleManager.UUIDS_ON_XFA[0])).build());
//            filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(BleManager.UUIDS_ON_THREAD[0])).build());
//            ScanSettings scanSettings = new ScanSettings.Builder()
//                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
//                    .setMatchMode(ScanSettings.MATCH_NUM_FEW_ADVERTISEMENT)
//                    .build();
            mBluetoothAdapter.getBluetoothLeScanner().startScan(/*filters, scanSettings, */mScanCallback);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final UUID[] serviceUuids = {BleManager.UUIDS_ON_XFA[0], BleManager.UUIDS_ON_THREAD[0]};
            //noinspection deprecation
            if (!mBluetoothAdapter.startLeScan(/*serviceUuids, */mLeScanCallback)) {
                log.error("startLeScan fail!");
            }
        }
    }

    public void stopScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //noinspection deprecation
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
}
