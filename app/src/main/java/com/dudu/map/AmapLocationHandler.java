package com.dudu.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.LocationSource;
import com.dudu.android.launcher.ui.activity.LocationMapActivity;
import com.dudu.android.launcher.ui.activity.NaviCustomActivity;
import com.dudu.android.launcher.utils.ActivitiesManager;
import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.LocationFilter;
import com.dudu.android.launcher.utils.LocationUtils;
import com.dudu.android.launcher.utils.TimeUtils;
import com.dudu.obd.MyGPSData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.qos.logback.core.util.LocationUtil;
import de.greenrobot.event.EventBus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by pc on 2015/11/3.
 */
public class AmapLocationHandler implements AMapLocationListener{

    private static final String TAG = "AmapLocationHandler";

    private Context mContext;

    private LocationManagerProxy mLocationManagerProxy;

    private int GPSdataTime = 0;// 第几个GPS点

    private AMapLocation last_Location;// 前一个位置点

    private AMapLocation cur_Location; // 当前位置点

    private boolean isAvalable = false; // 标志定位点是否有效

    private boolean isFirstRun = true; // 第一个点

    private boolean isFirstLoc = true; // 是否第一次定位成功

    private List<AMapLocation> unAvalableList; // 存放通过第一阶段但没通过第二阶段过滤的点

    private List<MyGPSData> gpsDataListToSend; // 通过过滤后的定位点的集合

    private Logger log;

    private Activity topActivity;

    private LocationManager locationManager;

    public AmapLocationHandler() {

        log = LoggerFactory.getLogger("lbs.gps");
    }

    public void init(Context context) {
        FileUtils.writeFile("/sys/class/gps_vreg/gps_vreg/gps_enable", "1");
        mContext = context;
        mLocationManagerProxy = LocationManagerProxy.getInstance(context);
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, 2000, 10, this);
        gpsDataListToSend = new ArrayList<>();
        unAvalableList = new ArrayList<>();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 监听状态
        if (checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.addGpsStatusListener(getGpsStatuslistener);

    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        String provider = location.getProvider();
        if (GPSdataTime < 2 && !provider.equals("lbs")) {
            GPSdataTime++;
            return;
        }
        log.trace("onLocationChanged");

        // 保存当前定位点
        LocationUtils.getInstance(mContext).setCurrentLocation(
                location.getLatitude(), location.getLongitude());
        
        topActivity = ActivitiesManager.getInstance().getTopActivity();
        if ((topActivity instanceof LocationMapActivity) ||
                (topActivity instanceof NaviCustomActivity) ||
                (topActivity instanceof NaviCustomActivity)) {

            EventBus.getDefault().post(new AmapLocationChangeEvent(location));
        }

        // m每秒转换成千米每小时
        if (location.hasSpeed() && location.getSpeed() > 0)
            location.setSpeed(location.getSpeed() * 36 / 10);
        if (isFirstLoc) {
            Bundle locBundle = location.getExtras();
            if (locBundle != null) {

                LocationUtils.getInstance(mContext).setCurrentCitycode(locBundle.getString("citycode"));
                last_Location = location;
                isFirstLoc = false;
            }
        }

        handlerGPS(location);
        // 更新preLocation
        last_Location = location;

        cur_Location = location;

    }

    private void handlerGPS(AMapLocation location){
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
            log.trace("gps未通过过滤locaion:{},{},{},{}", location.getLatitude(), location.getLongitude(),
                    location.getSpeed(), location.getBearing());
        }
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


    public AMapLocation getLast_Location() {

        return last_Location;
    }

    public AMapLocation getCur_Location() {
        return cur_Location;
    }

    public List<MyGPSData> getGpsDataListToSend() {
        return gpsDataListToSend;
    }

    public void stopLocation() {
        if (mLocationManagerProxy != null) {
            FileUtils.writeFile("/sys/class/gps_vreg/gps_vreg/gps_enable", "0");
            mLocationManagerProxy.removeUpdates(this);
            mLocationManagerProxy.destroy();
        }
        mLocationManagerProxy = null;
    }

    // 状态监听
    GpsStatus.Listener getGpsStatuslistener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    log.debug("第一次定位");
                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
                            .iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {

                        count++;
                    }
                    log.debug("搜索到{}颗卫星",count);

                    break;
                // 定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    log.debug("定位启动");
                    break;
                // 定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    log.debug("定位结束");
                    break;
            }
        };
    };

}
