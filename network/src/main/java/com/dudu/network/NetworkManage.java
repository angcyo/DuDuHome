package com.dudu.network;

import com.dudu.network.service.NetworkService;
import com.dudu.network.valueobject.ConnectionParam;
import com.dudu.network.valueobject.MessagePackage;

/**
 * Created by dengjun on 2015/11/27.
 * Description :
 */
public class NetworkManage {
    private static NetworkManage instance = null;

    private NetworkService networkService;

    public static  NetworkManage getInstance(){
        if (instance == null){
            synchronized (NetworkManage.class){
                if (instance == null){
                    instance = new NetworkManage();
                }
            }
        }
        return instance;
    }

    private NetworkManage(){
        networkService = new NetworkService();
    }

    public void init(String ip,int port){
        networkService.init(new ConnectionParam(ip,port));
    }

    public void release(){
        networkService.release();
    }

    //此发送方法把数据丢到阻塞队列里面，最好不要放在UI线程中运行
    public void sendMessage(MessagePackage messagePackage){
        networkService.sendMessage(messagePackage);
    }
}
