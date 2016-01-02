package com.dudu.android.ext.media.domain;

import android.view.SurfaceHolder;

import com.dudu.android.ext.media.domain.services.VideoHelper;
import com.dudu.android.ext.media.interfaces.VideoManager;

public class VideoManagerImpl implements VideoManager {

    private static VideoManagerImpl mInstance;

    private VideoHelper mVideoHelper;

    public static VideoManagerImpl getInstance() {
        if (mInstance == null) {
            mInstance = new VideoManagerImpl();
        }

        return mInstance;
    }

    private VideoManagerImpl() {
        mVideoHelper = VideoHelper.getInstance();
    }

    @Override
    public void startPreview(SurfaceHolder holder) {

    }

    @Override
    public void stopPreview() {

    }

    @Override
    public void startRecord(SurfaceHolder holder) {
        mVideoHelper.startRecord(holder);
    }

    @Override
    public void stopRecord() {
        mVideoHelper.stopRecord();
    }

    @Override
    public boolean getRecordingAudio() {
        return mVideoHelper.getRecordingAudio();
    }

    @Override
    public void setRecordingAudio(boolean recordingAudio) {
        mVideoHelper.setRecordingAudio(recordingAudio);
    }

}
