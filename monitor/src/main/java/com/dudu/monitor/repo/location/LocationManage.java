package com.dudu.monitor.repo.location;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.dudu.monitor.valueobject.LocationInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengjun on 2015/11/26.
 * Description :
 */
public class LocationManage implements ILocationListener{
    private static LocationManage instance = null;

    private ILocation mILocation;

    private List<LocationInfo> locationInfoList;
    private AMapLocation currentLoction; // 当前位置点 未过滤
    private LocationInfo mCurLocation; //当前位置点 过滤后的

    private Logger log;
    public static  LocationManage getInstance(){
        if (instance == null){
            synchronized (LocationManage.class){
                if (instance == null){
                    instance = new LocationManage();
                }
            }
        }
        return instance;
    }

    private LocationManage(){
        mILocation = new AmapLocation();
        mILocation.setLocationListener(this);

        locationInfoList = new ArrayList<>();
        log = LoggerFactory.getLogger("lbs.gps");
    }


    @Override
    public void onLocationResult(Object locationInfo) {
        if (locationInfo instanceof AMapLocation){
            log.debug("AMapLocation changed");
            currentLoction = (AMapLocation) locationInfo;

        }else if (locationInfo instanceof  LocationInfo){
            log.debug("gpsFilter changed");
            locationInfoList.add((LocationInfo)locationInfo);
            mCurLocation = (LocationInfo)locationInfo;
        }
    }

    @Override
    public void onLocationState(int locationState) {

    }

    public List<LocationInfo> getLocationInfoList() {
        return locationInfoList;
    }

    public AMapLocation getCurrentLoction() {
        return currentLoction;
    }

    public LocationInfo getCurLocation() {
        return mCurLocation;
    }

    public void startLocation(Context context){
//        mILocation.setLocationListener(this);
        mILocation.startLocation(context);
    }

    public void stopLocation(){
        mILocation.stopLocation();
    }
}
