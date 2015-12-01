package com.dudu.obd;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.dudu.event.BleStateChange;
import com.duu.bluetooth.SppConnectMain;

import org.scf4a.Event;
import org.scf4a.EventRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by lxh on 2015/12/1.
 */
public class PodOBD {
    private Logger log;

    private Context mContext;
    public PodOBD(){
        log = LoggerFactory.getLogger("obd.pod.spp");
    }

    public void init(Context context){
        mContext = context;
        SppConnectMain.getInstance().init(context);
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new Event.StartScanner());
    }

    public void onEvent(Event.BackScanResult event) {

        BluetoothDevice device = event.getDevice();
        log.debug("Try Connect {}[{}]", device.getName(), device.getAddress());
        EventBus.getDefault().post(new Event.Connect(device.getAddress(), Event.ConnectType.SPP, false));
    }

    public void onEventBackgroundThread(EventRead.L1ReadDone event) {
        final byte[] data = event.getData();
        try {
            log.debug("Receive OBD Data: = {}", new String(data, "UTF-8"));
        } catch (Exception e) {
            log.error("OBD Parse exception", e);
            e.printStackTrace();
        }
    }

    public void onEvent(Event.Disconnected event){

        log.debug("spp bluetooth Disconnected");
        EventBus.getDefault().post(new BleStateChange(BleStateChange.BLEDISCONNECTED));
        Observable.timer(10, TimeUnit.SECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        EventBus.getDefault().post(new Event.StartScanner());
                    }
                });
    }

    public void onEvent(Event.BTConnected event){
        log.debug("spp bluetooth BTConnected");
        EventBus.getDefault().post(new BleStateChange(BleStateChange.BLECONNECTED));
    }
}
