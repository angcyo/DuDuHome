package com.dudu.workflow.obd;

import com.dudu.android.libserial.SerialManager;

import java.io.IOException;
import java.io.InputStream;

import android_serialport_api.SerialPort;
import rx.ext.CreateObservable;
import rx.observables.ConnectableObservable;

public class OBDStream {

    ConnectableObservable<String> obdRawData() throws IOException {

        SerialPort serialPort = SerialManager.getInstance().getSerialPort("/dev/ttyHSL0");
        InputStream inputStream = serialPort.getInputStream();
        return CreateObservable.from(inputStream);
    }

}
