package com.duu.bluetooth;

import android.content.Context;

import org.scf4a.Event;

import de.greenrobot.event.EventBus;


public class SppConnectMain {

    private static SppConnectMain ourInstance = new SppConnectMain();
    private SppScanner sppScanner;
    private Context mContext;
    private SppManager sppManager;

    private SppConnectMain() {
    }

    public static SppConnectMain getInstance() {
        return ourInstance;
    }

    public void onEvent(Event.StartScanner event) {
        if (null == sppScanner) {
            sppScanner = new SppScanner(mContext);
        }
        if (!sppScanner.broadRegister) {
            sppScanner.initScan();
            sppScanner.broadRegister = true;
        } else {
            sppScanner.startScan();
        }
    }

    public void onEvent(Event.StopScanner event) {
        sppScanner.stopScan();
        sppScanner.broadRegister = false;
    }

    public void onEvent(Event.Connect event) {
        if (event.getType() == Event.ConnectType.SPP) {
            sppManager.connect(event.getMac(), true);
        }
    }

    public void onEvent(Event.DisConnect event) {
        if (event.getType() == Event.ConnectType.SPP) {
            sppManager.stop();
        }
    }

    public void init(Context context) {
        mContext = context;
        EventBus.getDefault().register(ourInstance);
        if (null == sppManager) {
            sppManager = new SppManager();
        }
    }

    public void unRegister() {
        EventBus.getDefault().unregister(ourInstance);
    }
}
