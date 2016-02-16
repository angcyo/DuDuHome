package com.dudu.workflow;

import com.dudu.workflow.robbery.RobberyRequest;
import com.dudu.workflow.robbery.RobberyRequestRetrofitImpl;

/**
 * Created by Administrator on 2016/2/16.
 */
public class RequestFactory {

    private static RequestFactory mInstance = new RequestFactory();

    private static RobberyRequest robberyRequest;

    public static RequestFactory getInstance(){
        return mInstance;
    }

    public void init(){
        robberyRequest = RobberyRequestRetrofitImpl.getInstance();
    }

    public static RobberyRequest getRobberyRequest() {
        return robberyRequest;
    }
}
