package com.dudu.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/2/16.
 */
public class GuardStateResponse {
    /*
     * 发送数据时调用的接口名
     */
    @SerializedName("switch")
    public int switchValue;
}
