package com.dudu.network.interfaces;

/**
 * Created by dengjun on 2015/11/27.
 * Description :
 */
public interface SendCallBack {
    public void onSendSuccess(String messageReceived);

    public void onSendFailure();
}
