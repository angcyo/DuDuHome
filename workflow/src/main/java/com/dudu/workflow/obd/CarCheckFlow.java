package com.dudu.workflow.obd;

import java.io.IOException;

import rx.Observable;

public class CarCheckFlow {

    public static void startCarCheck() throws IOException {
        OBDStream.getInstance().exec("AT400");
    }

    public static void clearCarCheckError() throws IOException {
        OBDStream.getInstance().exec("AT401");
    }

    public Observable<String> engineFailed() throws IOException {
        return OBDStream.getInstance().obdErrorString()
                .filter(s -> s.contains("P010A"));
    }

    public Observable<String> gearboxFailed() throws IOException {
        return OBDStream.getInstance().obdErrorString()
                .filter(s -> s.contains("U0401"));
    }

    public Observable<String> ABSFailed() throws IOException {
        return OBDStream.getInstance().obdErrorString()
                .filter(s -> s.contains("C1B02"));
    }

    public Observable<String> SRSFailed() throws IOException {
        return OBDStream.getInstance().obdErrorString()
                .filter(s -> s.contains("B0052"));
    }

    public Observable<String> WSBFailed() throws IOException {
        return OBDStream.getInstance().obdErrorString()
                .filter(s -> s.contains("xxx"));
    }

}
