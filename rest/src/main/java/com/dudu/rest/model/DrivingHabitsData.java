package com.dudu.rest.model;

/**
 * Created by Administrator on 2016/2/19.
 */
public class DrivingHabitsData {

    public static final String LECIZHE = "乐驰者";
    public static final String JIXINGXIA = "急行侠";
    public static final String JIJIAKE = "极驾客";

    private String drivingHabitsTime;
    private String dateTime;
    private String driverType;

    public String getDrivingHabitsTime() {
        return drivingHabitsTime;
    }

    public void setTime(String drivingHabitsTime) {
        this.drivingHabitsTime = drivingHabitsTime;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }
}
