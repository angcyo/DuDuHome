package com.dudu.network.valueobject;

/**
 * Created by dengjun on 2015/11/27.
 * Description :
 */
public class ConnectionParam {
    private String host = "119.29.65.127";
    private int port = 8888;
    private int connectTimeout = 30*1000;

    public ConnectionParam() {
    }

    public ConnectionParam(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
}
