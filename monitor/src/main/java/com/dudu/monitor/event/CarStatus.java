package com.dudu.monitor.event;

/**
 * Created by dengjun on 2015/12/2.
 * Description :
 */
public class CarStatus {
    public static final int CAR_OFFLINE = 0;
    public static final int CAR_ONLINE = 1;
    private int carStatus;
    public CarStatus(int carStatus){
        this.carStatus = carStatus;
    }
    public int getCarStatus(){
        return carStatus;
    }
}
