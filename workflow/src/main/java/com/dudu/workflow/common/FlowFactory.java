package com.dudu.workflow.common;

import com.dudu.persistence.user.RealmUserDataService;
import com.dudu.workflow.driving.DrivingFlow;
import com.dudu.workflow.user.UserFlow;

/**
 * Created by Administrator on 2016/2/17.
 */
public class FlowFactory {
    private static FlowFactory mInstance = new FlowFactory();
    private static UserFlow userFlow;

    private DrivingFlow drivingFlow;

    public static FlowFactory getInstance(){
        return mInstance;
    }

    public void init(){
        drivingFlow = new DrivingFlow();
        drivingFlow.getReceiveDataFlow();
        userFlow = new UserFlow(new RealmUserDataService());
    }

    public static UserFlow getUserDataFlow() {
        return userFlow;
    }
}
