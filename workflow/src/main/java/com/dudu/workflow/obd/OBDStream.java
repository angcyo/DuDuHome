package com.dudu.workflow.obd;

import com.dudu.android.libserial.SerialManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import android_serialport_api.SerialPort;
import rx.Observable;
import rx.ext.CreateObservable;
import rx.observables.ConnectableObservable;

public class OBDStream {
    private static OBDStream ourInstance = new OBDStream();

    public static OBDStream getInstance() {
        return ourInstance;
    }

    private OutputStream outputStream;
    private Observable<String> obdRawData = null;
    private Observable<String> obdRTString = null;
    private Observable<String> obdTTString = null;
    private Observable<String> obdErrorString = null;
    private Observable<String> obdTSPMON = null;
    private Observable<String> obdTSPMOFF = null;
    private Observable<String[]> OBDRTData = null;
    private Observable<Double> testSpeedStream = null;
    private Observable<String[]> OBDTTData = null;
    private Observable<Double> engSpeedStream = null;
    private Observable<Double> speedStream = null;

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
            outputStream = serialPort.getOutputStream();
            obdRawData = CreateObservable.from(new InputStreamReader(inputStream));
        }
        return obdRawData;
    }

    public static void obdStreamClose() {
        SerialManager.getInstance().closeSerialPort();
    }

    public void exec(String cmd) throws IOException {
        String send = cmd + "\r\n";
        outputStream.write(send.getBytes(StandardCharsets.US_ASCII));
    }

    public Observable<String> obdRTString() throws IOException {
        if (obdRTString == null) obdRTString = obdRTString(obdRawData());
        return obdRTString;
    }

    public Observable<String> obdTTString() throws IOException {
        if (obdTTString == null) obdTTString = obdTTString(obdRawData());
        return obdTTString;
    }

    public Observable<String> obdErrorString() throws IOException {
        if (obdErrorString == null) obdErrorString = obdErrorString(obdRawData());
        return obdErrorString;
    }

    public Observable<String> obdTSPMON() throws IOException {
        if (obdTSPMON == null) obdTSPMON = obdTSPMON(obdRawData());
        return obdTSPMON;
    }

    public Observable<String> obdTSPMOFF() throws IOException {
        if (obdTSPMOFF == null) obdTSPMOFF = obdTSPMOFF(obdRawData());
        return obdTSPMOFF;
    }

    public Observable<String[]> OBDRTData() throws IOException {
        if (OBDRTData == null) OBDRTData = OBDRTData(obdRTString());
        return OBDRTData;
    }

    public Observable<Double> testSpeedStream() throws IOException {
        if (testSpeedStream == null) testSpeedStream = testSpeedStream(obdRTString());
        return testSpeedStream;
    }

    public Observable<String[]> OBDTTData() throws IOException {
        if (OBDTTData == null) OBDTTData = OBDTTData(obdTTString());
        return OBDTTData;
    }

    public Observable<Double> engSpeedStream() throws IOException {
        if (engSpeedStream == null) engSpeedStream = engSpeedStream(OBDRTData());
        return engSpeedStream;
    }

    public Observable<Double> speedStream() throws IOException {
        if (speedStream == null) speedStream = speedStream(OBDRTData());
        return speedStream;
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
                .filter(strings -> strings.length == 16);
    }

    public static Observable<Double> testSpeedStream(Observable<String> input) {
        return input
                .map(s -> s.split(","))
                .filter(strings -> strings.length == 11)
                .map(strings -> strings[4])
                .map(s -> (double) Float.parseFloat(s));
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
