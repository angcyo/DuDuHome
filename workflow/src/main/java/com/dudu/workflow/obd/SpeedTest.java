package com.dudu.workflow.obd;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    public void stopTest() {
        for (Subscription sub : subArr) {
            if (!sub.isUnsubscribed()) sub.unsubscribe();
        }
        subArr.clear();
    }

    public void startTestError() {
        Log.d("SerialPort", "startTestError call start");
        try {
            Subscription sub1 = OBDStream.getInstance().obdErrorString()
                    .doOnNext(s -> Log.d("SerialPort", s))
                    .map(s -> s.split("|"))
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(s -> {
                        Log.d("SerialPort", "" + s);
                    });
            subArr.add(sub1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("SerialPort", "startTestError call end");
    }

    public void startTestGUN3() {
        Log.d("SerialPort", "startTestGUN3 call start");
        try {
            Subscription sub1 = OBDStream.getInstance().engSpeedStream()
                    .map(aDouble -> aDouble > 3000)
                    .distinctUntilChanged()
                    .take(5)
                    .timeout(60, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(aBoolean -> Log.d("SerialPort", "test startTestGUN3 changed ")
                            , throwable -> Log.d("SerialPort", "test startTestGUN3 timeout ")
                            , () -> Log.d("SerialPort", "test startTestGUN3 got it "));
            subArr.add(sub1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("SerialPort", "startTestGUN3 call end");
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

