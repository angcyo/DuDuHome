package com.dudu.commonlib.repo;

/**
 * Created by Administrator on 2016/2/15.
 */
public class Config {

    private boolean isTest;

    private String serverAddress;

    public boolean isTest() {
        return isTest;
    }

    public void setTest(boolean test) {
        isTest = test;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
}
