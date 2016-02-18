package com.dudu.workflow;

import com.dudu.workflow.driving.DrivingFlow;

/**
 * Created by Administrator on 2016/2/17.
 */
public class FlowFactory {
    private static FlowFactory mInstance = new FlowFactory();

    private DrivingFlow drivingFlow;

    public static FlowFactory getInstance(){
        return mInstance;
    }

    public void init(){
        drivingFlow = new DrivingFlow();
        drivingFlow.getReceiveDataFlow();
    }
}
