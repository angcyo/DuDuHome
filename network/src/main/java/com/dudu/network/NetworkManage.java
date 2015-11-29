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

    public void init(){
        networkService.init(new ConnectionParam());
    }

    public void release(){
        networkService.release();
    }

    public void sendMessage(MessagePackage messagePackage){
        networkService.sendMessage(messagePackage);
    }
}
