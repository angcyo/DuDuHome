package com.dudu.android.launcher.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.LocationManagerProxy;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;
import com.dudu.event.DeviceEvent;
import com.dudu.monitor.utils.LocationUtils;

import de.greenrobot.event.EventBus;

public class WeatherUtil {

    private static final String TAG = "WeatherUtil";

    private final static String[] WEATHER_STRINGS = new String[]{"晴", "多云",
            "阴", "阵雨", "雷阵雨", "小雨", "中雨", "大雨", "暴雨", "大暴雨", "特大暴雨", "小雪",
            "中雪", "大雪", "阵雪", "暴雪", "雾", "小雨转中雨", "中雨转大雨", "大雨转暴雨", "暴雨转大暴雨",
            "大暴雨转特大暴雨", "小雪转中雪", "中雪转大雪", "大雪转暴雪"};

    private static List<String> weatherList = new ArrayList<String>();

    static {
        weatherList = Arrays.asList(WEATHER_STRINGS);
    }

    public static void requestWeatherInfo() {
        LocationManagerProxy.getInstance(LauncherApplication.getContext()).requestWeatherUpdates(
                LocationManagerProxy.WEATHER_TYPE_LIVE, new AMapLocalWeatherListener() {
                    @Override
                    public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {
                        if (aMapLocalWeatherLive != null
                                && aMapLocalWeatherLive.getAMapException().getErrorCode() == 0) {
                           /* String weather = aMapLocalWeatherLive.getWeather();

                            String temperature = aMapLocalWeatherLive.getTemperature();*/

                            /*EventBus.getDefault().post(new DeviceEvent.Weather(weather, temperature));*/

                            LocationUtils.getInstance(LauncherApplication.getContext()).setCurrentCity(
                                    aMapLocalWeatherLive.getCity());
                            LocationUtils.getInstance(LauncherApplication.getContext()).setCurrentCitycode(
                                    aMapLocalWeatherLive.getCityCode());
                        } else {
                            EventBus.getDefault().post(new DeviceEvent.Weather("", ""));
                        }
                    }

                    @Override
                    public void onWeatherForecaseSearched(AMapLocalWeatherForecast aMapLocalWeatherForecast) {

                    }
                });
    }

    public static boolean isNight(long time) {
        SimpleDateFormat df = new SimpleDateFormat("HH", Locale.getDefault());
        String timeStr = df.format(new Date(System.currentTimeMillis()));
        try {
            int timeHour = Integer.parseInt(timeStr);
            return (timeHour >= 18 || timeHour <= 6);
        } catch (NumberFormatException e) {
            Log.v(TAG, e.getMessage() + "");
        }
        return false;
    }

    public static int getWeatherType(String weather) {
        if (TextUtils.isEmpty(weather)) {
            return Constants.NO_VALUE_FLAG;
        }

        int type = weatherList.indexOf(weather);
        if (type == -1) {
            return Constants.NO_VALUE_FLAG;
        }

        return type;
    }

    public static int getWeatherIcon(int type) {
        if (isNight(System.currentTimeMillis()))
            switch (type) {
                case Constants.SUNNY:
                    return R.drawable.weather_night_sunny;
                case Constants.CLOUDY:
                    return R.drawable.weather_night_cloudy;
                case Constants.LIGHT_RAIN:
                case Constants.MODERATE_RAIN:
                case Constants.HEAVY_RAIN:
                case Constants.SHOWER:
                case Constants.STORM:
                    return R.drawable.weather_night_rain;
                default:
                    break;
            }

        switch (type) {
            case Constants.SUNNY:
                return R.drawable.weather_sunny;
            case Constants.CLOUDY:
                return R.drawable.weather_cloudy;
            case Constants.OVERCAST:
                return R.drawable.weather_overcast;
            case Constants.SHOWER:
                return R.drawable.weather_shower;
            case Constants.THUNDERSHOWER:
                return R.drawable.weather_thunder_shower;
            case Constants.LIGHT_RAIN:
            case Constants.MODERATE_RAIN:
            case Constants.HEAVY_RAIN:
            case Constants.LIGHT_TO_MODERATE_RAIN:
            case Constants.MODERATE_TO_HEAVY_RAIN:
            case Constants.RAIN_TO_STORM:
                return R.drawable.weather_rain;
            case Constants.STORM:
            case Constants.HEAVY_STORM:
            case Constants.SEVERE_STORM:
            case Constants.STORM_TO_HEAVY_STORM:
            case Constants.HEAVY_TO_SEVERE_STORM:
                return R.drawable.weather_storm;
            case Constants.LIGHT_SNOW:
            case Constants.MODERATE_SNOW:
            case Constants.HEAVY_SNOW:
            case Constants.LIGHT_TO_MODERATE_SNOW:
            case Constants.MODERATE_TO_HEAVY_SNOW:
            case Constants.HEAVY_TO_SNOWSTORM:
                return R.drawable.weather_snow;
            case Constants.SNOWSTORM:
                return R.drawable.weather_snow_storm;
            case Constants.SNOW_SHOWER:
                return R.drawable.weather_snow_shower;
            case Constants.FOGGY:
                return R.drawable.weather_foggy;
            default:
                return R.drawable.weather_cloudy;
        }
    }
}
