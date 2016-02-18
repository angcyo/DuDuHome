package com.dudu.android.libserial;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

public class SerialManager {
    private static final String TAG = "SerialPort";
    private static SerialManager ourInstance = new SerialManager();

    public static SerialManager getInstance() {
        return ourInstance;
    }

    private int baudrate = 115200;

    private SerialManager() {
    }

    private SerialPort mSerialPort = null;

    public SerialPort getSerialPort(String path) throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            /* Read serial port parameters */
            Log.d(TAG, "config:path=" + path + " bandrate=" + baudrate);
            /* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

			/* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }
}
