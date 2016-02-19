package com.dudu.weather;

import android.text.TextUtils;

import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.monitor.Monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.subjects.ReplaySubject;

/**
 * Created by lxh on 2016/1/22.
 */
public class WeatherStream {

    private static WeatherStream mInstance;
    private final Logger mLogger;

    private ScheduledExecutorService requestWeatherExecutor;

    private static ReplaySubject<LocalWeatherLive> mLiveWeatherSubject = ReplaySubject.create();

    private static ReplaySubject<WeatherInfo> mLocalWeatherForecastSub = ReplaySubject.create();

    private WeatherSearch mSearch;

    private boolean hasWeather = false;

    public WeatherStream() {
        mLogger = LoggerFactory.getLogger("weather.flow");
        mSearch = new WeatherSearch(LauncherApplication.getContext());
    }

    public static Observable<LocalWeatherLive> getLiveWeatherStream() {
        return mLiveWeatherSubject.asObservable();
    }

    public static Observable<WeatherInfo> getForecastWeather() {
        return mLocalWeatherForecastSub.asObservable();
    }

    public static WeatherStream getInstance() {
        if (mInstance == null) {
            mInstance = new WeatherStream();
        }
        return mInstance;
    }

    private Thread requeatWeatherThread = new Thread() {

        @Override
        public void run() {
            weatherLiveFlow();
        }
    };

    public void startService() {
        if (requestWeatherExecutor == null)
            requestWeatherExecutor = Executors.newScheduledThreadPool(1);
        requestWeatherExecutor.scheduleAtFixedRate(requeatWeatherThread, 20, 30*60, TimeUnit.SECONDS);
    }

    private void weatherLiveFlow() {
        mLogger.debug("weather-rx:doOnNext");

        Observable.timer(20, TimeUnit.SECONDS).subscribe(aLong -> {

            if (!hasWeather) {
                weatherLiveFlow();
            }
        });

        if (TextUtils.isEmpty(getCurrentCity())) {
            return;
        }
        WeatherSearchQuery mLiveQuery = new WeatherSearchQuery(getCurrentCity(), WeatherSearchQuery.WEATHER_TYPE_LIVE);
        mSearch.setOnWeatherSearchListener(mWeatherListener);
        mSearch.setQuery(mLiveQuery);
        mSearch.searchWeatherAsyn();


    }

    private WeatherSearch.OnWeatherSearchListener mWeatherListener = new WeatherSearch.OnWeatherSearchListener() {
        @Override
        public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
            if (i == 0) {
                mLogger.debug("weather-rx onWeatherLiveSearched ");
                hasWeather = true;
                mLiveWeatherSubject.onNext(localWeatherLiveResult.getLiveResult());
            }
        }

        @Override
        public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

        }
    };

    private String getCurrentCity() {

        if (Monitor.getInstance(LauncherApplication.getContext()).getCurrentLocation() != null) {
            return Monitor.getInstance(LauncherApplication.getContext()).getCurrentLocation().getCity();
        }
        return null;
    }

}