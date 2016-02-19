package com.dudu.workflow.receiver;

import com.dudu.commonlib.repo.ReceiverData;
import com.dudu.commonlib.utils.DataJsonTranslation;
import com.dudu.commonlib.utils.RxBus;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2016/2/17.
 */
public class MessageReceiverFlow {

    public static final String LogTag = "MessageReceiverFlow";

    public Logger logger;

    private static MessageReceiverFlow mInstance = new MessageReceiverFlow();

    private MessageReceiverFlow(){
        logger = LoggerFactory.getLogger(LogTag);
    }

    public static MessageReceiverFlow getInstance(){
        return mInstance;
    }

    public void notifactionShowedResult(String notifiShowedString){
        logger.debug("onNotifactionShowedResult: " + notifiShowedString);
    }

    public void unregisterResult(int errorCode) {
        logger.debug("errorCode: " + errorCode);
        String text = "";
        if (errorCode == 0) {
            text = "反注册成功";
        } else {
            text = "反注册失败" + errorCode;
        }
        logger.debug("onUnregisterResult: " + text);
    }

    public void setTagResult(int errorCode, String tagName) {
        logger.debug("errorCode: "+errorCode+"; tagName: "+tagName);
        String text = "";
        if (errorCode == 0) {
            text = "\"" + tagName + "\"设置成功";
        } else {
            text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
        }
        logger.debug("onSetTagResult: "+text);
    }

    public void deleteTagResult(int errorCode, String tagName) {
        logger.debug("errorCode: "+errorCode+"; tagName:"+tagName);
        String text = "";
        if (errorCode == 0) {
            text = "\"" + tagName + "\"删除成功";
        } else {
            text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
        }
        logger.debug(text);
    }

    /**
     * 通知点击回调 actionType=1为该消息被清除，actionType=0为该消息被点击
     *
     * @param actionType
     * @param message
     * @param customContent
     */
    public void notifactionClickedResult(long actionType,String message,String customContent) {
        logger.debug("errorCode: "+message);
        String text = "";
        if (actionType == 0) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
            text = "通知被打开 :" + message;
        } else if (actionType == 0) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            text = "通知被清除 :" + message;
        }
        logger.debug("广播接收到通知被点击:" + message);
        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                // key1为前台配置的key
                if (!obj.isNull("key")) {
                    String value = obj.getString("key");
                    logger.debug("get custom value:" + value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // APP自主处理的过程。。。
        logger.debug(text);
    }

    public void registerResult(int errorCode,
                                 String message) {
        logger.debug("errorCode: "+errorCode+"; tagName: "+message);
        String text = "";
        if (errorCode == 0) {
            text = message + "注册成功";
        } else {
            text = message + "注册失败，错误码：" + errorCode;
        }
        logger.debug(text);
    }

    /**
     * 消息透传
     *
     * @param title
     * @param content
     * @param customContent
     */
    public void textMessage(String title, String content,String customContent) {
        logger.debug("收到消息: title: "+title+"; content: "+content+"; customContent: "+customContent);

        ReceiverData data = DataJsonTranslation.getDataFromReceiver(new ReceiverData(title,content,customContent));
        RxBus.getInstance().send(data);

        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                // key1为前台配置的key
                if (!obj.isNull("key")) {
                    String value = obj.getString("key");
                    logger.debug("get custom value:" + value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
