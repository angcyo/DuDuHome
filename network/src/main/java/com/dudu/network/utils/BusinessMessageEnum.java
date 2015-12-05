package com.dudu.network.utils;

public enum BusinessMessageEnum {

    GPS_DATA(1001, "GPS数据上传服务"),
    OBD_DATA(1002, "OBD综合数据上传服务"),
    OBD_DRIVING_HABITS_DATA(1003, "车辆驾驶习惯数据服务"),
    FLOW_DATA(1004, "使用流量上传服务"),

    DEVCIE_LOGIN_DATA(1005, "设备激活"),
    ACTIVATION_STATUS_DATA(1006, "检查设备是否激活"),
    GET_FLOW_DATA(1007, "流量查询"),
    SYNCONFIGURATION_DATA(1009, "流量策略配置同步"),
    LOGSUPLOAD_DATA(1010, "设备上传Logs日志文件"),
    PORTALUPDATE_DATA(1011, "portal更新"),
    PORTAL_DATA(1012, "portal弹出次数"),

    FLOW_SWITCH(2001, "流量开关推送指令服务"),
    FLOW_ARLAM(2002, "流量超限预警推送指令服务"),
    FLOW_EXCEPTION(2003, "流量异常预警推送指令服务"),
    UPDATE_PORTAL(2004, "更新portal推送指令服务"),
    UPDATE_LOGS(2005, "获取设备日志推送指令服务"),
    ACCESS_SWITCH(2006, "接人指令"),
    REBOOT_DEVICE(2007, "重启设备");

    private BusinessMessageEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    private int code;
    private String text;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static BusinessMessageEnum codeOf(int code) {
        for (BusinessMessageEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}
