package com.dudu.voice.semantic.bean;

/**
 * Created by Administrator on 2015/12/29.
 */
public class CmdBean extends SemanticBean {

    private String target;

    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

}
