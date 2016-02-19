package com.dudu.workflow.obd;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class SpeedTest {
    private static SpeedTest ourInstance = new SpeedTest();

    public static SpeedTest getInstance() {
        return ourInstance;
    }

    private List<Subscription> subArr;

    private SpeedTest() {
        subArr = new ArrayList<>();
    }

    public void stopTestSpeed() {
//        for (Subscription sub : subArr) {
//            if (!sub.isUnsubscribed()) sub.unsubscribe();
//        }
        OBDStream.obdStreamClose();
        subArr.clear();
    }

    public void startTestSpeed() {
        Log.d("SerialPort", "startTestSpeed call start");
        Observable<String> input;
        try {
            input = OBDStream.getInstance().obdRawData();
        } catch (IOException e) {
            e.printStackTrace();
            input = null;
        }
        if (input != null) {
            final Observable<String[]> observable = OBDStream.OBDRTData(OBDStream.obdRTString(input));
            Subscription sub1 = OBDStream.engSpeedStream(observable)
                    .subscribeOn(Schedulers.io())
                    .subscribe(aDouble -> {
                        Log.d("SerialPort", "eng speed: " + aDouble);
                    });
            Subscription sub2 = OBDStream.speedStream(observable)
                    .subscribeOn(Schedulers.io())
                    .subscribe(aDouble -> {
                        Log.d("SerialPort", "speed: " + aDouble);
                    });
            subArr.add(sub1);
            subArr.add(sub2);
            Log.d("SerialPort", "startTestSpeed call end");
        }
    }

}

