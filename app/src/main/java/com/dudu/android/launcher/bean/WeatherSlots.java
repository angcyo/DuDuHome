package com.dudu.android.launcher.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/10/30.
 */
public class WeatherSlots implements Serializable {

    private static final long serialVersionUID = 1L;

    private DateTime datetime;

    private WeatherLocation location;

    public DateTime getDateTime() {
        return datetime;
    }

    public void setDateTime(DateTime dateTime) {
        this.datetime = dateTime;
    }

    public WeatherLocation getLocation() {
        return location;
    }

    public void setLocation(WeatherLocation location) {
        this.location = location;
    }

}
