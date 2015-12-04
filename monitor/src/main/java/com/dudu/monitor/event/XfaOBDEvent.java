package com.dudu.monitor.event;

/**
 * Created by lxh on 2015/12/4.
 */
public class XfaOBDEvent {

    private String obdData;

    public XfaOBDEvent(String obdData){

        this.obdData = obdData;
    }

    public String getObdData() {
        return obdData;
    }
}
