package com.dudu.workflow;

import com.dudu.workflow.driving.DrivingFlow;

/**
 * Created by Administrator on 2016/2/17.
 */
public class FlowFactory {
    private static RequestFactory mInstance = new RequestFactory();

    private DrivingFlow drivingFlow;

    public static RequestFactory getInstance(){
        return mInstance;
    }

    public void init(){
        drivingFlow = new DrivingFlow();
        drivingFlow.getReceiveDataFlow();
    }
}
