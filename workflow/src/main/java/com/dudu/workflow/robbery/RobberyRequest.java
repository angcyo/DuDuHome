package com.dudu.workflow.robbery;

/**
 * Created by Administrator on 2016/2/16.
 */
public interface RobberyRequest {
    public void getCarInsuranceAuthState(String cellphone);
    public void requestCarInsuranceAuth();
    public void isCarRobbed(String cellphone, CarRobberdCallback callback);
    public void getRobberyState(String cellphone,RobberStateCallback callback);
    public void settingAntiRobberyMode(String cellphone,int type, int on_off,SwitchCallback callback);
    public void closeAntiRobberyMode(String cellphone, CloseRobberyModeCallback callback);

    public interface CarRobberdCallback{
        void hasRobbed(boolean success);
        void requestError(String error);
    }

    public interface SwitchCallback{
        void switchSuccess(boolean success);
        void requestError(String error);
    }

    public interface RobberStateCallback{
        void switchsState(boolean flashRateTimes,boolean emergencyCutoff,boolean stepOnTheGas);
        void requestError(String error);
    }

    public interface CloseRobberyModeCallback{
        void closeSuccess(boolean success);
        void requestError(String error);
    }
}
