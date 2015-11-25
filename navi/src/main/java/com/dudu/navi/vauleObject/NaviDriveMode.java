package com.dudu.navi.vauleObject;

import com.amap.api.navi.AMapNavi;

/**
 * Created by pc on 2015/11/14.
 */
public enum NaviDriveMode {

    /**
     *  速度最快
     */
    SPEEDFIRST(AMapNavi.DrivingDefault),

    /**
     * 时间最短且躲避拥堵
     */
    FASTESTTIME(AMapNavi.DrivingFastestTime),

    /**
     * 避免收费
     */
    SAVEMONEY(AMapNavi.DrivingSaveMoney),

    /**
     *距离最短
     */
    SHORTDESTANCE(AMapNavi.DrivingShortDistance),

    /**
     * 避免收费且躲避拥堵
     */
    AVOIDCONGESTION(AMapNavi.DrivingAvoidCongestion),

    /**
     * 不走高速快速路
     */
    NOEXPRESSWAYS(AMapNavi.DrivingNoExpressways);


    private int nCode;

    private NaviDriveMode(int nCode){

    }

    public String toString() {
        return String.valueOf ( this .nCode );
    }
}
