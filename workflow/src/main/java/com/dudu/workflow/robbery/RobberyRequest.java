package com.dudu.workflow.robbery;

/**
 * Created by Administrator on 2016/2/16.
 */
public interface RobberyRequest {

    public void getCarInsuranceAuthState();
    public void requestCarInsuranceAuth();
    public void isCarRobbed(CarRobberdCallback callback);
    public void getRobberyState(RobberStateCallback callback);
    public void settingAntiRobberyMode(int type, int on_off,SwitchCallback callback);
    public void closeAntiRobberyMode(CloseRobberyModeCallback callback);

    public interface CarRobberdCallback{
        void hasRobbed(boolean robbed);
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
