package com.dudu.workflow.common;

import com.dudu.commonlib.repo.ReceiverData;

/**
 * Created by Administrator on 2016/2/22.
 */
public class ReceiverDataFlow {

    public static boolean getGuardReceiveData(ReceiverData receiverData) {
        return receiverData.getTitle().equals(ReceiverData.THEFT_VALUE);
    }

    public static void saveGuardReceiveData(ReceiverData receiverData) {
        if (getGuardReceiveData(receiverData)) {
            DataFlowFactory.getSwitchDataFlow().saveRobberyState(receiverData.getSwitchValue().equals("1"));
        }
    }

    public static boolean getRobberyReceiveData(ReceiverData receiverData) {
        return receiverData.getTitle().equals(ReceiverData.ROBBERY_VALUE);
    }

    public static void saveRobberyReceiveData(ReceiverData receiverData){
        if (getRobberyReceiveData(receiverData)) {
            DataFlowFactory.getSwitchDataFlow().saveRobberyState(receiverData.getSwitch0Value().equals("1"));
            DataFlowFactory.getSwitchDataFlow().saveRobberyState(receiverData.getSwitch1Value().equals("1"));
            DataFlowFactory.getSwitchDataFlow().saveRobberyState(receiverData.getSwitch2Value().equals("1"));
            DataFlowFactory.getSwitchDataFlow().saveRobberyState(receiverData.getSwitch3Value().equals("1"));
        }
    }

}
