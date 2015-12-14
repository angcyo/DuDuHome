package com.dudu.obd;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.dudu.event.BleStateChange;
import com.duu.bluetooth.SppConnectMain;

import org.scf4a.Event;
import org.scf4a.EventRead;
import org.scf4a.EventWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by lxh on 2015/12/1.
 */
public class PodOBD {
    private Logger log;
    private Context mContext;
    private PrefixReadL1 readL1;
    private boolean hasData = false;
    private int disConnectedCount = 0;

    private Subscription disableSubscription;
    private Subscription enableSubscription;
    public PodOBD() {
        log = LoggerFactory.getLogger("obd.pod.spp");
        readL1 = new PrefixReadL1();
    }

    public void init(Context context) {
        log.debug("pod obd init");
        mContext = context;
        SppConnectMain.getInstance().init(context);
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        EventBus.getDefault().unregister(readL1);
        EventBus.getDefault().register(readL1);
        EventBus.getDefault().post(new Event.StartScanner());
    }

    public void uninit() {
        log.debug("pod obd uninit");
        SppConnectMain.getInstance().uninit();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().unregister(readL1);
        EventBus.getDefault().post(new Event.StopScanner());
    }

    public void onEvent(Event.BackScanResult event) {

        BluetoothDevice device = event.getDevice();
        log.debug("Try Connect {}[{}]", device.getName(), device.getAddress());
        EventBus.getDefault().post(new Event.Connect(device.getAddress(), Event.ConnectType.SPP, false));
    }

    public void onEvent(Event.Disconnected event) {
        hasData = false;
        log.debug("spp bluetooth Disconnected");
        processDisConnected(event);
    }

    public void onEvent(Event.BTConnected event) {
        log.debug("spp bluetooth BTConnected adr = {}",event.getDevAddr());
        EventBus.getDefault().post(new BleStateChange(BleStateChange.BLECONNECTED));
        disConnectedCount = 0;
        if(enableSubscription!=null&&disableSubscription!=null){
            disableSubscription.unsubscribe();
            enableSubscription.unsubscribe();
        }
        Observable.timer(30, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (!hasData) {
                            EventBus.getDefault().post(new Event.Reconnect());
                        }
                    }
                });
    }

    public void onEvent(EventRead.L1ReadDone event) {
        hasData = true;
    }

    private void processDisConnected(Event.Disconnected event) {
        disConnectedCount++;
        if (disConnectedCount >= 3)
            EventBus.getDefault().post(new BleStateChange(BleStateChange.BLEDISCONNECTED));
        if(event.getError() == Event.ErrorCode.ScanInvokeFail)
            EventBus.getDefault().post(new Event.BluetoothDisable());

        if(enableSubscription!=null&&disableSubscription!=null){
            disableSubscription.unsubscribe();
            enableSubscription.unsubscribe();
        }
        disableSubscription = Observable.timer(10, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        EventBus.getDefault().post(new Event.BluetoothEnable());
                       enableSubscription = Observable.timer(10, TimeUnit.SECONDS)
                                .subscribe(new Action1<Long>() {
                                    @Override
                                    public void call(Long aLong) {
                                        EventBus.getDefault().post(new Event.StartScanner());
                                    }
                                });
                    }
                });
    }
}
