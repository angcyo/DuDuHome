package com.dudu.rest.common;

import com.dudu.rest.service.GuardService;
import com.dudu.rest.service.RobberyService;

/**
 * Created by Administrator on 2016/2/15.
 */
public class Request {
    private static Request mInstance = new Request();

    private static RetrofitClient mClient;

    private RobberyService mRobberyService;
    private GuardService mGuardService;

    public static Request getInstance() {
        return mInstance;
    }

    private Request() {
    }

    public void init(){
        mClient = new RetrofitClient();
        mRobberyService = mClient.getRetrofit().create(RobberyService.class);
        mGuardService = mClient.getRetrofit().create(GuardService.class);
    }

    public RobberyService getRobberyService() {
        return mRobberyService;
    }

    public GuardService getGuardService() {
        return mGuardService;
    }
}

