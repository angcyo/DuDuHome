package com.dudu.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/2/15.
 */
public class RequestResponse {
    /**
     * 0	操作成功
     * 50007	method参数为NULL
     * 50008	messageId参数为NULL
     * 50003	不合法的Token
     * 50012	手机号格式错误
     */
    @SerializedName("resultCode")
    private long resultCode;

}
