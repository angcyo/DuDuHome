package com.dudu.voice.semantic.chain;

import android.content.Context;
import android.media.AudioManager;

import com.dudu.android.launcher.LauncherApplication;
import com.dudu.voice.semantic.bean.SemanticBean;
import com.dudu.voice.semantic.bean.VolumeBean;
import com.dudu.voice.semantic.constant.SemanticConstant;

/**
 * Created by 赵圣琪 on 2015/10/28.
 */
public class VolumeChain extends SemanticChain {

    public static final int VOLUME_INCREMENTAL = 3;

    private AudioManager mAudioManager;

    private int mMaxVolume;

    private int mCurVolume;

    private int mPreVolume;

    public VolumeChain() {
        super();
        mAudioManager = (AudioManager) LauncherApplication.getContext()
                .getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mCurVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean matchSemantic(String service) {
        return SemanticConstant.SERVICE_VOLUME.equalsIgnoreCase(service);
    }

    @Override
    public boolean doSemantic(SemanticBean semantic) {
        VolumeBean bean = (VolumeBean) semantic;
        String operation = bean.getOperation();

        if (operation.equals("+")) {
            turnUpVolume();
        } else if (operation.equals("-")) {
            turnDownVolume();
        } else if (operation.equals("max")) {
            turnVolumeToValue(mMaxVolume);
        } else if (operation.equals("min")) {
            turnVolumeToValue(1);
        } else if (operation.equals("mute_on")) {
            turnVolumeToValue(0);
        } else if (operation.equals("mute_off")) {
            return turnOnVolume();
        } else {
            return false;
        }

        mVoiceManager.startUnderstanding();
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
        if (value == 0) {
            mPreVolume = mCurVolume;
        }

        mCurVolume = value;
        mAudioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                value,
                AudioManager.FLAG_PLAY_SOUND
                        | AudioManager.FLAG_SHOW_UI);
    }

    private boolean turnOnVolume() {
        if (mCurVolume != 0) {
            return false;
        }

        mVoiceManager.startUnderstanding();

        mAudioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                mPreVolume,
                AudioManager.FLAG_PLAY_SOUND
                        | AudioManager.FLAG_SHOW_UI);
        return true;
    }

}
