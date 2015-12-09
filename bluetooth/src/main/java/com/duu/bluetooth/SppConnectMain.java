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
    private int timeoutCount = 0;
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
                            timeoutCount++;
                            try {
                                if (!hasMac()) {
                                    stopScan();
                                }
                                EventBus.getDefault().post(new Event.Disconnected(Event.ErrorCode.ScanInvokeFail));
                            } catch (Exception e) {
                                log.debug("time out error", e);
                            }
                        }
                    }
                });

        if(timeoutCount>=2||!hasMac()){
            log.debug("obd timeoutcount[{}]",timeoutCount);
            UnbindBluetooth.unbind();
            scanner();
            return;
        }
        if (hasMac()) {
            onEvent(new Event.Connect(BluetoothMacUtil.getMac(mContext),
                    Event.ConnectType.SPP, false));
            return;
        }
    }

    public void onEvent(Event.StopScanner event) {
        log.debug("pod obd StopScanner");
        stopScan();
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

    public void uninit() {
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

    public void onEvent(Event.Reconnect event){
        log.debug("obd Reconnect");
        sppManager.stop();
        Observable.timer(5, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        sppManager.connect(BluetoothMacUtil.getMac(mContext), true);
                        }
                });

    }

    private boolean hasMac(){
       return !TextUtils.isEmpty(BluetoothMacUtil.getMac(mContext));
    }

    private void scanner(){
        if (!sppScanner.broadRegister) {
            sppScanner.initScan();
            sppScanner.broadRegister = true;
        } else {
            sppScanner.startScan();
        }

    }

    private void stopScan(){
        sppScanner.broadRegister = false;
        sppScanner.stopScan();
    }
}
