package com.dudu.monitor.valueobject;

import android.location.Location;

import com.amap.api.location.AMapLocation;

/**
 * Created by dengjun on 2015/11/26.
 * Description :
 */
public class LocationInfo {
    /**经度  */
    private double longitude;
    /**纬度  */
    private double latitude;
    /**高程  */
    private double altitude;
    /**速度  */
    private float speed;
    /**方向  */
    private float direction;
    /** 精度*/
    private float accuracy;
    /** 时间*/
    private String locTime;

    private Integer type;		//类型 1.急加速 	2.急减速	3.急转弯	4.急变道	5.疲劳驾驶	6.发动机转速不匹配

    private String obeId;

    public LocationInfo(Location location){

    }

    public LocationInfo(AMapLocation location){
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        altitude = location.getAltitude();
        speed = location.getSpeed();
        direction = location.getBearing();
        accuracy = location.getAccuracy();
    }








    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public String getLocTime() {
        return locTime;
    }

    public void setLocTime(String locTime) {
        this.locTime = locTime;
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
