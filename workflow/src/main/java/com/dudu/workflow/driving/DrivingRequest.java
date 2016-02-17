package com.dudu.workflow.driving;

/**
 * Created by Administrator on 2016/2/17.
 */
public interface DrivingRequest {

    public void pushAcceleratedTestData(double time,RequesetCallback callback);
    public interface RequesetCallback{
        public void requestSuccess(boolean success);
    }
}
