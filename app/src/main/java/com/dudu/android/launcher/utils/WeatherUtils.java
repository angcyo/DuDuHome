package com.dudu.android.launcher.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;
import com.dudu.event.DeviceEvent;
import com.dudu.monitor.Monitor;
import com.dudu.monitor.utils.LocationUtils;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.scf4a.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ch.qos.logback.core.util.LocationUtil;
import de.greenrobot.event.EventBus;

public class WeatherUtils {

    private static final String TAG = "WeatherUtils";

    private final static String[] WEATHER_STRINGS = new String[]{"晴", "多云",
            "阴", "阵雨", "雷阵雨", "小雨", "中雨", "大雨", "暴雨", "大暴雨", "特大暴雨", "小雪",
            "中雪", "大雪", "阵雪", "暴雪", "雾", "小雨转中雨", "中雨转大雨", "大雨转暴雨", "暴雨转大暴雨",
            "大暴雨转特大暴雨", "小雪转中雪", "中雪转大雪", "大雪转暴雪"};

    private static List<String> weatherList = new ArrayList<String>();

    static {
        weatherList = Arrays.asList(WEATHER_STRINGS);
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

    public static void requestWeather(Context context) {
        String currentCity = getCurrentCity(context);
        if (currentCity != null) {
            LogUtils.v("weather", "获取当前的额城市为：" + currentCity);
            WeatherSearchQuery mQuery = new WeatherSearchQuery(currentCity, WeatherSearchQuery.WEATHER_TYPE_LIVE);
            WeatherSearch mSearch = new WeatherSearch(context);
            mSearch.setOnWeatherSearchListener(new MyWeatherSearchListener());
            mSearch.setQuery(mQuery);
            mSearch.searchWeatherAsyn(); //异步搜索
        }

    }

    private static class MyWeatherSearchListener implements WeatherSearch.OnWeatherSearchListener {
        @Override
        public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
            if (i == 0) {
                if (localWeatherLiveResult != null && localWeatherLiveResult.getLiveResult() != null) {
                    LocalWeatherLive weatherLive = localWeatherLiveResult.getLiveResult();
                    String weather = weatherLive.getWeather();
                    String temperature = weatherLive.getTemperature();
                    String wind = weatherLive.getWindDirection() + "风" + weatherLive.getWindPower() + "级";
                    String weatherText = weather + "\n温度" + temperature + "℃\n" + wind;
                    LocationUtils.getInstance(LauncherApplication.getContext()).setCurrentCityWeather(weatherText);
                    EventBus.getDefault().post(new DeviceEvent.Weather(weather, temperature));
                } else {
                    LogUtils.v("weather", "获取天气失败...");
                }
            } else {
                LogUtils.v("weather", "获取天气失败..");
            }
        }


        @Override
        public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {
            if (i == 0) {
                if (localWeatherForecastResult != null && localWeatherForecastResult.getWeatherForecastQuery() != null) {
                    LocalWeatherForecast mForecast = localWeatherForecastResult.getForecastResult();
                    List<LocalDayWeatherForecast> list = mForecast.getWeatherForecast();
                    for (LocalDayWeatherForecast forecast : list) {
                        LogUtils.v("weather", "date:" + forecast.getDate());
                        LogUtils.v("weather", "tem:" + forecast.getDayTemp() + "-" + forecast.getNightTemp());
                    }
                } else {
                    LogUtils.v("weather", "获取天气失败...");
                }
            } else {
                LogUtils.v("weather", "获取天气失败..");
            }
        }
    }

    public static String getCurrentCity(Context context) {
        String currentCity = null;
        if (Monitor.getInstance(context).getCurrentLocation() != null) {
            currentCity = Monitor.getInstance(context).getCurrentLocation().getCity();
        }

        return currentCity;
    }
}
