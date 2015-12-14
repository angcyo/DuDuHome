package com.dudu.monitor.valueobject;

import android.location.Location;

import com.amap.api.location.AMapLocation;
import com.dudu.monitor.utils.TimeUtils;

/**
 * Created by dengjun on 2015/11/26.
 * Description :
 */
public class LocationInfo {
    /**经度  */
    private double lon;
    /**纬度  */
    private double lat;
    /**高程  */
    private double altitude;
    /**速度  */
    private float speeds;
    /**方向  */
    private float direction;
    /** 精度*/
    private float accuracy;
    /** 时间*/
    private String createTime;

    private Integer type;		//类型 1.急加速 	2.急减速	3.急转弯	4.急变道	5.疲劳驾驶	6.发动机转速不匹配

//    private String obeId;

    public LocationInfo(Location location){

    }

    public LocationInfo(AMapLocation location){
        if(location == null)
            return;
        lon = location.getLongitude();
        lat = location.getLatitude();
        altitude = location.getAltitude();
        speeds = location.getSpeed();
        direction = location.getBearing();
        accuracy = location.getAccuracy();
        createTime = TimeUtils.dateLongFormatString(location.getTime(),
                TimeUtils.format1);
        type = 0;
    }


    @Override
    public boolean equals(Object object) {
        if (object == null){
            return false;
        }else {
            if (object instanceof LocationInfo){
                LocationInfo location = (LocationInfo)object;
                if (location.getLon() == this.lon && location.getLat() == this.lat
                        && location.getAccuracy() == this.accuracy && location.getAltitude() == this.altitude
                        /*&& location.getCreateTime().equals(this.createTime) */&& location.getDirection() == this.direction
                       /* && location.getType().intValue() == this.type.intValue()*/&& location.getSpeeds() == this.speeds){
                    return true;
                }
            }
            return false;
        }
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getSpeeds() {
        return speeds;
    }

    public void setSpeeds(float speeds) {
        this.speeds = speeds;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
