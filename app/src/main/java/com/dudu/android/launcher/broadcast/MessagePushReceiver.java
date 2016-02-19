package com.dudu.android.launcher.broadcast;

import android.content.Context;

import com.dudu.workflow.receiver.MessageReceiverFlow;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

/**
 * Created by Administrator on 2016/2/5.
 */
public class MessagePushReceiver extends XGPushBaseReceiver {

    @Override
    public void onNotifactionShowedResult(Context context,
                                          XGPushShowedResult notifiShowedRlt) {
        if(notifiShowedRlt==null){
            MessageReceiverFlow.getInstance().notifactionShowedResult(notifiShowedRlt.toString());
        }
    }

    @Override
    public void onUnregisterResult(Context context, int errorCode) {
        MessageReceiverFlow.getInstance().unregisterResult(errorCode);
    }

    @Override
    public void onSetTagResult(Context context, int errorCode, String tagName) {
        MessageReceiverFlow.getInstance().setTagResult(errorCode,tagName);
    }

    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {
        MessageReceiverFlow.getInstance().deleteTagResult(errorCode,tagName);
    }

    /**
     * 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
     */
    @Override
    public void onNotifactionClickedResult(Context context,
                                           XGPushClickedResult message) {
        if(message!=null) {
            MessageReceiverFlow.getInstance().notifactionClickedResult(message.getActionType(), message.toString(), message.getCustomContent());
        }
    }

    @Override
    public void onRegisterResult(Context context, int errorCode,
                                 XGPushRegisterResult message) {
        if(message!=null) {
            MessageReceiverFlow.getInstance().registerResult(errorCode, message.toString());
        }
    }

    /**
     * 消息透传
     */
    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        if(message!=null) {
            MessageReceiverFlow.getInstance().textMessage(message.getTitle(), message.getContent(), message.getCustomContent());
        }
    }

}


