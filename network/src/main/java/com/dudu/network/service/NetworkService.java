package com.dudu.network.service;

import com.dudu.network.client.MinaConnection;
import com.dudu.network.interfaces.IConnectCallBack;
import com.dudu.network.interfaces.IConnection;
import com.dudu.network.valueobject.ConnectionParam;
import com.dudu.network.valueobject.ConnectionState;
import com.dudu.network.valueobject.MessagePackage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by dengjun on 2015/11/28.
 * Description :
 */
public class NetworkService implements IConnectCallBack
{
    private  BlockingQueue<MessagePackage> messagePackagesQueue;
    IConnection iConnection = null;

    private Logger log;

    private boolean sendThreadRunFlag = false;
    //发送数据后，上锁， 发送数据成功后释放锁
    private String sendMessageLock = "SendMessageLock";
    //当前发送的消息包
    private MessagePackage curSendMessagePackage;


    public NetworkService() {
        iConnection = new MinaConnection();

        messagePackagesQueue = new ArrayBlockingQueue<MessagePackage>(500, true);

        log = LoggerFactory.getLogger("network");
    }

    //发送数据异步发送，消息放到阻塞队列，由发送线程统一发送
    public void sendMessage(MessagePackage messagePackage){
        try {
            messagePackagesQueue.put(messagePackage);
        } catch (InterruptedException e) {
            log.error("异常:"+ e);
        }
    }

    private  Thread sendThread = new Thread(){
        @Override
        public void run() {
            while (sendThreadRunFlag){
                try{
                    MessagePackage messagePackageToSend = nextMessagePackage();

                    if(iConnection.isConnected()){
                        sendMessageReal(messagePackageToSend);
                        curSendMessagePackage = messagePackageToSend;
                        try {
                            sendMessageLock.wait();//后续做时间控制
                        } catch (InterruptedException e) {
                            log.error("异常:"+e);
                        }
                    }
                }catch (Exception e){
                    log.error("异常:"+ e);
                }
            }
        }
    };

    private MessagePackage nextMessagePackage(){
        MessagePackage messagePackage = null;


        return messagePackage;
    }

    private void sendMessageReal(MessagePackage messagePackage){
        log.debug("发送消息_加密前 messageID："+messagePackage.getMessageId() + "  消息内容：" + messagePackage.toJsonString());
        //加密还未加上
        iConnection.sendMessage(messagePackage.toJsonString());
        log.debug("发送消息_加密后 messageID：" + messagePackage.getMessageId() + "  消息内容：");
    }

    public void connect(ConnectionParam connectionParam){
        iConnection.connect(connectionParam);
    }

    @Override
    public void onConnectionState(ConnectionState connectionState) {

    }

    @Override
    public void onReceive(String messageReceived) {

    }

    @Override
    public void onMessageSent(String messageSent) {//执行这里时，表明数据发送成功了
        sendMessageLock.notify();
    }



}
