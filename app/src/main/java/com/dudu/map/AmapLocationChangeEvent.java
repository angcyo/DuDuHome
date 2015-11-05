package com.dudu.map;

import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.LocationSource;

/**
 * Created by pc on 2015/11/3.
 */
public class AmapLocationChangeEvent {

    private AMapLocation aMapLocation;

    private LocationSource.OnLocationChangedListener listener;

    private LocationSource locationSource;

    public AmapLocationChangeEvent(AMapLocation aMapLocation){
        this.aMapLocation = aMapLocation;
    }

    public AMapLocation getAMapLocation(){

        return aMapLocation;
    }

    public LocationSource.OnLocationChangedListener getListener(){
        return listener;
    }




}
