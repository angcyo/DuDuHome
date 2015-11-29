package com.dudu.voice.semantic.chain;

import com.dudu.android.launcher.utils.Constants;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by Administrator on 2015/11/12.
 */
public class MapChoiseDefalutChain extends  DefaultChain  {

    @Override
    public boolean matchSemantic(String service) {
        return true;
    }

    @Override
    public boolean doSemantic(String json) {
        mVoiceManager.startSpeaking(Constants.UNDERSTAND_CHOISE_INPUT_TIPS, SemanticConstants.TTS_START_UNDERSTANDING,false);
        return true;
    }

}
