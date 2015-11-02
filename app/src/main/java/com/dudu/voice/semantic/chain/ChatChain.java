package com.dudu.voice.semantic.chain;

import com.dudu.android.launcher.utils.CmdType;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by Administrator on 2015/10/31.
 */
public class ChatChain extends  SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return service.equals(CmdType.SERVICE_CHAT);
    }

    @Override
    public boolean doSemantic(String json) {
        String chatAnswer = JsonUtils.parseIatResult(json,
                "answer");
        String chatText = JsonUtils.parseIatResult(
                chatAnswer, "text");
        mVoiceManager.startSpeaking(chatText, SemanticConstants.TTS_START_UNDERSTANDING);
        return true;
    }

}
