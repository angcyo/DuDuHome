package com.dudu.voice.semantic.chain;

import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by Administrator on 2015/11/16.
 */
public class DatetimeChain extends SemanticChain {

    @Override
    public boolean matchSemantic(String service) {
        return SemanticConstants.SERVICE_DEATETIME.equals(service);
    }

    @Override
    public boolean doSemantic(String json) {
        String playText = parseDatetime(json);
        mVoiceManager.startSpeaking(playText, SemanticConstants.TTS_START_UNDERSTANDING);
        return true;
    }

    private String parseDatetime(String json) {
        String answer = JsonUtils.parseIatResult(json,
                "answer");
        String text = JsonUtils.parseIatResult(
                answer, "text");
        String[] dateArray = text.split(" ");
        String result = "抱歉，获取日期失败。";
        if (dateArray.length >= 3) {
            result = dateArray[0] + "\n" + dateArray[2];
        }

        return result;
    }

}
