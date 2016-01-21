package com.dudu.monitor.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LocationUtils {
    private static Context mContext;
    private static SharedPreferences locationPreferences;
    private static LocationUtils mCurrentLocation;
    public static final String LOCATION_SPF = "LOCATION_SPF";
    public static final String CURRENT_LATITUDE = "CURRENT_LATITUDE";
    public static final String CURRENT_LONGITUDE = "CURRENT_LONGITUDE";
    public static final String CURRENT_CITY = "CURRENT_CITY";
    public static final String NAVISTART_LAT = "NAVISTART_LAT";
    public static final String NAVISTART_LON = "NAVISTART_LON";
    public static final String NAVIEND_LAT = "NAVIEND_LAT";
    public static final String NAVIEND_LON = "NAVIEND_LON";
    public static final String CURRENT_CITYCODE = "CURRENT_CITYCODE";
    public static final String CURRENT_CITY_WEATHER = "CURRENT_CITY_WEATHER";

    public static LocationUtils getInstance(Context context) {
        if (mCurrentLocation == null)
            mCurrentLocation = new LocationUtils();
        if (locationPreferences == null)
            locationPreferences = context.getSharedPreferences(LOCATION_SPF, Context.MODE_PRIVATE);
        mContext = context;
        return mCurrentLocation;
    }

    /**
     * 获取当前定位的经纬度
     *
     * @return 返回double数组
     */
    public double[] getCurrentLocation() {
        if (locationPreferences == null)
            locationPreferences = mContext.getSharedPreferences(LOCATION_SPF, Context.MODE_PRIVATE);
        double latitude = Double.parseDouble(locationPreferences.getString(CURRENT_LATITUDE, "0"));
        double longitude = Double.parseDouble(locationPreferences.getString(CURRENT_LONGITUDE, "0"));
        double[] latlon = {latitude, longitude};
        return latlon;
    }

    /**
     * 保存当前定位的经纬度
     *
     * @param latitude
     * @param longitude
     */
    public void setCurrentLocation(double latitude, double longitude) {
        if (locationPreferences == null)
            locationPreferences = mContext.getSharedPreferences(LOCATION_SPF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = locationPreferences.edit();
        editor.putString(CURRENT_LATITUDE, latitude + "");
        editor.putString(CURRENT_LONGITUDE, longitude + "");
        editor.commit();
    }

    /**
     * 获取当前定位的城市
     *
     * @return
     */
    public String getCurrentCity() {
        if (locationPreferences == null)
            locationPreferences = mContext.getSharedPreferences(LOCATION_SPF, Context.MODE_PRIVATE);
        return locationPreferences.getString(CURRENT_CITY, null);
    }

    /**
     * 保存当前定位的城市
     *
     * @param city
     */
    public void setCurrentCity(String city) {
        if (locationPreferences == null)
            locationPreferences = mContext.getSharedPreferences(LOCATION_SPF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = locationPreferences.edit();
        editor.putString(CURRENT_CITY, city);
        editor.commit();
    }

    /**
     * 保存导航的起始点
     *
     * @param
     */
    public void setNaviStartPoint(double lat, double lon) {
        if (locationPreferences == null)
            locationPreferences = mContext.getSharedPreferences(LOCATION_SPF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = locationPreferences.edit();
        editor.putString(NAVISTART_LAT, lat + "");
        editor.putString(NAVISTART_LON, lon + "");
        editor.commit();
    }

    /**
     * 获取导航的起始点
     *
     * @return
     */
    public double[] getNaviStartPoint() {
        if (locationPreferences == null)
            locationPreferences = mContext.getSharedPreferences(LOCATION_SPF, Context.MODE_PRIVATE);
        double latitude = Double.parseDouble(locationPreferences.getString(NAVISTART_LAT, "0"));
        double longitude = Double.parseDouble(locationPreferences.getString(NAVISTART_LON, "0"));
        double[] latlon = {latitude, longitude};
        return latlon;
    }

    /**
     * 保存导航的结束点
     */
    public void setNaviEndPoint(double lat, double lon) {
        if (locationPreferences == null)
            locationPreferences = mContext.getSharedPreferences(LOCATION_SPF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = locationPreferences.edit();
        editor.putString(NAVIEND_LAT, lat + "");
        editor.putString(NAVIEND_LON, lon + "");
        editor.commit();
    }

    /**
     * 获取导航的结束点
     *
     * @return
     */
    public double[] getNaviEndPoint() {
        if (locationPreferences == null)
            locationPreferences = mContext.getSharedPreferences(LOCATION_SPF, Context.MODE_PRIVATE);
        double latitude = Double.parseDouble(locationPreferences.getString(NAVIEND_LAT, "0"));
        double longitude = Double.parseDouble(locationPreferences.getString(NAVIEND_LON, "0"));
        double[] latlon = {latitude, longitude};
        return latlon;
    }

    /**
     * 获取当前定位的cityCode
     *
     * @return
     */
    public String getCurrentCityCode() {
        if (locationPreferences == null)
            locationPreferences = mContext.getSharedPreferences(LOCATION_SPF, Context.MODE_PRIVATE);
        return locationPreferences.getString(CURRENT_CITYCODE, "");
    }

    public void setCurrentCitycode(String citycode) {
        if (locationPreferences == null)
            locationPreferences = mContext.getSharedPreferences(LOCATION_SPF, Context.MODE_PRIVATE);
        locationPreferences.edit().putString(CURRENT_CITYCODE, citycode).commit();
    }

    public void setCurrentCityWeather(String currentCityWeather) {
        if (locationPreferences == null) {
            locationPreferences = mContext.getSharedPreferences(LOCATION_SPF, Context.MODE_PRIVATE);
        }
        locationPreferences.edit().putString(CURRENT_CITY_WEATHER, currentCityWeather).commit();
    }

    public String getCurrentCityWeather() {
        if (locationPreferences == null) {
            locationPreferences = mContext.getSharedPreferences(LOCATION_SPF, Context.MODE_PRIVATE);
        }
        return locationPreferences.getString(CURRENT_CITY_WEATHER, null);
    }
}
