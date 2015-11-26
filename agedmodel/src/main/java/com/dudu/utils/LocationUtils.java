package com.dudu.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.amap.api.location.LocationManagerProxy;

/**
 * Created by Administrator on 2015/11/25.
 */
public class LocationUtils {
    private static LocationUtils mInstance;
    private static SharedPreferences sharedPreferences;
    private static Context mContext;
    public static final String LOCATION_SPF = "LOCATION_SPF";
    public static final String CURRENT_LATITUDE = "CURRENT_LATITUDE";
    public static final String CURRENT_LONGITUDE = "CURRENT_LONGITUDE";
    public static final String CURRENT_CITY = "CURRENT_CITY";
    public static final String CURRENT_CITYCODE = "CURRENT_CITYCODE";
    public static LocationUtils getInstance(Context context){
        if(mInstance==null){
            mInstance=new LocationUtils();
        }
        if(sharedPreferences==null){
            sharedPreferences=context.getSharedPreferences(LOCATION_SPF,Context.MODE_PRIVATE);
            mContext=context;
        }
        return mInstance;
    }
    /**
     * 保存当前的城市
     * */
    public void setCurrentCity(String currentCity){
        if(sharedPreferences==null){
            sharedPreferences=mContext.getSharedPreferences(LOCATION_SPF,Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(CURRENT_CITY,currentCity);
        editor.commit();
    }
    /**
     * 获得当前的城市
     * */
    public String getCurrentCity(){
        if(sharedPreferences==null){
            sharedPreferences=mContext.getSharedPreferences(LOCATION_SPF,Context.MODE_PRIVATE);
        }
        return  sharedPreferences.getString(CURRENT_CITY,"深圳");
    }
    /**
     * 保存当前的纬度和经度
     * */
    public void setCurrentCoordinate(double latitude,double longitude){
        if(sharedPreferences==null){
            sharedPreferences=mContext.getSharedPreferences(LOCATION_SPF,Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(CURRENT_LATITUDE,latitude+"");
        editor.putString(CURRENT_LONGITUDE,longitude+"");
        editor.commit();
    }
    /**
     * 获得当前的额坐标
     * */
    public double[] getCurrentCoordinate(){
        if(sharedPreferences==null){
            sharedPreferences=mContext.getSharedPreferences(LOCATION_SPF,Context.MODE_PRIVATE);
        }
        double latitude=Double.parseDouble(sharedPreferences.getString(CURRENT_LATITUDE,"0"));
        double longitude=Double.parseDouble(sharedPreferences.getString(CURRENT_LONGITUDE,"0"));
        double [] location={latitude,longitude};
        return location;
    }
    /**
     * 获取当前定位的cityCode
     * @return
     */
    public String getCurrentCityCode(){
        if(sharedPreferences==null)
            sharedPreferences =  mContext.getSharedPreferences(LOCATION_SPF, Context.MODE_PRIVATE);
        return sharedPreferences.getString(CURRENT_CITYCODE,"");
    }

    public void setCurrentCitycode(String citycode){
        if(sharedPreferences==null)
            sharedPreferences =  mContext.getSharedPreferences(LOCATION_SPF, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(CURRENT_CITYCODE,citycode).commit();
    }
}
