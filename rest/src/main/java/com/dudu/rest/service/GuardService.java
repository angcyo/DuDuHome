package com.dudu.rest.service;

import com.dudu.rest.model.GuardStateResponse;
import com.dudu.rest.model.RequestResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Administrator on 2016/2/16.
 */
public interface GuardService {

    /**
     * 防盗开关
     * @param cellphone 账号
     * @param on_off  开关
     * @return
     */
    @GET("/external/mirror/theft/{cellphone}/{value}")
    public Call<RequestResponse> guardSwitch(@Path("cellphone") String cellphone, @Path("value")int on_off);

    /**
     * 获取防盗开关状态
     * @param cellphone 账号
     * @return
     */
    @GET("/external/getThelfSwitch/{cellphone}")
    public Call<GuardStateResponse> getGuardState(@Path("cellphone") String cellphone);
}
