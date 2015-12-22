package com.dudu.network.service;

import android.util.Log;

import com.dudu.network.event.DriveDatasUpload;
import com.dudu.network.event.FlowUpload;
import com.dudu.network.event.GetFlow;
import com.dudu.network.event.LocationInfoUpload;
import com.dudu.network.event.MessageMethod;
import com.dudu.network.event.ObdDatasUpload;
import com.dudu.storage.Storage;
import com.dudu.storage.core.IReadCallBack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by dengjun on 2015/12/4.
 * Description :此类处理发送失败保存在文件的中的消息
 *                     发送失败的消息并不是所有的都需要保存，见协议文档
 */
public class StorageMessageHandler {
    private  NetworkService mNetworkService;
    private Logger log;

    public StorageMessageHandler(NetworkService mNetworkService) {
        this.mNetworkService = mNetworkService;
        log = LoggerFactory.getLogger("network");
    }

    public void proStorageMessage(){
        log.info("处理发送失败后持久化的消息---");
        Storage.getInstance().readData(new IReadCallBack() {
            @Override
            public void onReadData(List<String> dataStringList) {
                try {
                    if (dataStringList!=null){
                        for(int i = 0; i < dataStringList.size(); i++){
                            String [] messageArray = dataStringList.get(i).split("@");//把消息的method字段和消息分割开
//                            log.debug("读取消息：{}", dataStringList.get(i));
                            proStorageMessage(messageArray[0], messageArray[1]);
                        }
                        dataStringList.clear();
                    }
                } catch (Exception e) {
                    log.error("异常:{}",e);
                    e.printStackTrace();
                }
            }
        });
    }

    private void proStorageMessage(String method, String messageJsonString){
        try {
            switch (method){
                //主动上传
                case MessageMethod.COORDINATES:
                    LocationInfoUpload locationInfoUpload = new LocationInfoUpload();
                    locationInfoUpload.createFromJsonString(messageJsonString);
                    mNetworkService.sendMessage(locationInfoUpload);
                    break;
                case MessageMethod.OBDDATAS:
                    ObdDatasUpload obdDatasUpload = new ObdDatasUpload();
                    obdDatasUpload.createFromJsonString(messageJsonString);
                    mNetworkService.sendMessage(obdDatasUpload);
                    break;
                case MessageMethod.DRIVEDATAS:
                    DriveDatasUpload driveDatasUpload = new DriveDatasUpload();
                    driveDatasUpload.createFromJsonString(messageJsonString);
                    mNetworkService.sendMessage(driveDatasUpload);
                    break;
                case MessageMethod.DEVICELOGIN:
                    break;
                case MessageMethod.ACTIVATIONSTATUS:
                    break;
                case MessageMethod.GETFLOW:
                    GetFlow getFlow = new GetFlow();
                    getFlow.createFromJsonString(messageJsonString);
                    mNetworkService.sendMessage(getFlow);
                    break;
                case MessageMethod.FLOW:
                    FlowUpload flowUpload = new FlowUpload();
                    flowUpload.createFromJsonString(messageJsonString);
                    mNetworkService.sendMessage(flowUpload);
                    break;
                case MessageMethod.SYNCONFIGURATION:
                    break;
                case MessageMethod.LOGSUPLOAD:

                    break;
                case MessageMethod.PORTALUPDATE:
                    break;


                //被动接收
                case MessageMethod.ACCESS:
                    break;
                case MessageMethod.SWITCHFLOW:
                    break;
                case MessageMethod.DATAOVERSTEPALARM:
                    break;
                case MessageMethod.DATAEXCEPTIONALARM:
                    break;
                case MessageMethod.UPDATEPORTAL:
                    break;
                case MessageMethod.LOGS:
                    break;
                case MessageMethod.REBOOTDEVICE:
                    break;
                default:
                    break;
            }
        } catch (Exception e){
            log.error("异常:{}",e);
        }
    }
}
