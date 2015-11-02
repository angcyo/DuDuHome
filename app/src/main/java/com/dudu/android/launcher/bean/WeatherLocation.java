package com.dudu.android.launcher.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/10/30.
 */
public class WeatherLocation implements Serializable {

    private static final long serialVersionUID = 1L;

    private String type;

    private String cityAddr;

    private String city;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCityAddr() {
        return cityAddr;
    }

    public void setCityAddr(String cityAddr) {
        this.cityAddr = cityAddr;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
