package com.dudu.map;


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
import com.dudu.event.LocationChangeEvent;
import com.dudu.utils.LocationUtils;


import de.greenrobot.event.EventBus;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by Administrator on 2015/11/25.
 * 监听地图改变的类
 */
public class AMapLocationHandler implements AMapLocationListener {
    private static Context mContext;
    private LocationManagerProxy locationManagerProxy;
    private LocationManager locationManager;
    private static AMapLocationHandler mInstance;

    public static AMapLocationHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AMapLocationHandler();
        }
        mContext = context;
        return mInstance;
    }

    public void init() {
        locationManagerProxy = LocationManagerProxy.getInstance(mContext);

        locationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 3000, 10, this);//定位

        if (checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locationManager.addGpsStatusListener(new GPSListener());
    }

    /**
     * 定位改变回调的方法
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //纬度
        double latitude = aMapLocation.getLatitude();
        //经度
        double longitude = aMapLocation.getLongitude();
        /**
         * 将定位到的经度和纬度存储到sharedPreferences中
         * */
        LocationUtils.getInstance(mContext).setCurrentCoordinate(latitude, longitude);
        EventBus.getDefault().post(new LocationChangeEvent(aMapLocation));
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

    private class GPSListener implements GpsStatus.Listener {

        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:

                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                    EventBus.getDefault().post(locationManager.getGpsStatus(null));

                    break;
                // 定位启动
                case GpsStatus.GPS_EVENT_STARTED:

                    break;
                // 定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    break;
            }
        }
    }

}
