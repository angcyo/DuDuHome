package com.dudu.network.service;

import com.dudu.network.event.AccessGps;
import com.dudu.network.event.ActiveDeviceRes;
import com.dudu.network.event.CheckDeviceActiveRes;
import com.dudu.network.event.DataExceptionAlarm;
import com.dudu.network.event.DataOverstepAlarm;
import com.dudu.network.event.FlowSynConfigurationRes;
import com.dudu.network.event.FlowUploadResponse;
import com.dudu.network.event.GeneralResponse;
import com.dudu.network.event.GetFlowResponse;
import com.dudu.network.event.LocationInfoUpload;
import com.dudu.network.event.LogSend;
import com.dudu.network.event.MessageMethod;
import com.dudu.network.event.PortalUpdateRes;
import com.dudu.network.event.RebootDevice;
import com.dudu.network.event.SwitchFlow;
import com.dudu.network.event.UpdatePortal;
import com.dudu.network.event.UploadVideo;
import com.dudu.network.utils.DuduLog;
import com.dudu.network.utils.Encrypt;
import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.greenrobot.event.EventBus;

/**
 * Created by dengjun on 2015/11/30.
 * Description : 收到服务器发送来的消息 在这个处理响应，
 *                       消息组装成类，通过eventBus消息发送到对应模块去处理
 */
public class MessageHandler {
    public static final  String resultCodeSuccess = "200";
    public static final  String resultCodeFailure = "400";

    private  NetworkService mNetworkService;

    private Logger log;


    public MessageHandler(NetworkService networkService) {
        mNetworkService = networkService;
        log = LoggerFactory.getLogger("network");
    }

    //处理收到的消息
    public void processReceivedMessage(JSONObject messageJsonObject){
        try {
            switch (messageJsonObject.getString("method")){
                case MessageMethod.COORDINATES:
                    proGeneralResponse(messageJsonObject);
                    break;
                case MessageMethod.OBDDATAS:
                    proGeneralResponse(messageJsonObject);
                    break;
                case MessageMethod.DRIVEDATAS:
                    proGeneralResponse(messageJsonObject);
                    break;
                case MessageMethod.DEVICELOGIN:
                    proActiveDeviceRes(messageJsonObject);
                    break;
                case MessageMethod.ACTIVATIONSTATUS:
                    proCheckDeviceActiveRes(messageJsonObject);
                    break;
                case MessageMethod.GETFLOW:
                    proGetFlowResponse(messageJsonObject);
                    break;
                case MessageMethod.FLOW:
                    proFlowUploadResponse(messageJsonObject);
                    break;
                case MessageMethod.SYNCONFIGURATION:
                    proFlowSynConfigurationRes(messageJsonObject);
                    break;
                case MessageMethod.LOGSUPLOAD:

                    break;
                case MessageMethod.PORTALUPDATE:
                    proPortalUpdateRes(messageJsonObject);
                    break;
                case MessageMethod.PORTAL:
                    proGeneralResponse(messageJsonObject);
                    break;


                //被动接收
                case MessageMethod.ACCESS:
                    proAccessMessage(messageJsonObject);
                    break;
                case MessageMethod.SWITCHFLOW:
                    proSwitchflowMessage(messageJsonObject);
                    break;
                case MessageMethod.DATAOVERSTEPALARM:
                    proDataOverstepAlarmMessage(messageJsonObject);
                    break;
                case MessageMethod.DATAEXCEPTIONALARM:
                    proDataExceptionAlarmMessage(messageJsonObject);
                    break;
                case MessageMethod.UPDATEPORTAL:
                    proUpdatePortalMessage(messageJsonObject);
                    break;
                case MessageMethod.LOGS:
                    proLogsMessage(messageJsonObject);
                    break;
                case MessageMethod.REBOOTDEVICE:
                    proRebootDeviceMessage(messageJsonObject);
                    break;
                case MessageMethod.UPLOADVIDEO:
                    proUploadVideoMessage(messageJsonObject);
                    break;
                default:
                    log.error("network-收到错误的网络消息--------");
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private void proGeneralResponse(JSONObject messageJsonObject){
        log.info("network-处理proGeneralResponse事件");
        GeneralResponse generalResponse = new GeneralResponse();
        generalResponse.createFromJsonString(messageJsonObject.toString());
        if (generalResponse.getMessageId().equals(mNetworkService.getCurSendMessagePackage().getMessageId()))
        {
            mNetworkService.removeHeadOfMessageQueue();
            mNetworkService.nodifyReceiveResponse();
        }
    }

    private void proActiveDeviceRes(JSONObject messageJsonObject){
        ActiveDeviceRes activeDeviceRes = new ActiveDeviceRes();
        activeDeviceRes.createFromJsonString(messageJsonObject.toString());
        if (activeDeviceRes.getMessageId().equals(mNetworkService.getCurSendMessagePackage().getMessageId()))
        {
            mNetworkService.removeHeadOfMessageQueue();
            mNetworkService.nodifyReceiveResponse();
        }
        log.info("network-发出ActiveDeviceRes事件");
        EventBus.getDefault().post(activeDeviceRes);
    }

    private void proCheckDeviceActiveRes(JSONObject messageJsonObject){
        CheckDeviceActiveRes checkDeviceActiveRes = new CheckDeviceActiveRes();
        checkDeviceActiveRes.createFromJsonString(messageJsonObject.toString());
        if (checkDeviceActiveRes.getMessageId().equals(mNetworkService.getCurSendMessagePackage().getMessageId()))
        {
            mNetworkService.removeHeadOfMessageQueue();
            mNetworkService.nodifyReceiveResponse();
        }
        log.info("network-发出checkDeviceActiveRes事件");
        EventBus.getDefault().post(checkDeviceActiveRes);
    }

    private void proGetFlowResponse(JSONObject messageJsonObject){
        GetFlowResponse getFlowResponse = new GetFlowResponse();
        getFlowResponse.createFromJsonString(messageJsonObject.toString());
        if (getFlowResponse.getMessageId().equals(mNetworkService.getCurSendMessagePackage().getMessageId()))
        {
            mNetworkService.removeHeadOfMessageQueue();
            mNetworkService.nodifyReceiveResponse();
        }
        log.info("network-发出GetFlowResponse事件");
        EventBus.getDefault().post(getFlowResponse);
    }

    private void proFlowUploadResponse(JSONObject messageJsonObject){
        FlowUploadResponse flowUploadResponse = new FlowUploadResponse();
        flowUploadResponse.createFromJsonString(messageJsonObject.toString());

        if (flowUploadResponse.getMessageId().equals(mNetworkService.getCurSendMessagePackage().getMessageId()))
        {
            mNetworkService.removeHeadOfMessageQueue();
            mNetworkService.nodifyReceiveResponse();
        }

        log.info("network-发出FlowUploadResponse事件");
        EventBus.getDefault().post(flowUploadResponse);
    }

    private void proFlowSynConfigurationRes(JSONObject messageJsonObject){
        FlowSynConfigurationRes flowSynConfigurationRes = new FlowSynConfigurationRes();
        flowSynConfigurationRes.createFromJsonString(messageJsonObject.toString());

        if (flowSynConfigurationRes.getMessageId().equals(mNetworkService.getCurSendMessagePackage().getMessageId()))
        {
            mNetworkService.removeHeadOfMessageQueue();
            mNetworkService.nodifyReceiveResponse();
        }

        log.info("network-发出FlowSynConfigurationRes事件");
        EventBus.getDefault().post(flowSynConfigurationRes);
    }

    private void proPortalUpdateRes(JSONObject messageJsonObject){
        PortalUpdateRes portalUpdateRes = new PortalUpdateRes();
        portalUpdateRes.createFromJsonString(messageJsonObject.toString());

        if (portalUpdateRes.getMessageId().equals(mNetworkService.getCurSendMessagePackage().getMessageId()))
        {
            mNetworkService.removeHeadOfMessageQueue();
            mNetworkService.nodifyReceiveResponse();
        }

        log.info("network-发出PortalUpdateRes事件");
        EventBus.getDefault().post(portalUpdateRes);
    }

    //
    private void proPortalRes(JSONObject messageJsonObject){

    }


    //被动接收

    private void proAccessMessage(JSONObject messageJsonObject){
        AccessGps accessGps = new AccessGps();
        accessGps.createFromJsonString(messageJsonObject.toString());

        log.info("network-发出AccessGps事件");
        EventBus.getDefault().post(accessGps);
    }

    private void proSwitchflowMessage(JSONObject messageJsonObject){
        SwitchFlow switchFlow = new SwitchFlow();
        switchFlow.createFromJsonString(messageJsonObject.toString());

        log.info("network-发出AccessGps事件");
        EventBus.getDefault().post(switchFlow);
    }

    private void proDataOverstepAlarmMessage(JSONObject messageJsonObject){
        DataOverstepAlarm dataOverstepAlarm = new DataOverstepAlarm();
        dataOverstepAlarm.createFromJsonString(messageJsonObject.toString());

        log.info("network-发出DataOverstepAlarm事件");
        EventBus.getDefault().post(dataOverstepAlarm);
    }

    private void proDataExceptionAlarmMessage(JSONObject messageJsonObject){
        DataExceptionAlarm dataExceptionAlarm = new DataExceptionAlarm();
        dataExceptionAlarm.createFromJsonString(messageJsonObject.toString());

        log.info("network-发出DataExceptionAlarm事件");
        EventBus.getDefault().post(dataExceptionAlarm);
    }

    private void proUpdatePortalMessage(JSONObject messageJsonObject){
        UpdatePortal updatePortal = new UpdatePortal();
        updatePortal.createFromJsonString(messageJsonObject.toString());

        log.info("network-发出UpdatePortal事件");
        EventBus.getDefault().post(updatePortal);
    }

    private void proLogsMessage(JSONObject messageJsonObject){
        LogSend logSend = new LogSend();
        logSend.createFromJsonString(messageJsonObject.toString());

        log.info("network-发出LogSend事件");
        EventBus.getDefault().post(logSend);
    }

    private void proRebootDeviceMessage(JSONObject messageJsonObject){
        RebootDevice rebootDevice = new RebootDevice();
        rebootDevice.createFromJsonString(messageJsonObject.toString());
        log.info("network-发出RebootDevice事件");
        EventBus.getDefault().post(rebootDevice);
    }

    private void proUploadVideoMessage(JSONObject messageJsonObject){
        UploadVideo uploadVideo = new UploadVideo();
        uploadVideo.createFromJsonString(messageJsonObject.toString());
        log.info("network-发出UploadVideo事件");
        EventBus.getDefault().post(uploadVideo);
    }

    public void init(){

    }

    public void release(){

    }

}
