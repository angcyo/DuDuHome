package com.dudu.network.event;

import com.dudu.network.valueobject.MessagePackage;

/**
 * Created by dengjun on 2015/12/1.
 * Description :3.6.1 发送日志文件
 */
public class LogUpload extends MessagePackage{
    private String messageId;
    private  String method;

    private String obeId;

    @Override
    public void setMessageId(String messageId) {

    }

    @Override
    public String getMessageId() {
        return null;
    }

    @Override
    public boolean isNeedWaitResponse() {
        return false;
    }

    @Override
    public boolean isNeedEncrypt() {
        return false;
    }

    @Override
    public void createFromJsonString(String messageJsonString) {

    }

    @Override
    public String toJsonString() {
        return null;
    }
}
