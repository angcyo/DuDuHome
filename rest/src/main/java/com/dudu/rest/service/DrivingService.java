package com.dudu.rest.service;

import com.dudu.rest.model.RequestResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Administrator on 2016/2/17.
 */
public interface DrivingService {

    @GET("/external/mirror/acceleratedTest/{cellphone}/{json}")
    public Call<RequestResponse> pushAcceleratedTestData(@Path("cellphone") String cellphone, @Path("json")String json);

    @GET("/external/mirror/drivingHabits/{cellphone}/{json}")
    public Call<RequestResponse> pushDrivingHabits(@Path("cellphone") String cellphone, @Path("json")String json);
}
