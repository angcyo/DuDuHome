package com.dudu.monitor.valueobject;

/**
 * Created by dengjun on 2015/12/8.
 * Description : 流量信息类
 */
public class FlowInfo {
    //总流量
    private float mTotalFlow;
    //剩余流量
    private float remainingFlow;
    //当月已用流量
    private float curMonthUsedFlow;

    public float getmTotalFlow() {
        return mTotalFlow;
    }

    public void setmTotalFlow(float mTotalFlow) {
        this.mTotalFlow = mTotalFlow;
    }

    public float getRemainingFlow() {
        return remainingFlow;
    }

    public void setRemainingFlow(float remainingFlow) {
        this.remainingFlow = remainingFlow;
    }

    public float getCurMonthUsedFlow() {
        return curMonthUsedFlow;
    }

    public void setCurMonthUsedFlow(float curMonthUsedFlow) {
        this.curMonthUsedFlow = curMonthUsedFlow;
    }
}
