package com.dudu.voice.semantic.chain;

import android.content.Context;
import android.media.AudioManager;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.bean.VoiceEntity;
import com.dudu.android.launcher.bean.VoiceSlots;
import com.dudu.android.launcher.utils.GsonUtil;
import com.dudu.android.launcher.utils.JsonUtils;
import com.dudu.voice.semantic.SemanticConstants;

/**
 * Created by 赵圣琪 on 2015/10/28.
 */
public class AdjustVolumeChain extends SemanticChain {

    public static final int VOLUME_INCREMENTAL = 2;

    public enum VolumeState {
        NORMAL,
        ADJUST
    }

    private VolumeState mState = VolumeState.NORMAL;

    private AudioManager mAudioManager;

    private int mMaxVolume;

    private int mCurVolume;

    public AdjustVolumeChain() {
        super();
        mAudioManager = (AudioManager) LauncherApplication.getContext()
                .getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mCurVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean matchSemantic(String service) {
        return SemanticConstants.SERVICE_VOICE.equalsIgnoreCase(service);
    }

    @Override
    public boolean doSemantic(String json) {

        mVoiceManager.startUnderstanding();

        String semantic = JsonUtils.parseIatResult(json,
                "semantic");

        VoiceEntity voice = (VoiceEntity) GsonUtil
                .jsonToObject(semantic, VoiceEntity.class);

        VoiceSlots slots = voice.getSlots();

        String state = slots.getState();

        if (state.equals("+")) {
            turnUpVolume();
            mState = VolumeState.ADJUST;
        } else if (state.equals("-")) {
            turnDownVolume();
            mState = VolumeState.ADJUST;
        } else if (state.equals("++") && mState == VolumeState.ADJUST) {
            turnUpVolume();
        } else if (state.equals("--") && mState == VolumeState.ADJUST) {
            turnDownVolume();
        } else if (state.equals("mute")) {
            turnVolumeToValue(0);
        } else if (state.equals("max")) {
            turnVolumeToValue(mMaxVolume);
        } else if (state.equals("min")) {
            turnVolumeToValue(1);
        } else {
            mState = VolumeState.NORMAL;
            return false;
        }

        return true;
    }

    private void turnUpVolume() {
        mCurVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                mCurVolume + VOLUME_INCREMENTAL >= mMaxVolume ? mMaxVolume
                        : mCurVolume + VOLUME_INCREMENTAL,
                AudioManager.FLAG_PLAY_SOUND
                        | AudioManager.FLAG_SHOW_UI);
    }

    private void turnDownVolume() {
        mCurVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                mCurVolume - VOLUME_INCREMENTAL <= 0 ? 0
                        : mCurVolume - VOLUME_INCREMENTAL,
                AudioManager.FLAG_PLAY_SOUND
                        | AudioManager.FLAG_SHOW_UI);
    }


    private void turnVolumeToValue(int value) {
        mAudioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                value,
                AudioManager.FLAG_PLAY_SOUND
                        | AudioManager.FLAG_SHOW_UI);
    }

}
