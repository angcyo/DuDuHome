package com.dudu.event;

/**
 * Created by Eaway.Shen on 2015/12/5.
 */
public class ListenerResetEvent {
    public static final int LISTENER_ON_HELLO = 0;
    public static final int LISTENER_OFF = 1;
    public static final int LISTENER_ON = 2;

    private int listenerStatus;

    public ListenerResetEvent(int listenerStatus) {
        this.listenerStatus = listenerStatus;
    }

    public int getListenerStatus() {
        return listenerStatus;
    }
}
