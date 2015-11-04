package com.dudu.map;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.LocationSource;
import com.dudu.android.launcher.utils.LocationFilter;
import com.dudu.android.launcher.utils.LocationUtils;
import com.dudu.android.launcher.utils.TimeUtils;
import com.dudu.obd.MyGPSData;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by pc on 2015/11/3.
 */
public class AmapLocationHandler implements AMapLocationListener,LocationSource {

    private static final String TAG = "AmapLocationHandler";

    private Context mContext;

    private LocationManagerProxy mLocationManagerProxy;

    private OnLocationChangedListener mListener;

    private int GPSdataTime = 0;// 第几个GPS点

    private AMapLocation last_Location;// 前一个位置点

    private AMapLocation cur_Location; // 当前位置点

    private boolean isAvalable = false; // 标志定位点是否有效

    private boolean isFirstRun = true; // 第一个点

    private boolean isFirstLoc = true; // 是否第一次定位成功

    private List<AMapLocation> unAvalableList; // 存放通过第一阶段但没通过第二阶段过滤的点

    private List<MyGPSData> gpsDataListToSend; // 通过过滤后的定位点的集合

    private Logger log;

    public AmapLocationHandler (){

        log = LoggerFactory.getLogger("lbs.gps");
    }

    public void init(Context context){
        mLocationManagerProxy = LocationManagerProxy.getInstance(context);
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, 1000, 10, this);
        gpsDataListToSend = new ArrayList<>();
        unAvalableList = new ArrayList<>();
    }

    @Override
    public void onLocationChanged(AMapLocation location) {

        String provider = location.getProvider();
        if (GPSdataTime < 2 && !provider.equals("lbs")) {
            GPSdataTime++;
            return;
        }


        // 保存当前定位点
        LocationUtils.getInstance(mContext).setCurrentLocation(
                location.getLatitude(), location.getLongitude());

        EventBus.getDefault().post(new AmapLocationChangeEvent(location));

        // m每秒转换成千米每小时
        if (location.hasSpeed() && location.getSpeed() > 0)
            location.setSpeed(location.getSpeed() * 36 / 10);
        if (isFirstLoc) {
            last_Location = location;
            isFirstLoc = false;
        }
        // 第一阶段过滤
        if (LocationFilter.checkStageOne(location.getLatitude(),
                location.getLongitude(), location.getAccuracy(),
                location.getBearing())) {
            // 第一个点，只用第一阶段过滤和速度过滤
            if (isFirstRun) {
                if (LocationFilter.checkSpeed(location.getSpeed())) {
                    isFirstRun = false;
                    isAvalable = true;
                } else {
                    isAvalable = false;
                    unAvalableList.add(location);
                }
            } else {
                if (location.getSpeed() > 2
                        && LocationFilter.checkStageTwo(last_Location
                        .getSpeed(), location.getSpeed(), TimeUtils
                        .dateLongFormatString(last_Location.getTime(),
                                TimeUtils.format1), TimeUtils
                        .dateLongFormatString(location.getTime(),
                                TimeUtils.format1))) { // 如果不是第一个点且速度大于2，则需通过第二阶段过滤
                    log.debug("gps第二阶段过滤成功");
                    isAvalable = true;
                    unAvalableList.clear();
                } else if (location.getSpeed() >= 0
                        && location.getSpeed() <= 2
                        && LocationFilter.checkStageTwo(last_Location
                        .getSpeed(), location.getSpeed(), TimeUtils
                        .dateLongFormatString(last_Location.getTime(),
                                TimeUtils.format1), TimeUtils
                        .dateLongFormatString(location.getTime(),
                                TimeUtils.format1))
                        && LocationFilter
                        .checkSpeedDValue(location.getSpeed(), location
                                        .getSpeed(), TimeUtils
                                        .dateLongFormatString(
                                                location.getTime(),
                                                TimeUtils.format1), TimeUtils
                                        .dateLongFormatString(
                                                location.getTime(),
                                                TimeUtils.format1), location
                                        .getLatitude(),
                                location.getLongitude(), location
                                        .getLatitude(), location
                                        .getLongitude())) { // 速度小于2，需经过第二阶段过滤

                    log.debug("gps第三阶段过滤成功");
                    // 和静态过滤)
                    isAvalable = true;
                    unAvalableList.clear();
                } else {
                    isAvalable = false;
                    unAvalableList.add(location);
                    if (unAvalableList.size() == 3) {
                        // 如果第一个点和第二个点通过第二阶段过滤，则再将第二个点和第三个点用第二阶段的规则过滤，否则清空列表
                        if (LocationFilter.checkStageTwo(unAvalableList.get(0)
                                        .getSpeed(), unAvalableList.get(1).getSpeed(),
                                TimeUtils.dateLongFormatString(unAvalableList
                                        .get(0).getTime(), TimeUtils.format1),
                                TimeUtils.dateLongFormatString(unAvalableList
                                        .get(1).getTime(), TimeUtils.format1))) {
                            if (LocationFilter.checkStageTwo(unAvalableList
                                            .get(1).getSpeed(), unAvalableList.get(2)
                                            .getSpeed(), TimeUtils
                                            .dateLongFormatString(unAvalableList.get(1)
                                                    .getTime(), TimeUtils.format1),
                                    TimeUtils.dateLongFormatString(
                                            unAvalableList.get(2).getTime(),
                                            TimeUtils.format1))) {
                                isAvalable = true;
                                location = unAvalableList.get(2);
                                // unAvalableList.clear();
                            } else {
                                unAvalableList.clear();
                            }
                        } else {
                            unAvalableList.clear();
                        }
                    }
                }
            }
            if (isAvalable) {
                MyGPSData myGpsData = new MyGPSData(location.getLatitude(),
                        location.getLongitude(), location.getSpeed(),
                        location.getAltitude(), location.getBearing(),
                        TimeUtils.dateLongFormatString(location.getTime(),
                                TimeUtils.format1), location.getAccuracy(), 0);
                if (gpsDataListToSend != null
                        && !gpsDataListToSend.contains(myGpsData)) {
                    gpsDataListToSend.add(myGpsData);
                }
                unAvalableList.clear();
            }

        } else {
            log.debug("gps未通过过滤locaion:{},{}",location.getLatitude(),location.getLongitude());
        }

        // 更新preLocation
        last_Location = location;

        cur_Location = location;

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {


        mListener = onLocationChangedListener;

    }

    @Override
    public void deactivate() {
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(this);
            mLocationManagerProxy.destroy();
        }
        mLocationManagerProxy = null;
    }

    public AMapLocation getLast_Location (){

        return  last_Location;
    }

    public AMapLocation getCur_Location(){
        return  cur_Location;
    }

    public List<MyGPSData> getGpsDataListToSend(){
        return gpsDataListToSend;
    }
}
