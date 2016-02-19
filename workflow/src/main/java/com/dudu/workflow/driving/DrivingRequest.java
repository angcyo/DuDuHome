package com.dudu.workflow.driving;

import com.dudu.rest.model.AccTestData;
import com.dudu.rest.model.DrivingHabitsData;

/**
 * 驾车相关请求
 * Created by Eaway on 2016/2/17.
 */
public interface DrivingRequest {

    /**
     * 发送加速测试数据
     *
     * @param accTestData
     * @param callback
     */
    public void pushAcceleratedTestData(AccTestData accTestData, RequesetCallback callback);

    /**
     * 发送驾驶习惯数据
     *
     * @param drivingHabitsData
     * @param callback
     */
    public void pushDrivingHabitsData(DrivingHabitsData drivingHabitsData, RequesetCallback callback);

    public interface RequesetCallback{
        public void requestSuccess(boolean success);
    }
}
