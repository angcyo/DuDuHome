package com.dudu.network.valueobject;

/**
 * Created by dengjun on 2015/11/27.
 * Description :
 */
public class ConnectionState {
    public static final int CONNECTION_SUCCESS = 0;//连接成功
    public static final int CONNECTION_CREATE= 1;//连接创建
    public static final int CONNECTION_FAIL= 2;//连接失败
//    public static final int CONNECTION_OPEND = 3;
    public static final int CONNECTION_IDLE = 4;//连接休眠

    public int connectionState;

    public ConnectionState(int connectionState) {
        this.connectionState = connectionState;
    }
}
