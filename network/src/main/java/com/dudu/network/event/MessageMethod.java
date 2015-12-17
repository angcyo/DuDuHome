package com.dudu.network.event;

/**
 * Created by dengjun on 2015/11/30.
 * Description :  此类对应《Dcloud服务端与设备通讯接口说明书》中的消息中的method字段
 */
public class MessageMethod {
    //设备主动上传消息
    public static final String COORDINATES = "coordinates";            // GPS数据上传
    public static final String OBDDATAS = "obdDatas";               // obd实时数据上传
    public static final String DRIVEDATAS = "driveDatas";        // obd熄火数据上传
    public static final String DEVICELOGIN = "deviceLogin";                  // 设备激活
    public static final String ACTIVATIONSTATUS = "activationStatus";       //检查设备是否激活
//    public static final String TAKEPHOTO = "takePhoto";                     // 拍照指令
    public static final String GETFLOW = "getFlow";                     // 3.4.1 流量查询
    public static final String FLOW = "flow";                     // 3.4.2 使用流量上报
    public static final String SYNCONFIGURATION = "synConfiguration";// 33.4.3 流量策略配置同步
    public static final String LOGSUPLOAD = "logsUpload";                     // 3.6.1 发送日志文件
    public static final String PORTALUPDATE = "portalupdate";      //portal更新
    public static final String PORTAL= "portal";      //portal更新

    //设备被动接收消息
    public static final String ACCESS = "access";                          // 接人指令  GPS（微信接口）
    public static final String SWITCHFLOW = "switchFlow";                    // 流量开关
    public static final String DATAOVERSTEPALARM = "dataOverstepAlarm";  // 4.2.2 流量超限预警
    public static final String DATAEXCEPTIONALARM = "dataExceptionAlarm"; // 4.2.2 流量超限预警
    public static final String UPDATEPORTAL = "updatePortal";      //4.3.1 更新Portal
    public static final String LOGS = "logs";                  // 上传logs
    public static final String REBOOTDEVICE = "rebootDevice";                  // 重启设备

    public static final String UPLOADVIDEO = "uploadVideo";                  // 上传视频

//    public static final String METHOD_WIFICONTROL = "";                   // wifi 热点开关控制
//
//    public static final String METHOD_WIFICONFIG = "";                    // wifi 配置
}
