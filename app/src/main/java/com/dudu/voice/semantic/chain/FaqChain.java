package com.dudu.voice.semantic.chain;

import com.dudu.android.launcher.utils.CmdType;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by Administrator on 2015/10/31.
 */
public class FaqChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return service.equals(CmdType.SERVICE_FAQ);
    }

    @Override
    public boolean doSemantic(String json) {
        String faqAnswer = JsonUtils.parseIatResult(json,
                "answer");
        String faqText = JsonUtils.parseIatResult(
                faqAnswer, "text");
        mVoiceManager.startSpeaking(faqText, SemanticConstants.TTS_START_UNDERSTANDING);
        return true;
    }

}
