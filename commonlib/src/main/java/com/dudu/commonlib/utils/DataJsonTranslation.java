package com.dudu.commonlib.utils;

import com.dudu.commonlib.repo.ReceiverData;
import com.google.gson.Gson;

/**
 * Created by Administrator on 2016/2/17.
 */
public class DataJsonTranslation {

    public static String objectToJson(Object object){
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static Object jsonToObject(String json, Class classOfJson){
        Gson gson = new Gson();
        return gson.fromJson(json,classOfJson);
    }

    public static ReceiverData getDataFromReceiver(String receivedString){
        ReceiverData receiverData = new ReceiverData();
        if(receivedString.startsWith(ReceiverData.XGPUSHSHOWEDRESULT_KEY)
                &&receivedString.contains("[")&&receivedString.contains("]")){
            String xgpushshowedresult = receivedString
                    .substring(receivedString.indexOf("["),receivedString.lastIndexOf("]")).trim();
            String[] contents = xgpushshowedresult.split(",");
            for(String content:contents){
                if(content.contains("=")){
                    String[] values = content.trim().split("=");
                    if(values.length>1){
                        String key = values[0].trim();
                        switch (key){
                            case ReceiverData.CONTENT_KEY:
                                receiverData.setContent(values[1].trim());
                                receiverData.setSwitchContent(Integer.valueOf(values[1].trim()));
                                break;
                            case ReceiverData.TITLE_KEY:
                                receiverData.setContent(values[1].trim());
                                break;
                            case ReceiverData.CUSTOMCONTENT_KEY:
                                receiverData.setContent(values[1].trim());
                                break;
                        }
                    }
                }
            }
        }
        return receiverData;
    }
}
