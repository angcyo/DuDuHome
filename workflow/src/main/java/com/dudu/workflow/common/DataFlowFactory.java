package com.dudu.workflow.common;

import com.dudu.persistence.switchmessage.RealmSwitchMessageService;
import com.dudu.persistence.user.RealmUserDataService;
import com.dudu.workflow.switchmessage.SwitchDataFlow;
import com.dudu.workflow.user.UserFlow;

/**
 * Created by Administrator on 2016/2/17.
 */
public class DataFlowFactory {
    private static DataFlowFactory mInstance = new DataFlowFactory();
    private static UserFlow userFlow;
    private static SwitchDataFlow switchDataFlow;

    public static DataFlowFactory getInstance(){
        return mInstance;
    }

    public void init(){
        userFlow = new UserFlow(new RealmUserDataService());
        switchDataFlow = new SwitchDataFlow(new RealmSwitchMessageService());

    }

    public static UserFlow getUserDataFlow() {
        return userFlow;
    }

    public static SwitchDataFlow getSwitchDataFlow() {
        return switchDataFlow;
    }
}
