package com.dudu.monitor.repo.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.dudu.monitor.utils.LocationFilter;
import com.dudu.monitor.utils.LocationUtils;
import com.dudu.monitor.utils.TimeUtils;
import com.dudu.monitor.valueobject.LocationInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import de.greenrobot.event.EventBus;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by dengjun on 2015/11/26.
 * Description :
 */
public class AmapLocation implements AMapLocationListener, ILocation{
    private Context mContext;

    private LocationManagerProxy mLocationManagerProxy;
    private LocationManager locationManager;
    private ILocationListener mILocationListener = null;

    private Logger log;

    private int GPSdataTime = 0;// 第几个GPS点
    private AMapLocation last_Location;// 前一个位置点
    private AMapLocation cur_Location; // 当前位置点
    private boolean isAvalable = false; // 标志定位点是否有效
    private boolean isFirstRun = true; // 第一个点
    private boolean isFirstLoc = true; // 是否第一次定位成功
    private List<AMapLocation> unAvalableList; // 存放通过第一阶段但没通过第二阶段过滤的点
//    private List<LocationInfo> gpsDataListToSend; // 通过过滤后的定位点的集合

    // 状态监听
    GpsStatus.Listener getGpsStatuslistener = new GpsStatus.Listener() {

        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    log.debug("第一次定位");
                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//                    log.debug("卫星状态改变");
                    EventBus.getDefault().post(locationManager.getGpsStatus(null));
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

        }
    };

    public AmapLocation() {
        log = LoggerFactory.getLogger("lbs.gps");
    }

    @Override
    public void startLocation(Context context) {
        mContext = context;

        mLocationManagerProxy = LocationManagerProxy.getInstance(context);
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, 2000, 10, this);

        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.addGpsStatusListener(getGpsStatuslistener);
    }

    @Override
    public void stopLocation() {
        if(mLocationManagerProxy != null){
            mLocationManagerProxy.removeUpdates(this);
            mLocationManagerProxy.destroy();

            locationManager.removeGpsStatusListener(getGpsStatuslistener);
        }
        mLocationManagerProxy = null;
    }

    @Override
    public void setLocationListener(ILocationListener iLocationListener) {
        mILocationListener = iLocationListener;
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        String provider = location.getProvider();
        log.debug("定到位置 "+ provider);
        log.debug("位置信息 "+ "纬度:"+ location.getLatitude()+ "  经度："+ location.getLongitude());

        if (GPSdataTime < 2) {
            GPSdataTime++;
            return;
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

        if (mILocationListener != null){
            mILocationListener.onLocationResult(location);
        }
        /*LocationInfo locationInfo = new LocationInfo(location);
        if (mILocationListener != null) {
            mILocationListener.onLocationResult(locationInfo);
        }*/

        handlerGPS(location);
        // 更新preLocation
        last_Location = location;
        cur_Location = location;
    }

    private void handlerGPS(AMapLocation location) {
        // 第一阶段过滤
        if (LocationFilter.checkStageOne(location.getLatitude(),
                location.getLongitude(), location.getAccuracy(),
                location.getBearing())) {
            log.debug("gps第一阶段过滤成功");
            if (isFirstRun) {
                // 第一个点，只用第一阶段过滤和速度过滤
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
                LocationInfo locationInfo = new LocationInfo(location);
                if (mILocationListener != null) {
                    mILocationListener.onLocationResult(locationInfo);
                }
                unAvalableList.clear();
            }

        } else {
            log.trace("gps未通过过滤locaion:lat={},lon={},speed={},bear={}", location.getLatitude(), location.getLongitude(),
                    location.getSpeed(), location.getBearing());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        log.debug("定位 onLocationChanged");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        log.debug("定位 onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        log.debug("定位 onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        log.debug("定位 onProviderDisabled");
    }

}
