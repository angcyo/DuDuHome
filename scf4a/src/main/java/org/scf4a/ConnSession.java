package org.scf4a;

import de.greenrobot.event.EventBus;

public class ConnSession {
    private static ConnSession ourInstance = new ConnSession();

    static {
        init();
    }

    private String lastConnectedMAC;
    private String lastConnectedName;
    private boolean isConnected;

    private Event.ConnectType type;

    private ConnSession() {
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
                EventBus.getDefault().post(new Event.Connect(lastConnectedMAC, type, true));
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
    }

    public void onEvent(Event.SPIConnected event) {
        isConnected = true;
        type = Event.ConnectType.SPI;
    }

    public void onEvent(Event.Disconnected event) {
        isConnected = false;
    }
}
