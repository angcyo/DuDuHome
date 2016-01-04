package com.dudu.android.launcher.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RecordBindService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
