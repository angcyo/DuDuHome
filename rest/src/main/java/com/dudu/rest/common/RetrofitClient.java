package com.dudu.rest.common;

/**
 * Created by Administrator on 2016/2/15.
 */

import com.dudu.commonlib.xml.ConfigReader;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Eaway on 2016/2/13.
 */
public class RetrofitClient {

    private final Retrofit retrofit;

    public RetrofitClient() {
        String baseUrl = ConfigReader.getInstance().getConfig().getServerAddress();
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

}
