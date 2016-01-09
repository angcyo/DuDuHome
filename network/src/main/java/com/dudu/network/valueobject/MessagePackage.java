package com.dudu.network.valueobject;

/**
 * Created by dengjun on 2015/11/23.
 * Description :与服务器通信消息数据的抽象基类，后续所有类型的业务数据继承此类，网络通信模块只对基类进行处理，
 *                     数据持久化也只对对基类进行处理
 */
public abstract class MessagePackage{
    //设置消息ID
    public abstract void setMessageId(String messageId);

    //获取消息ID
    public abstract  String getMessageId();
    //获取method字段
    public abstract String getMethod();

    /*
    * 标识此包数据发送后是否需要等待响应，目前处理方式发送后不等待响应
    * 预留接口，后面可能会需要发送数据等待响应后再发下一条
    */
    public abstract boolean isNeedWaitResponse();

    //标识此包数据是否需要加密
    public abstract boolean isNeedEncrypt();

    // json格式字符串解码生成具体的业务数据方法，子类实现
    public abstract void createFromJsonString(String messageJsonString);

    //具体业务数据编码成json格式的消息字符串
    public abstract String toJsonString();

    //标记消息包发送失败的时候，是否需要缓存数据，待网络恢复再次发送
    public abstract boolean isNeedCache();

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        } else {
            if (object instanceof MessagePackage){
                MessagePackage messagePackage = (MessagePackage)object;
                if (messagePackage.getMessageId().equals(this.getMessageId())){
                    return true;
                }
            }
            return false;
        }
    }
}
