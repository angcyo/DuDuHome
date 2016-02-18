package com.dudu.workflow.driving;

import com.dudu.rest.model.AccTestData;

/**
 * Created by Administrator on 2016/2/17.
 */
public interface DrivingRequest {

    public void pushAcceleratedTestData(AccTestData time, RequesetCallback callback);
    public interface RequesetCallback{
        public void requestSuccess(boolean success);
    }
}
