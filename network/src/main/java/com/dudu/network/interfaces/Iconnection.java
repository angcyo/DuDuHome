package com.dudu.network.interfaces;

import com.dudu.network.valueobject.ConnectionParam;

/**
 * Created by dengjun on 2015/11/27.
 * Description :
 */
public interface IConnection {
    public boolean isConnected();

    public void connect(ConnectionParam connectionParam);

    public void disConnect();

    public void setConnectCallBack(IConnectCallBack iConnectCallBack);

    public void sendMessage(String sendMessage);
}
