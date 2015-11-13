package com.dudu.voice.semantic.chain;

import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by Administrator on 2015/11/13.
 */
public class BaikeChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return SemanticConstants.SERVICE_BAIKE.equals(service);
    }

    @Override
    public boolean doSemantic(String json) {
        String answer = JsonUtils.parseIatResult(json, "answer");
        String bakeText = JsonUtils.parseIatResult(answer, "text");
        mVoiceManager.startSpeaking(bakeText, SemanticConstants.TTS_START_UNDERSTANDING);
        return true;
    }

}
