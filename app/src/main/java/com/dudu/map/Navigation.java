package com.dudu.map;

/**
 * Created by pc on 2015/11/3
 */
public class Navigation {

    public static final int NAVI_NORMAL = 1;

    public static final int NAVI_TWO = 2;

    public static final int NAVI_BACK = 3;

    private String naviAddress;

    private double[] destination;

    private int type;

    private int driveMode;

    public Navigation(double[]destination,int navitype,int driveMode){

        this.driveMode = driveMode;
        this.destination = destination;
        this.type = navitype;
    }

    public String getNaviAddress() {
        return naviAddress;
    }

    public double[] getDestination() {
        return destination;
    }

    public int getType() {
        return type;
    }

    public int getDriveMode() {
        return driveMode;
    }

    public void setNaviAddress(String naviAddress) {
        this.naviAddress = naviAddress;
    }

    public void setDestination(double[] destination) {
        this.destination = destination;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDriveMode(int driveMode) {
        this.driveMode = driveMode;
    }
}
