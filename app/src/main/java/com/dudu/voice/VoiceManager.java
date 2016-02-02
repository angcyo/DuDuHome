package com.dudu.voice;

import com.dudu.voice.semantic.constant.TTSType;

/**
 * Created by 赵圣琪 on 2015/12/24.
 */
public interface VoiceManager {

    void onInit();

    void startWakeup();

    void stopWakeup();

    void startVoiceService();

    void startUnderstanding();

    void stopUnderstanding();

    void startSpeaking(String playText);

    void startSpeaking(String playText, TTSType type);

    void startSpeaking(String playText, TTSType type, boolean showMessage);

    void stopSpeaking();

    void onStop();

    void onDestroy();

    void incrementMisUnderstandCount();

    void clearMisUnderstandCount();
}
