package com.dudu.network.service;

import com.dudu.network.client.MinaConnection;
import com.dudu.network.interfaces.IConnectCallBack;
import com.dudu.network.interfaces.IConnection;
import com.dudu.network.utils.Encrypt;
import com.dudu.network.valueobject.ConnectionParam;
import com.dudu.network.valueobject.ConnectionState;
import com.dudu.network.valueobject.MessagePackage;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by dengjun on 2015/11/28.
 * Description :
 */
public class NetworkService implements IConnectCallBack
{
    //阻塞队列，用于存放要发送的消息
    private  BlockingQueue<MessagePackage> messagePackagesQueue;
    //缓存Map，用于缓存发送失败和发送成功没有收到响应的消息
//    private Map<String, MessagePackage> cacheMap;

    IConnection iConnection = null;
    ConnectionParam connectionParam;

    private Logger log;

    //发送数据线程运行标记
    private boolean sendThreadRunFlag = false;
    //发送数据后，上锁， 发送数据成功后释放锁
    private String sendMessageLock = "SendMessageLock";
    //当前发送的消息包
    private MessagePackage curSendMessagePackage;
    //当前发送无需响应的消息的消息
    private String curNoNeedResponseMessageString;

    private ScheduledExecutorService sendThreadPool = null;

    //消息处理器，对服务器发来的消息处理
    private MessageHandler messageHandler;

    public NetworkService() {
        iConnection = new MinaConnection();
        iConnection.setConnectCallBack(this);
        messagePackagesQueue = new ArrayBlockingQueue<MessagePackage>(500, true);
//        cacheMap = new Hashtable<String, MessagePackage>(500);

        messageHandler = new MessageHandler(this);
        log = LoggerFactory.getLogger("network");
    }

    private  Thread sendThread = new Thread(){
        @Override
        public void run() {
            while (sendThreadRunFlag){
                try{
                    if (iConnection.isConnected() == false)
                        iConnection.connect(connectionParam);

                    if (iConnection.isConnected() == false){
                        synchronized (sendThread){
                            log.debug("----发送线程等待---：");
                            sendThread.wait();
                        }
                    }

                    //鉴于数据持久化模块还没做，如果队列数据达到400，就删除队列前100个消息
                    if (messagePackagesQueue.size() > 400){
                        for (int i = 0; i<100; i++)
                            removeHeadOfMessageQueue();
                    }

                    MessagePackage messagePackageToSend = nextMessagePackage();
                    log.debug(/*"----消息---："+ messagePackageToSend.toJsonString()+*/ " 发送消息时： 网络状态："+iConnection.isConnected());
                    curSendMessagePackage = messagePackageToSend;
                    if(iConnection.isConnected()){
                        sendMessageReal(messagePackageToSend);
                        if (messagePackageToSend.isNeedWaitResponse())
                            waitResponse();
                    }else {
                        //做持久化处理

                    }
                }catch (Exception e){
                    log.error("异常:"+ e);
                    e.printStackTrace();
                }
            }
        }
    };



    //发送数据异步发送，消息放到阻塞队列，由发送线程统一发送
    public void sendMessage(MessagePackage messagePackage){
        try {
            log.debug("messagePackagesQueue消息队列大小："+ messagePackagesQueue.size()+ "  消息ID："+messagePackage.getMessageId());
            messagePackagesQueue.put(messagePackage);
        } catch (InterruptedException e) {
            log.error("异常:"+ e);
        }
        synchronized (messagePackagesQueue) {
            messagePackagesQueue.notifyAll();
        }

        synchronized (sendThread){
//            log.debug("sendMessage 通知发送线程，可以发送消息了--------");
            sendThread.notifyAll();
        }
    }

    private MessagePackage nextMessagePackage(){
        MessagePackage messagePackage = null;
        /*while ((messagePackage = messagePackagesQueue.poll()) == null){
            try {//为null的情况说明队列里面没有要发送的消息，等待有发送的消息
                messagePackagesQueue.wait();
            } catch (InterruptedException e) {
                log.error("异常:" + e);
            }
        }*/

        while ((messagePackage = messagePackagesQueue.peek()) == null){//只取，不删，发送成功才删除
            try {//为null的情况说明队列里面没有要发送的消息，等待有发送的消息
                synchronized (messagePackagesQueue){
                    messagePackagesQueue.wait();
                }
            } catch (InterruptedException e) {
                log.error("获取下一条消息异常:" + e);
            }
        }
        return messagePackage;
    }

    private void waitResponse(){
        try {//是否需要等待，后续待定
            synchronized (sendMessageLock) {
                log.debug("----发送消息后--等待响应---：");
                sendMessageLock.wait(30*1000);//后续做时间控制,
            }
        } catch (InterruptedException e) {
            log.error("异常:"+e);
        }
    }

    private void sendMessageReal(MessagePackage messagePackage){
        String sendMessage = messagePackage.toJsonString();
        log.info("发送消息_加密前 messageID：" + messagePackage.getMessageId() + "  消息内容：" + sendMessage);
        try {
            if (messagePackage.isNeedEncrypt())
            {
                sendMessage = Encrypt.AESEncrypt(sendMessage, Encrypt.vi);
                log.debug("发送消息_加密后 messageID：" + messagePackage.getMessageId() + "  消息内容："+ sendMessage);
            }else {
                log.info("响应服务器发送的消息-----------");
                curNoNeedResponseMessageString = sendMessage;
            }

            iConnection.sendMessage(sendMessage);
        } catch (Exception e) {
            log.error("异常:" + e);
        }
    }


    //初始化网络服务
    public void init(ConnectionParam connectionParam){
        this.connectionParam = connectionParam;

        sendThreadRunFlag = true;
        sendThreadPool = Executors.newScheduledThreadPool(2);
        sendThreadPool.execute(sendThread);

        sendThreadPool.scheduleAtFixedRate(keepAliveThread, 30, 40, TimeUnit.SECONDS);

//        iConnection.connect(this.connectionParam);
        messageHandler.init();
    }

    //结束网络服务
    public void release(){
        sendThreadRunFlag = false;

        //如果队列还有数据未发送完，需要等待或者持久化


        if (sendThreadPool != null && !sendThreadPool.isShutdown()) {
            try {
                sendThreadPool.awaitTermination(20*1000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("异常:" + e);
            }
            sendThreadPool = null;
        }

        messageHandler.release();
    }

    public void connect(ConnectionParam connectionParam){
        iConnection.connect(connectionParam);
    }

    @Override
    public void onConnectionState(ConnectionState connectionState) {
        log.info("网络状态：" + connectionState.connectionState);
        switch (connectionState.connectionState) {
            case ConnectionState.CONNECTION_CREATE:
                break;
            case ConnectionState.CONNECTION_FAIL://当连接被关闭的时候，此方法被调用。
                break;
            case ConnectionState.CONNECTION_IDLE://默认情况不会有限制状态
                break;
            case ConnectionState.CONNECTION_SUCCESS:
                synchronized (sendThread){
                    log.debug("通知发送线程，可以发送消息了--------");
                    sendThread.notify();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onReceive(String messageReceived) {
            //需要解析出消息ID，知道哪条数据收到响应了
        /*JSONObject jsonResult = null;
        try {
            jsonResult = new JSONObject(messageReceived);
            if (*//*jsonResult.has("messageId") &&*//* jsonResult.has("resultCode") *//*&& jsonResult.has("method")*//*) {
                if (jsonResult.getString("messageId").equals(curSendMessagePackage.getMessageId()))
                    messagePackagesQueue.remove();
                synchronized (sendMessageLock){
                    log.debug("发送数据收到响应，通知可以发送下一条--------");
                    sendMessageLock.notifyAll();//通知可以发送下一条数据了
                }
            }
        } catch (JSONException e) {
            log.error("异常:"+ e);
        }*/

        try {
            messageHandler.processReceivedMessage(new JSONObject(messageReceived));
        } catch (JSONException e) {
            log.error("异常:" + e);
        }
    }

    public void removeHeadOfMessageQueue(){
        messagePackagesQueue.remove();
    }

    public void nodifyReceiveResponse(){
        synchronized (sendMessageLock){
            log.debug("发送数据收到响应，通知可以发送下一条--------");
            sendMessageLock.notifyAll();//通知可以发送下一条数据了
        }
    }

    public MessagePackage getCurSendMessagePackage() {
        return curSendMessagePackage;
    }

    @Override
    public void onMessageSent(String messageSent) {//执行这里时，表明数据发送成功了
//        sendMessageLock.notify();//通知可以发送下一条数据了
        if (messageSent.equals(curNoNeedResponseMessageString)){
            log.debug("无需响应的消息，发送成功后就删除-------");
            removeHeadOfMessageQueue();
        }
    }

    private Thread keepAliveThread = new Thread(){
        @Override
        public void run() {
            if (sendThreadRunFlag == false)//发送线程无需运行的时候，说明设备要休眠了
                return;
            try {
                if (iConnection.isConnected() == false){
                    iConnection.disConnect();
                    log.info("守护线程重连网络---");
                    iConnection.connect(connectionParam);
                }
            }catch (Exception e){
                log.error("异常:"+ e);
            }
        }
    };
}
