package com.dudu.conn;

/**
 * Created by lxh on 2015/11/7.
 */
public class ConnMethod {

    public static final String METHOD_GPSDATA = "coordinates";            // GPS数据上传

    public static final String METHOD_OBDDATA = "obdDatas";               // obd实时数据上传

    public static final String METHOD_FLAMEOUTDATA = "driveDatas";        // obd熄火数据上传

    public static final String METHOD_TAKEPHOTO = "takePhoto";                     // 拍照指令

    public static final String METHOD_NAVI = "";                          // 接人指令

    public static final String METHOD_PORTALUPDATE = "portalUpdate";      //portal更新

    public static final String METHOD_WIFICONTROL = "";                   // wifi 热点开关控制

    public static final String METHOD_WIFICONFIG = "";                    // wifi 配置

    public static final String METHOD_ACTIVEDEVICE = "deviceLogin";                  // 设备激活

    public static final String METHOD_LOGBANC = "logs";                  // 上传logs

    public static final String METHOD_ACTIVATAIONSTATUS = "activationStatus";       //检查设备是否激活

}
