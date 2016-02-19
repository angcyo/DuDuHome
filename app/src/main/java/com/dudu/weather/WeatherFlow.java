package com.dudu.weather;


import com.amap.api.services.weather.LocalWeatherLive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by lxh on 2016/1/22.
 */
public class WeatherFlow {

    private static WeatherFlow mInstance;
    private final Logger mLogger;

    public WeatherFlow() {
        mLogger = LoggerFactory.getLogger("weather.flow");
    }

    private WeatherInfo weatherInfo;

    public WeatherInfo getWeatherInfo() {
        return weatherInfo;
    }

    public static WeatherFlow getInstance() {
        if (mInstance == null) {
            mInstance = new WeatherFlow();
        }
        return mInstance;
    }

    public Observable<WeatherInfo> requestWeather() {
        mLogger.debug("weather-rx:requestWeatherFlow");

        return WeatherStream.getLiveWeatherStream().map(new Func1<LocalWeatherLive, WeatherInfo>() {
            @Override
            public WeatherInfo call(LocalWeatherLive localWeatherLive) {
                weatherInfo = new WeatherInfo(localWeatherLive.getWeather(), localWeatherLive.getTemperature(),
                        localWeatherLive.getWindDirection() + localWeatherLive.getWindPower());
                return weatherInfo;
            }
        });

    }
}
