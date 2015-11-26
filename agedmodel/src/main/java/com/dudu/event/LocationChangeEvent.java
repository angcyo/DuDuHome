package com.dudu.event;

import com.amap.api.location.AMapLocation;

/**
 * Created by Administrator on 2015/11/25.
 */
public class LocationChangeEvent {
    private AMapLocation location;
    public  LocationChangeEvent(AMapLocation location){
        this.location=location;
    }
    public AMapLocation getLocation(){
        return location;
    }
}
