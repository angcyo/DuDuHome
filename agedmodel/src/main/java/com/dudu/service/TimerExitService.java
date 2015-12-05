package com.dudu.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.dudu.event.ExitTimerEvent;

import de.greenrobot.event.EventBus;

public class TimerExitService extends Service {
    public TimerExitService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
        @Override
        public void onCreate() {
            super.onCreate();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(8*60*60*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    EventBus.getDefault().post(new ExitTimerEvent());
                }
            }).start();
        }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}

