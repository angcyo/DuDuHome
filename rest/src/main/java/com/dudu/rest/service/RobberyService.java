package com.dudu.rest.service;

import com.dudu.rest.model.QueryRobberyResponse;
import com.dudu.rest.model.RequestResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Administrator on 2016/2/15.
 */
public interface RobberyService {

    /**
     * 防劫相关的开关
     * @param cellphone 账号
     * @param type 类型：0.防劫模式开关，1.闪大灯10次；2.紧急停车后下车；3.连踩油门3次
     * @param on_off 1.开；0.关
     * @return
     */
    @GET("/external/mirror/robbery/{cellphone}/{type}/{value}")
    public Call<RequestResponse> robberySwitch(@Path("cellphone") String cellphone, @Path("type")int type, @Path("value")int on_off);

    /**
     * 获取防劫各个开关的状态
     * @param cellphone
     * @return
     */
    @GET("/external/getALLRobberySwitch/{cellphone}")
    public Call<QueryRobberyResponse> getRobberyState(@Path("cellphone")String cellphone);
}
