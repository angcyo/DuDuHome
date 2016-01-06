package com.dudu.navi.vauleObject;

import com.amap.api.navi.enums.PathPlanningStrategy;

/**
 * Created by pc on 2015/11/14.
 */
public enum NaviDriveMode {

    /**
     *  速度最快
     */
    SPEEDFIRST(PathPlanningStrategy.DRIVING_DEFAULT,"速度最快"),

    /**
     * 时间最短且躲避拥堵
     */
    FASTESTTIME(PathPlanningStrategy.DRIVING_FASTEST_TIME,"时间最短且躲避拥堵"),

    /**
     * 避免收费
     */
    SAVEMONEY(PathPlanningStrategy.DRIVING_SAVE_MONEY,"避免收费"),

    /**
     *距离最短
     */
    SHORTDESTANCE(PathPlanningStrategy.DRIVING_SHORT_DISTANCE,"距离最短"),

    /**
     * 避免收费且躲避拥堵
     */
    AVOIDCONGESTION(PathPlanningStrategy.DRIVING_AVOID_CONGESTION,"避免收费且躲避拥堵"),

    /**
     * 不走高速快速路
     */
    NOEXPRESSWAYS(PathPlanningStrategy.DRIVING_NO_EXPRESS_WAYS,"不走高速快速路");


    private int nCode;

    private String name;

    private NaviDriveMode(int nCode, String name){
        this.nCode = nCode;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getnCode() {
        return nCode;
    }

    public String toString() {
        return String.valueOf ( this .nCode );
    }
}
