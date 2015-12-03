package com.dudu.network.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xuzhao on 2015/12/3.
 */
public class StringTools {
    public static String GetStringValue(String field, JSONObject object) {
        String value = null;
        if (object.has(field)) {
            try {
                value = object.getString(field);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

}
