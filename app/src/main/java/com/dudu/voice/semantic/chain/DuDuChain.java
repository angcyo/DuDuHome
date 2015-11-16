package com.dudu.voice.semantic.chain;

import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by Administrator on 2015/11/16.
 */
public class DuDuChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return SemanticConstants.SERVICE_DUDU.equals(service);
    }

    @Override
    public boolean doSemantic(String json) {
        String playText = "嘟嘟智能科技是一家非常具有创新力，发展迅速的创业型互联网企业，" +
                "公司的四位创始人很逗逼。";
        mVoiceManager.startSpeaking(playText, SemanticConstants.TTS_START_UNDERSTANDING);
        return true;
    }

}
