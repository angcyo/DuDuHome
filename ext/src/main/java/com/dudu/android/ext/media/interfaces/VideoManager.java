package com.dudu.android.ext.media.interfaces;

import android.view.SurfaceHolder;

/**
 * Created by Administrator on 2015/12/9.
 */
public interface VideoManager {

    void startPreview(SurfaceHolder holder);

    void stopPreview();

    void startRecord(SurfaceHolder holder);

    void stopRecord();

    boolean getRecordingAudio();

    void setRecordingAudio(boolean recordingAudio);

}
