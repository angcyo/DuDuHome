package com.dudu.weather;


import com.dudu.android.launcher.utils.WeatherUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Jervis on 2015/9/15.
 */
public class WeatherChinaGlobal {

    private WeatherChinaGlobalToday now;

    private List<WeatherChinaGlobalForecast> daily_forecast;

    private String area;

    public WeatherChinaGlobalToday getNow() {
        return now;
    }

    public void setNow(WeatherChinaGlobalToday now) {
        this.now = now;
    }

    public List<WeatherChinaGlobalForecast> getDailyForecast() {
        return daily_forecast;
    }

    public void setDailyForecast(List<WeatherChinaGlobalForecast> dailyForecast) {
        this.daily_forecast = dailyForecast;
    }

    public String getSpeakWord(String time) {

        int witchDay = getWitchDay(time);

        if (witchDay == 0) {
            now.setArea(area);
            return now.getSpeakWord();
        } else {
            daily_forecast.get(witchDay).setArea(area);
            return daily_forecast.get(witchDay).getSpeakWord(witchDay);
        }
    }

    private int getWitchDay(String time) {
        Date todayDate = new Date();
        Date queryDate = null;
        try {
            queryDate = new SimpleDateFormat("yyyyMMdd").parse(time);
        } catch (ParseException e) {

        }

        return WeatherUtils.daysBetween(todayDate, queryDate);
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Object getWeatherObj(String time) {

        int witchDay = getWitchDay(time);
        if (witchDay == 2) {
            WeatherChinaGlobalForecast weather = daily_forecast.get(2);
            weather.setDay("后天");
            return weather;
        } else if (witchDay == 1) {
            WeatherChinaGlobalForecast weather = daily_forecast.get(1);
            weather.setDay("明天");
            return weather;
        } else {
            return now;
        }
    }
}
