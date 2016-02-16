package com.dudu.workflow;

import com.dudu.workflow.guard.GuardRequest;
import com.dudu.workflow.guard.GuardRequestRetrofitImpl;
import com.dudu.workflow.robbery.RobberyRequest;
import com.dudu.workflow.robbery.RobberyRequestRetrofitImpl;

/**
 * Created by Administrator on 2016/2/16.
 */
public class RequestFactory {

    private static RequestFactory mInstance = new RequestFactory();

    private static RobberyRequest robberyRequest;
    private static GuardRequest guardRequest;

    public static RequestFactory getInstance(){
        return mInstance;
    }

    public void init(){
        robberyRequest = RobberyRequestRetrofitImpl.getInstance();
        guardRequest = GuardRequestRetrofitImpl.getInstance();
    }

    public static RobberyRequest getRobberyRequest() {
        return robberyRequest;
    }
    public static GuardRequest getGuardRequest() {
        return guardRequest;
    }
}
