package com.dudu.workflow.obd;

import com.dudu.android.libserial.SerialManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android_serialport_api.SerialPort;
import rx.Observable;
import rx.ext.CreateObservable;
import rx.observables.ConnectableObservable;

public class OBDStream {
    private static OBDStream ourInstance = new OBDStream();

    public static OBDStream getInstance() {
        return ourInstance;
    }

    private Observable<String> obdRawData = null;
    private OBDStream() {
    }

    public ConnectableObservable<String> obdRawDataConnectable() throws IOException {

        SerialPort serialPort = SerialManager.getInstance().getSerialPort("/dev/ttyHS5");
        InputStream inputStream = serialPort.getInputStream();
        return CreateObservable.from(inputStream);
    }

    public Observable<String> obdRawData() throws IOException {

        if (obdRawData == null) {
            SerialPort serialPort = SerialManager.getInstance().getSerialPort("/dev/ttyHS5");
            InputStream inputStream = serialPort.getInputStream();
            obdRawData = CreateObservable.from(new InputStreamReader(inputStream));
        }
        return obdRawData;
    }

    public static void obdStreamClose() {
        SerialManager.getInstance().closeSerialPort();
    }

    public static Observable<String> obdRTString(Observable<String> input) {
        return input
                .filter(s -> s.startsWith("$OBD-RT"));
    }

    public static Observable<String> obdTTString(Observable<String> input) {
        return input
                .filter(s -> s.startsWith("$OBD-TT"));
    }

    public static Observable<String> obdErrorString(Observable<String> input) {
        return input
                .filter(s -> s.startsWith("$400="));
    }

    public static Observable<String> obdTSPMON(Observable<String> input) {
        return input
                .filter(s -> s.startsWith("$ATTSPMON+OK"));
    }

    public static Observable<String> obdTSPMOFF(Observable<String> input) {
        return input
                .filter(s -> s.startsWith("$ATTSPMOFF+OK"));
    }

    public static Observable<String[]> OBDRTData(Observable<String> input) {
        return input
                .map(s -> s.split(","))
                .filter(strings -> strings.length >= 15);
    }

    public static Observable<String[]> OBDTTData(Observable<String> input) {
        return input
                .map(s -> s.split(","))
                .filter(strings -> strings.length >= 9);
    }

    public static Observable<Double> engSpeedStream(Observable<String[]> input) {
        return input
                .map(strings -> strings[2])
                .map(s -> (double) Float.parseFloat(s));
    }

    public static Observable<Double> speedStream(Observable<String[]> input) {
        return input
                .map(strings -> strings[3])
                .map(s -> (double) Float.parseFloat(s));
    }
}
