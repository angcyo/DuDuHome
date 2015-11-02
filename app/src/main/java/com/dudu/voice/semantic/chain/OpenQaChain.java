package com.dudu.voice.semantic.chain;

import com.dudu.android.launcher.utils.CmdType;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by Administrator on 2015/10/31.
 */
public class OpenQaChain extends  SemanticChain  {

    @Override
    public boolean matchSemantic(String service) {
        return service.matches(CmdType.SERVICE_OPENQA);
    }

    @Override
    public boolean doSemantic(String json) {
        String answer = JsonUtils.parseIatResult(json,
                "answer");
        String qaText = JsonUtils.parseIatResult(answer,
                "text");
        mVoiceManager.startSpeaking(qaText, SemanticConstants.TTS_START_UNDERSTANDING);
        return true;
    }
}
