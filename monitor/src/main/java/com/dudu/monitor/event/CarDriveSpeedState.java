package com.dudu.monitor.event;

/**
 * Created by dengjun on 2015/12/3.
 * Description :汽车驾驶速度状态
 *        急加速为1，急减速未2， 发动机转速不匹配为6
 */
public class CarDriveSpeedState {
    //急加速为1，急减速未2， 发动机转速不匹配为6
    int speedState;

    public CarDriveSpeedState(int speedState) {
        this.speedState = speedState;
    }

    public int getSpeedState() {
        return speedState;
    }
}
