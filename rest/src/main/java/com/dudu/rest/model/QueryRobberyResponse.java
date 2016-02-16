package com.dudu.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/2/15.
 */
public class QueryRobberyResponse{

    /**
     * 防劫模式开关:0.关闭；1.开启
     */
    @SerializedName("switch0")
    public long switch0;
    /**
     * 限制闪大灯10次:0.关闭；1.开启
     */
    @SerializedName("switch1")
    public long switch1;
    /**
     * 紧急停车后下车:0.关闭；1.开启
     */
    @SerializedName("switch2")
    public long switch2;
    /**
     * 连踩油门限制3次:0.关闭；1.开启
     */
    @SerializedName("switch3")
    public long switch3;
}
