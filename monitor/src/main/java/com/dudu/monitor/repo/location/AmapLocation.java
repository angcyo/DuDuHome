package com.dudu.monitor.repo.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.LocationManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.dudu.monitor.utils.LocationFilter;
import com.dudu.monitor.utils.LocationUtils;
import com.dudu.monitor.utils.TimeUtils;
import com.dudu.monitor.valueobject.LocationInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by dengjun on 2015/11/26.
 * Description :
 */
public class AmapLocation implements AMapLocationListener, ILocation {
    private Context mContext;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private LocationManager locationManager;
    private ILocationListener mILocationListener = null;

    private Logger log;

    private int GPSdataTime = 0;// 第几个GPS点
    private AMapLocation last_Location, cur_locatopn;// 前一个位置点
    private boolean isAvalable = false; // 标志定位点是否有效
    private boolean isFirstRun = true; // 第一个点
    private boolean isFirstLoc = true; // 是否第一次定位成功
    private List<AMapLocation> unAvalableList = new ArrayList<>(); // 存放通过第一阶段但没通过第二阶段过滤的点

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
                    log.debug("卫星状态改变");
                    if (last_Location != null && locationManager != null)
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
        locationClient = new AMapLocationClient(mContext);
        locationOption = new AMapLocationClientOption();
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClient.setLocationListener(this);
        locationOption.setNeedAddress(true);
        locationOption.setInterval(2000);
        locationClient.startLocation();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void stopLocation() {
        if (locationClient != null) {
            locationClient.unRegisterLocationListener(this);
            locationClient.stopLocation();
            locationClient.onDestroy();
            locationManager.removeGpsStatusListener(getGpsStatuslistener);
        }
        locationManager = null;
        if (cur_locatopn != null) {
            LocationUtils.getInstance(mContext).setCurrentLocation(cur_locatopn.getLatitude(),
                    cur_locatopn.getLongitude());
        }
        isFirstRun = true;
        isFirstLoc = true;

    }

    @Override
    public void setLocationListener(ILocationListener iLocationListener) {
        mILocationListener = iLocationListener;
    }


    @Override
    public boolean isLocation() {
        return locationClient != null;
    }


    @Override
    public void onLocationChanged(AMapLocation location) {

        log.debug("定到位置 {}, Satellites[{}]", location.getProvider(), location.getSatellites());
        log.debug("位置信息 lat {},lon {}", location.getLatitude(), location.getLongitude());

        if (GPSdataTime < 2) {
            GPSdataTime++;
            return;
        }
        cur_locatopn = location;
        // m每秒转换成千米每小时
        if (location.hasSpeed() && location.getSpeed() > 0)
            location.setSpeed(location.getSpeed() * 36 / 10);
        if (isFirstLoc) {
            if (checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.addGpsStatusListener(getGpsStatuslistener);
            }
            LocationUtils.getInstance(mContext).setCurrentCity(location.getCity());
            LocationUtils.getInstance(mContext).setCurrentLocation(location.getLatitude(), location.getLongitude());
            last_Location = location;
            isFirstLoc = false;
        }

        if (mILocationListener != null && location != null) {
            mILocationListener.onLocationResult(location);
        }

        handlerGPS(location);
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
                    isAvalable = true;
                    unAvalableList.clear();
                    log.debug("gps第二阶段过滤成功");
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
                    log.debug("未通过第二阶段或第三阶段过滤");
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
                log.debug("gps通过过滤");
                last_Location = location;
                LocationInfo locationInfo = new LocationInfo(location);
                if (mILocationListener != null) {
                    mILocationListener.onLocationResult(locationInfo);
                }
                unAvalableList.clear();

            }

        } else {

            log.debug("gps未通过过滤");
        }
    }


}
