package com.dudu.voice.semantic.parser;


import com.dudu.voice.semantic.bean.SemanticBean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lxh on 2015/12/24.
 */
public class SpeechJsonParser {

    private static SpeechJsonParser mInstance;

    private CloudJsonParser mCloudParser;


    private NativeJsonParser mNativeParser;

    private SpeechJsonParser() {
        mCloudParser = new CloudJsonParser();
        mNativeParser = new NativeJsonParser();
    }

    public static SpeechJsonParser getInstance() {
        if (mInstance == null) {
            mInstance = new SpeechJsonParser();
        }

        return mInstance;
    }

    public SemanticBean parseSemanticJson(String result) {
        try {
            JSONObject json = new JSONObject(result);
            if (!json.isNull("src")) {
                String src = json.optString("src");
                if ("cloud".equals(src)) {
                    return mCloudParser.getSemanticBean(result);
                } else if ("native".equals(src)) {
                    return mNativeParser.getSemanticBean(result);
                }
            }
        } catch (JSONException e) {
            // 忽略
        }

        return null;
    }
}
