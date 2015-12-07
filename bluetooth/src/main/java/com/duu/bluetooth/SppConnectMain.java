package com.duu.bluetooth;

import android.content.Context;
import android.text.TextUtils;

import org.scf4a.Event;
import org.scf4a.EventWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.functions.Action1;


public class SppConnectMain {

    private static SppConnectMain ourInstance = new SppConnectMain();
    private SppScanner sppScanner;
    private Context mContext;
    private SppManager sppManager;

    private Logger log;

    private SppConnectMain() {
        log = LoggerFactory.getLogger("obd.pod.spp");
    }

    public static SppConnectMain getInstance() {
        return ourInstance;
    }

    public void onEvent(Event.StartScanner event) {
        log.debug("pod obd StartScanner");
        if (null == sppScanner) {
            sppScanner = new SppScanner(mContext);
        }
        Observable.timer(2, TimeUnit.MINUTES)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (sppManager.getState() != SppManager.STATE_CONNECTED) {
                            log.debug("spp scan time out");
                            try {
                                EventBus.getDefault().post(new
                                        Event.Disconnected(Event.ErrorCode.ScanInvokeFail));
                                sppScanner.broadRegister = false;
                                sppScanner.stopScan();
                            } catch (Exception e) {
                                log.debug("time out error",e);
                            }

                        }

                    }
                });

        if (!TextUtils.isEmpty(BluetoothMacUtil.getMac(mContext))) {

            onEvent(new Event.Connect(BluetoothMacUtil.getMac(mContext),
                    Event.ConnectType.SPP, false));
            return;
        }

        if (!sppScanner.broadRegister) {
            sppScanner.initScan();
            sppScanner.broadRegister = true;
        } else {
            sppScanner.startScan();
        }

    }

    public void onEvent(Event.StopScanner event) {
        log.debug("pod obd StopScanner");
        sppScanner.stopScan();
        sppScanner.broadRegister = false;
    }

    public void onEvent(Event.Connect event) {
        log.debug("pod obd connect");
        if (event.getType() == Event.ConnectType.SPP) {
            sppManager.connect(event.getMac(), true);
        }
    }

    public void onEvent(Event.DisConnect event) {
        log.debug("pod obd DisConnect");
        if (event.getType() == Event.ConnectType.SPP) {
            sppManager.stop();
        }
    }

    public void init(Context context) {
        mContext = context;
        EventBus.getDefault().register(ourInstance);
        if (null == sppManager) {
            sppManager = new SppManager(mContext);
        }
    }

    public void unRegister() {
        EventBus.getDefault().unregister(ourInstance);
    }

    public void onEvent(EventWrite.Data2Write event) {
        log.debug("pod obd Data2Write {}", event.data);
        sppManager.write(event.data);
    }

    public void onEvent(Event.BluetoothDisable event) {
        log.debug("bluetooth disable");
        sppManager.disableBluetooth();

    }

    public void onEvent(Event.BluetoothEnable event) {
        log.debug("bluetooth enable");
        sppManager.enableBluetooth();

    }

}
