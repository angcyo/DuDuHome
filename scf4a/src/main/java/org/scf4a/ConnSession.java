package org.scf4a;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.functions.Action1;

public class ConnSession {
    private static ConnSession ourInstance = new ConnSession();

    static {
        init();
    }

    private Logger log;
    private String lastConnectedMAC;
    private String lastConnectedName;
    private boolean isConnected;

    private Event.ConnectType type;

    private ConnSession() {
        log = LoggerFactory.getLogger("ble.session");
        type = Event.ConnectType.UNKNOWN;
    }

    public static ConnSession getInstance() {
        return ourInstance;
    }

    private static void init() {
        EventBus.getDefault().register(ourInstance);
    }

    public void uninit() {
        EventBus.getDefault().unregister(ourInstance);
    }

    public String getLastConnectedMAC() {
        return lastConnectedMAC;
    }

    public String getLastConnectedName() {
        return lastConnectedName;
    }

    public Event.ConnectType getType() {
        return type;
    }

    public boolean isSessionValid() {
        return lastConnectedMAC != null && lastConnectedName != null;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void reConnect() {
        if (isSessionValid()) {
            if (!isConnected()) {
                EventBus.getDefault().post(new Event.Connect(lastConnectedMAC, type, false));
            }
        }
    }

    public void onEvent(Event.Connect event) {
        type = event.getType();
        switch (type) {
            case BLE:
            case SPP:
                lastConnectedMAC = event.getMac();
                break;
        }
    }

    public void onEvent(Event.BTConnected event) {
        lastConnectedMAC = event.getDevAddr();
        lastConnectedName = event.getDevName();
        isConnected = true;
        type = event.getType();
        EventBus.getDefault().post(new BleStateChange(BleStateChange.BLECONNECTED));
        log.debug("BLE Connected");
    }

    public void onEvent(Event.SPIConnected event) {
        isConnected = true;
        type = Event.ConnectType.SPI;
    }

    public void onEvent(Event.Disconnected event) {
        isConnected = false;
        log.debug("BLE Disconnected");
        EventBus.getDefault().post(new BleStateChange(BleStateChange.BLEDISCONNECTED));

        Observable.timer(1, TimeUnit.MINUTES)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        log.debug("BLE timeout reScan, isConnected:{}", isConnected);
                        if (!isConnected)
                            EventBus.getDefault().post(new Event.StartScanner(type));
                    }
                });
    }
}
