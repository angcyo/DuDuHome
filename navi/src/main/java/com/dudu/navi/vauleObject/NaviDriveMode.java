package com.dudu.navi.vauleObject;

import com.amap.api.navi.AMapNavi;

/**
 * Created by pc on 2015/11/14.
 */
public enum NaviDriveMode {

    /**
     * 速度最快
     */
    SPEEDFIRST(AMapNavi.DrivingDefault, "速度最快"),

    /**
     * 时间最短且躲避拥堵
     */
    FASTESTTIME(AMapNavi.DrivingFastestTime, "时间最短且躲避拥堵"),

    /**
     * 避免收费
     */
    SAVEMONEY(AMapNavi.DrivingSaveMoney, "避免收费"),

    /**
     * 距离最短
     */
    SHORTDESTANCE(AMapNavi.DrivingShortDistance, "距离最短"),

    /**
     * 避免收费且躲避拥堵
     */
    AVOIDCONGESTION(AMapNavi.DrivingAvoidCongestion, "避免收费且躲避拥堵"),

    /**
     * 不走高速快速路
     */
    NOEXPRESSWAYS(AMapNavi.DrivingNoExpressways, "不走高速快速路");


    private int nCode;

    private String name;

    private NaviDriveMode(int nCode, String name) {
        this.nCode = nCode;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return String.valueOf(this.nCode);
    }

    public int getnCode() {
        return nCode;
    }
}
