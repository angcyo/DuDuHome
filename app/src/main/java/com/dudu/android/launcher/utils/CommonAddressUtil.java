package com.dudu.android.launcher.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pc on 2015/11/2.
 */
public class CommonAddressUtil {

    private static SharedPreferences addressPreferences = null;

    private static SharedPreferences.Editor editor = null;

    private static final String ADDRESS_PREFERENCE = "ADDRESS_PREFERENCE";

    private static final String HOME_ADDRESS = "HOME_ADDRESS";

    private static final String HOMETOWN_ADDRESS = "HOMETOWN_ADDRESS";

    private static final String COMPANY_ADDRESS = "COMPANY_ADDRESS";

    private static final String HOME_LAT = "HOME_LAT";

    private static final String HOME_LON = "HOME_LON";

    private static final String HOMETOWN_LAT = "HOMETOWN_LAT";

    private static final String HOMETOWN_LON = "HOMETOWN_LON";

    private static final String COMPANY_LAT = "COMPANY_LAT";

    private static final String COMPANY_LON = "COMPANY_LON";

    public static final String HOME = "家";

    public static final String HOMETOWN = "老家";

    public static final String COMPANY = "公司";

    /**
     * 获取家地址的经纬度
     * @param context
     * @return
     */
    public static double[] getHomeAddress(Context context){

        addressPreferences =  context.getSharedPreferences(ADDRESS_PREFERENCE,Context.MODE_APPEND);
        String latStr = addressPreferences.getString(HOME_LAT,"-90");
        String lonStr = addressPreferences.getString(HOME_LON,"-90");
        double lat = Double.parseDouble(latStr);
        double lon = Double.parseDouble(lonStr);

        return new double[]{lat,lon};

    }


    /**
     * 获取老家地址的经纬度
     * @param context
     * @return
     */
    public static double[] getHometownAddress(Context context){

        addressPreferences =  context.getSharedPreferences(ADDRESS_PREFERENCE,Context.MODE_APPEND);
        String latStr = addressPreferences.getString(HOMETOWN_LAT,"-90");
        String lonStr = addressPreferences.getString(HOMETOWN_LON,"-90");
        double lat = Double.parseDouble(latStr);
        double lon = Double.parseDouble(lonStr);

        return new double[]{lat,lon};

    }

    /**
     * 获取公司地址的经纬度
     * @param context
     * @return
     */
    public static double[] getCompanyAddress(Context context){

        addressPreferences =  context.getSharedPreferences(ADDRESS_PREFERENCE,Context.MODE_APPEND);
        String latStr = addressPreferences.getString(COMPANY_LAT,"-90");
        String lonStr = addressPreferences.getString(COMPANY_LON,"-90");
        double lat = Double.parseDouble(latStr);
        double lon = Double.parseDouble(lonStr);

        return new double[]{lat,lon};
    }

    /**
     * 获取家地址
     * @param context
     * @return
     */
    public static String getHome(Context context){

        addressPreferences =  context.getSharedPreferences(ADDRESS_PREFERENCE,Context.MODE_APPEND);

        return addressPreferences.getString(HOME_ADDRESS,"");
    }

    /**
     * 获取老家地址
     * @param context
     * @return
     */
    public static String getHometown(Context context){

        addressPreferences =  context.getSharedPreferences(ADDRESS_PREFERENCE,Context.MODE_APPEND);

        return addressPreferences.getString(HOMETOWN_ADDRESS,"");
    }

    /**
     * 获取公司地址
     * @param context
     * @return
     */
    public static String getCompany(Context context){

        addressPreferences =  context.getSharedPreferences(ADDRESS_PREFERENCE,Context.MODE_APPEND);

        return addressPreferences.getString(COMPANY_ADDRESS,"");
    }


    /**
     * 保存家地址的经纬度
     * @param context
     */
    public static void setHomeAddress(Context context, double lat,double lon){

        editor = context.getSharedPreferences(ADDRESS_PREFERENCE,Context.MODE_APPEND).edit();
        editor.putString(HOME_LAT,lat+"");
        editor.putString(HOME_LON,lon+"");
        editor.commit();
    }


    /**
     * 保存老家地址的经纬度
     * @param context
     */
    public static void setHometownAddress(Context context, double lat,double lon){

        editor = context.getSharedPreferences(ADDRESS_PREFERENCE,Context.MODE_APPEND).edit();
        editor.putString(HOMETOWN_LAT,lat+"");
        editor.putString(HOMETOWN_LON,lon+"");
        editor.commit();
    }

    /**
     * 保存家地址的经纬度
     * @param context
     */
    public static void setCompanyAddress(Context context, double lat,double lon){

        editor = context.getSharedPreferences(ADDRESS_PREFERENCE,Context.MODE_APPEND).edit();
        editor.putString(COMPANY_LAT,lat+"");
        editor.putString(COMPANY_LON,lon+"");
        editor.commit();
    }

    /**
     * 保存家地址
     * @param context
     * @param address
     */
    public static void setHome(Context context,String address){

        editor = context.getSharedPreferences(ADDRESS_PREFERENCE,Context.MODE_APPEND).edit();
        editor.putString(HOME_ADDRESS,address).commit();
    }


    /**
     *
     * @param context
     * @param address
     */
    public static void setHometown(Context context,String address){

        editor = context.getSharedPreferences(ADDRESS_PREFERENCE,Context.MODE_APPEND).edit();
        editor.putString(HOMETOWN_ADDRESS,address).commit();

    }

    /**
     *
     * @param context
     * @param address
     */
    public static void setCompany(Context context,String address){

        editor = context.getSharedPreferences(ADDRESS_PREFERENCE,Context.MODE_APPEND).edit();
        editor.putString(COMPANY_ADDRESS,address).commit();
    }

    /**
     * 保存地址
     * @param type
     * @param context
     * @param address
     */
    public static void setCommonAddress(String type,Context context,String address){

        switch (type){

            case HOME:
                setHome(context,address);
                break;
            case HOMETOWN:
                setHometown(context,address);
                break;
            case COMPANY:
                setCompany(context,address);
                break;

        }

    }

    public static void setCommonLocation(String type,Context context,double lat,double lon){

        switch (type){

            case HOME:
                setHomeAddress(context, lat,lon);
                break;
            case HOMETOWN:
                setHometownAddress(context, lat,lon);
                break;
            case COMPANY:
                setCompanyAddress(context,lat,lon);
                break;

        }
    }

}
