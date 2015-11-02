package com.dudu.android.launcher.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/10/30.
 */
public class WeatherEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private WeatherSlots slots;

    public WeatherSlots getSlots() {
        return slots;
    }

    public void setSlots(WeatherSlots slots) {
        this.slots = slots;
    }
}
