package com.dudu.navi.vauleObject;

/**
 * Created by Administrator on 2015/11/25.
 */
public enum CommonAddressType {

    HOME("家"),

    HOMETOWN("老家"),

    COMPANY("公司");

    private String name;

    private CommonAddressType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
