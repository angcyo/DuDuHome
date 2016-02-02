package com.dudu.voice.semantic.bean.map;

/**
 * Created by lxh on 2015/12/29.
 */
public class NavigationBean extends MapBean{

    private String operation;

    private String object;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {

        this.object = object;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {

        this.operation = operation;
    }
}
