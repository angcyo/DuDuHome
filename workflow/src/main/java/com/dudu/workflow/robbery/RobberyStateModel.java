package com.dudu.workflow.robbery;

/**
 * Created by Administrator on 2016/2/22.
 */
public class RobberyStateModel {
    private Boolean robberyState;

    public RobberyStateModel(final Boolean robberyState) {
        this.robberyState = robberyState;
    }

    public Boolean getRobberyState() {
        return robberyState;
    }

    public void setRobberyState(Boolean robberyState) {
        this.robberyState = robberyState;
    }
}
