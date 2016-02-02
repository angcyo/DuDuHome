package com.dudu.voice;

import com.dudu.voice.semantic.constant.TTSType;
import com.dudu.voice.speech.SpeechManagerImpl;

/**
 * Created by 赵圣琪 on 2015/12/24.
 */
public class VoiceManagerProxy implements VoiceManager {

    private static VoiceManagerProxy mInstance;

    private VoiceManager mVoiceManager;

    public static VoiceManagerProxy getInstance() {
        if (mInstance == null) {
            mInstance = new VoiceManagerProxy();
        }

        return mInstance;
    }

    private VoiceManagerProxy() {
        // 默认使用思必驰
        mVoiceManager = new SpeechManagerImpl();
    }

    @Override
    public void startSpeaking(String playText) {
        if (mVoiceManager != null) {
            mVoiceManager.startSpeaking(playText);
        }
    }

    @Override
    public void startSpeaking(String playText, TTSType type) {
        if (mVoiceManager != null) {
            mVoiceManager.startSpeaking(playText, type);
        }
    }

    @Override
    public void startSpeaking(String playText, TTSType type, boolean showMessage) {
        if (mVoiceManager != null) {
            mVoiceManager.startSpeaking(playText, type, showMessage);
        }
    }

    @Override
    public void onInit() {
        if (mVoiceManager != null) {
            mVoiceManager.onInit();
        }
    }

    @Override
    public void startWakeup() {
        if (mVoiceManager != null) {
            mVoiceManager.startWakeup();
        }
    }

    @Override
    public void stopWakeup() {
        if (mVoiceManager != null) {
            mVoiceManager.stopWakeup();
        }
    }

    @Override
    public void startVoiceService() {
        if (mVoiceManager != null) {
            mVoiceManager.startVoiceService();
        }
    }

    @Override
    public void startUnderstanding() {
        if (mVoiceManager != null) {
            mVoiceManager.startUnderstanding();
        }
    }

    @Override
    public void stopUnderstanding() {
        if (mVoiceManager != null) {
            mVoiceManager.stopUnderstanding();
        }
    }

    @Override
    public void stopSpeaking() {
        if (mVoiceManager != null) {
            mVoiceManager.stopSpeaking();
        }
    }

    @Override
    public void onStop() {
        if (mVoiceManager != null) {
            mVoiceManager.onStop();
        }
    }

    @Override
    public void onDestroy() {
        if (mVoiceManager != null) {
            mVoiceManager.onDestroy();
        }
    }

    @Override
    public void incrementMisUnderstandCount() {
        if (mVoiceManager != null) {
            mVoiceManager.incrementMisUnderstandCount();
        }
    }

    @Override
    public void clearMisUnderstandCount() {
        if (mVoiceManager != null) {
            mVoiceManager.clearMisUnderstandCount();
        }
    }

}
