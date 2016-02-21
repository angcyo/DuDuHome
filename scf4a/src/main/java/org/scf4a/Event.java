package org.scf4a;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 定义连接事件
 */
public class Event {

    public enum ErrorCode {
        BluetoothNotSupport,
        BLENotSupport,
        ScanInvokeFail,
        ConnectInvokeFail,
        DeviceDisConnected,
        UNKNOWN
    }

    public enum ConnectType {
        BLE,
        SPP,
        USB,
        SPI,
        UNKNOWN
    }

    public static class StartScanner {
        private ConnectType type;

        public StartScanner(final ConnectType type) {
            this.type = type;
        }

        public ConnectType getType() {
            return type;
        }
    }

    public static class StopScanner {
    }

    public static class BackScanResult {
        private BluetoothDevice device;

        private ConnectType type;

        public BackScanResult(final BluetoothDevice device, final ConnectType type) {
            this.device = device;
            this.type = type;
        }

        public BluetoothDevice getDevice() {
            return device;
        }

        public ConnectType getType() {
            return type;
        }
    }

    public static class Connect {
        private String mac;
        private ConnectType type;
        private boolean isAuth;

        public Connect(String mac, ConnectType type, boolean isAuth) {
            this.mac = mac;
            this.type = type;
            this.isAuth = isAuth;
        }

        public ConnectType getType() {
            return type;
        }

        public String getMac() {
            return mac;
        }

        public boolean isAuth() {
            return isAuth;
        }
    }


    public static class DisConnect {
        private ConnectType type;

        public DisConnect(ConnectType type) {
            this.type = type;
        }

        public ConnectType getType() {
            return type;
        }
    }

    public static class BTConnected {

        private String devName; // 设备名称
        private String devAddr; // 设备地址
        private ConnectType type;

        public BTConnected(final String devName, final String devAddr, final ConnectType type) {
            this.devName = devName;
            this.devAddr = devAddr;
            this.type = type;
        }

        public String getDevAddr() {
            return devAddr;
        }

        public String getDevName() {
            return devName;
        }

        public ConnectType getType() {
            return type;
        }
    }

    public static class Disconnected {
        private ErrorCode error;

        public Disconnected(final ErrorCode error) {
            this.error = error;
        }

        public ErrorCode getError() {
            return error;
        }
    }

    public static class BLEInit {
        private BluetoothGatt mBluetoothGatt;
        private BluetoothGattCharacteristic mWriteChara;
        private BluetoothDevice mDevice;

        public BLEInit(final BluetoothDevice mDevice, final BluetoothGatt mBluetoothGatt, final BluetoothGattCharacteristic mWriteChara) {
            this.mDevice = mDevice;
            this.mBluetoothGatt = mBluetoothGatt;
            this.mWriteChara = mWriteChara;
        }

        public BluetoothDevice getDevice() {
            return mDevice;
        }

        public BluetoothGatt getBluetoothGatt() {
            return mBluetoothGatt;
        }

        public BluetoothGattCharacteristic getWriteChara() {
            return mWriteChara;
        }
    }

    public static class SPPInitOutStream {

        private OutputStream outStream;

        public SPPInitOutStream(OutputStream outStream) {
            this.outStream = outStream;
        }

        public OutputStream getOutStream() {
            return outStream;
        }
    }

    public static class SPPInitInStream {

        private InputStream inStream;

        public SPPInitInStream(InputStream inStream) {
            this.inStream = inStream;
        }

        public InputStream getInStream() {
            return inStream;
        }
    }

    public static class USBInit {

        private UsbEndpoint inEndpoint;  //读数据节点
        private UsbEndpoint outEndpoint;//写数据节点
        private UsbDeviceConnection connection;//USB连接

        public USBInit(UsbDeviceConnection connection, UsbEndpoint inEndpoint, UsbEndpoint outEndpoint) {
            this.connection = connection;
            this.inEndpoint = inEndpoint;
            this.outEndpoint = outEndpoint;
        }

        public UsbDeviceConnection getConnection() {
            return connection;
        }

        public UsbEndpoint getInEndpoint() {
            return inEndpoint;
        }

        public UsbEndpoint getOutEndpoint() {
            return outEndpoint;
        }
    }

    public static class UsbConnect {
    }

    public static class UsbDisConnect {
    }

    public static class Connecting {
    }

    public static class SPIConnected {
    }


    public static class BluetoothDisable{

    }

    public static class BluetoothEnable{

    }

    public static class Reconnect{

    }
}
