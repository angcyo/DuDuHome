package com.dudu.conn;

/**
 * Created by Administrator on 2015/11/30.
 */
public class ConnectionConstants {

    /**
     * 后台接口调用的方法名称
     */
    public static final String METHOD_GPSDATA = "coordinates";            // GPS数据上传

    public static final String METHOD_OBDDATA = "obdDatas";               // obd实时数据上传

    public static final String METHOD_FLAMEOUTDATA = "driveDatas";        // obd熄火数据上传

    public static final String METHOD_TAKEPHOTO = "takePhoto";                     // 拍照指令

    public static final String METHOD_NAVI = "";                          // 接人指令

    public static final String METHOD_PORTALUPDATE = "updatePortal";

    public static final String METHOD_WIFICONTROL = "";                   // wifi 热点开关控制

    public static final String METHOD_WIFICONFIG = "";                    // wifi 配置

    public static final String METHOD_ACTIVEDEVICE = "deviceLogin";                  // 设备激活

    public static final String METHOD_LOGBANC = "logs";                  // 上传logs

    public static final String METHOD_ACTIVATAIONSTATUS = "activationStatus";       //检查设备是否激活

    public static final String METHOD_FLOW = "flow";                  // 上传流量数据

    public static final String METHOD_GETFLOW = "getFlow";                  // 流量查询

    public static final String METHOD_SYNCONFIGURATION = "synConfiguration";          // 流量策略配置同步

    public static final String METHOD_SWITCHFLOW = "switchFlow";                 // 流量控制开关

    public static final String METHOD_DATAOVERSTEPALARM = "dataOverstepAlarm";          // 流量超限预警

    public static final String METHOD_DATAEXCEPTIONALARM = "dataExceptionAlarm";          // 流量策略配置同步


    /**
     * 后台接口返回码
     */
    public static final String RESULT_CODE_SUCCESS = "200"; // 操作成功

    public static final String RESULT_CODE_FAILURE = "400"; // 操作失败

    public static final int RESULT_TRAFFIC_CONTROL_CLOSE = 1;

    public static final int RESULT_TRAFFIC_CONTROL_OPEN = 0;

    public static final int FIELD_ALARM_LEVEL_OPEN = 0;

    public static final int FIELD_ALARM_LEVEL_ADVANCED_WARNING = 1;

    public static final int FIELD_ALARM_LEVEL_INTERMEDIATE_WARNING = 2;

    public static final int FIELD_ALARM_LEVEL_LOWLEVEL_WARNING = 3;

    public static final int FIELD_ALARM_LEVEL_CLOSE = 4;

    /**
     * 后台接口字段名称
     */
    public static final String FIELD_RESULT_CODE = "resultCode";

    public static final String FIELD_RESULT = "result";

    public static final String FIELD_TRAFFIC_CONTROL = "trafficControl";

    public static final String FIELD_MONTH_MAX_VALUE = "monthMaxValue";

    public static final String FIELD_FREE_ADD_VALUE = "freeAddValue";

    public static final String FIELD_DAILY_MAX_VALUE = "dailyMaxValue";

    public static final String FIELD_PORTAL_ADDRESS = "portalAddress";

    public static final String FIELD_UP_LIMIT_MAX_VALUE = "upLimitMaxValue";

    public static final String FIELD_DOWN_LIMIT_MAX_VALUE = "downLimitMaxValue";

    public static final String FIELD_LIFE_TYPE = "lifeType";

    public static final String FIELD_UPLOAD_LIMIT = "uploadLimit";

    public static final String FIELD_FREE_ADD_TIMES = "freeAddTimes";

    public static final String FIELD_REMAINING_FLOW = "remainingFlow";

    public static final String FIELD_MIDDLE_ARLAM_VALUE = "middleArlamValue";

    public static final String FIELD_HIGH_ARLAM_VALUE = "highArlamValue";

    public static final String FIELD_LOW_ARLAM_VALUE = "lowArlamValue";

    public static final String FIELD_DOWNLOAD_LIMIT = "downloadLimit";

    public static final String FIELD_FREE_ARRIVE_VALUE = "freeArriveValue";

    public static final String FIELD_PORTAL_VERSION = "portalVersion";

    public static final String FIELD_ALARM_LEVEL = "alarmLevel";


}
