package com.dudu.voice.semantic.chain;

import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by Administrator on 2015/10/30.
 */
public class DefaultChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return true;
    }

    @Override
    public boolean doSemantic(String json) {
        String playText = "嘟嘟识别不了，请重试。";
        mVoiceManager.startSpeaking(playText, SemanticConstants.TTS_START_UNDERSTANDING);
        return true;
    }

}