package com.dudu.workflow.obd;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        for (Subscription sub : subArr) {
            if (!sub.isUnsubscribed()) sub.unsubscribe();
        }
        subArr.clear();
    }

    public void startTestSpeed() {
        Log.d("SerialPort", "startTestSpeed call start");
        try {
            Subscription sub1 = OBDStream.getInstance().testSpeedStream()
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(aDouble -> {
                        Log.d("SerialPort", "test speed: " + aDouble);
                    });
            Subscription sub2 = OBDStream.getInstance().speedStream()
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(aDouble -> {
                        Log.d("SerialPort", "speed: " + aDouble);
                    });
            subArr.add(sub1);
            subArr.add(sub2);
            Log.d("SerialPort", "startTestSpeed call end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

