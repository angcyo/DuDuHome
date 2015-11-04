package com.dudu.voice.semantic.chain;

import com.dudu.android.launcher.utils.Constants;
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
        mVoiceManager.startSpeaking(Constants.UNDERSTAND_MISUNDERSTAND, SemanticConstants.TTS_START_UNDERSTANDING);
        return true;
    }

}