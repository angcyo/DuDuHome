package com.dudu.commonlib.utils;

import com.dudu.commonlib.repo.ReceiverData;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/17.
 */
public class DataJsonTranslation {

    private static Logger logger = LoggerFactory.getLogger("DataJsonTranslation");

    public static String objectToJson(Object object){
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static Object jsonToObject(String json, Class classOfJson){
        Gson gson = new Gson();
        return gson.fromJson(json,classOfJson);
    }

    public static ReceiverData getDataFromReceiver(ReceiverData receiverData){
        try {
            receiverData = setContentData(receiverData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return receiverData;
    }

    public static ReceiverData setContentData(ReceiverData receiverData) throws JSONException {
        switch (receiverData.getTitle()){
            case ReceiverData.ACCELERATEDTESTSTART_VALUE:
                break;
            case ReceiverData.THEFT_VALUE:
                receiverData.setSwitchValue(toMap(receiverData.getContent()).get(ReceiverData.SWITCH_KEY));
                break;
            case ReceiverData.ROBBERY_VALUE:
                Map<String,String> map = toMap(receiverData.getContent());
                receiverData.setSwitch0Value(map.get(ReceiverData.SWITCH_KEY+"0"));
                receiverData.setSwitch1Value(map.get(ReceiverData.SWITCH_KEY+"1"));
                receiverData.setSwitch2Value(map.get(ReceiverData.SWITCH_KEY+"2"));
                receiverData.setSwitch3Value(map.get(ReceiverData.SWITCH_KEY+"3"));
                break;
        }
        return receiverData;
    }

    /**
     * 将Json对象转换成Map
     *
     * @param jsonString
     *            json对象
     * @return Map对象
     * @throws JSONException
     */
    public static Map<String,String> toMap(String jsonString) throws JSONException {
        logger.debug("jsonString:"+jsonString);
        JSONObject jsonObject = new JSONObject(jsonString);
        Map<String,String> result = new HashMap<>();
        Iterator iterator = jsonObject.keys();
        String key = null;
        String value = null;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            value = jsonObject.getString(key);
            result.put(key, value);
        }
        return result;

    }
}
