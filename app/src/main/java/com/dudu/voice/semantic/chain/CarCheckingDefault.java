package com.dudu.voice.semantic.chain;

import com.dudu.android.launcher.utils.Constants;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by Administrator on 2015/11/5.
 */
public class CarCheckingDefault extends DefaultChain {

    @Override
    public boolean doSemantic(String json) {
        mVoiceManager.startSpeaking(Constants.UNDERSTAND_MISUNDERSTAND, SemanticConstants.TTS_START_UNDERSTANDING, false);
        return true;
    }
}
