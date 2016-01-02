package com.dudu.android.ext.media.domain.services;

/**
 * Created by Administrator on 2016/1/2.
 */
public class VideoStatus {
    public static final int ON = 1;
    public static final int OFF = 0;

    private int state;

    public VideoStatus(final int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }


}
