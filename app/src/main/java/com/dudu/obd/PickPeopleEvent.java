package com.dudu.obd;

/**
 * Created by pc on 2015/11/2.
 */
public class PickPeopleEvent {

    private double[] location;

    public PickPeopleEvent(double[] location){
        this.location = location;
    }
    public double[] getLocation(){
        return  location;
    }
}
