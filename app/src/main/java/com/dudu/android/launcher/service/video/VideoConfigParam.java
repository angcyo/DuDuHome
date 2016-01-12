package com.dudu.android.launcher.service.video;

import android.media.CamcorderProfile;

/**
 * Created by dengjun on 2015/12/16.
 * Description : 录像视频参数
 */
public class VideoConfigParam {
    /* 默认录像间隔*/
    public static final int DEFAULT_VIDEO_INTERVAL = 5*60*1000;

    public static final   int DEFAULT_VIDEOBITRATE = 2 * 1024 * 1024;//2M
    public static  final  int DEFAULT_WIDTH = 1280;
    public static  final  int DEFAULT_HEIGHT = 720;
    public static  final  int DEFAULT_RATE= 30;
    public static  final  int DEFAULT_QUALITY= CamcorderProfile.QUALITY_HIGH;

    public static final int DEFAULT_UPLOAD_VIDEO_INTERVAL = 1*10*1000;
    public static  final int DEFAULT_UPLOAD_VIDEOBITRATE = 300 * 1024;//512k
    public static  final  int DEFAULT_UPLOAD_WIDTH = 352;
    public static  final  int DEFAULT_UPLOAD_HEIGHT = 288;
    public static  final  int DEFAULT_UPLOAD_RATE = 15;
    public static  final  int DEFAULT_UPLOAD_QUALITY = CamcorderProfile.QUALITY_CIF;

    private   int video_interval;
    /*video output bit rate */
    private int videoBitRate;
    /* */
    private int width;
    /* */
    private int height;
    /* */
    private int rate;
    /* */
    private int quality;


    public VideoConfigParam() {
        video_interval = DEFAULT_VIDEO_INTERVAL;
        videoBitRate = DEFAULT_VIDEOBITRATE;
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        rate = DEFAULT_RATE;
        quality = DEFAULT_QUALITY;
    }

    /* 设置成正常录制视频的参数*/
    public void resetToDefault(){
        video_interval = DEFAULT_VIDEO_INTERVAL;
        videoBitRate = DEFAULT_VIDEOBITRATE;
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        rate = DEFAULT_RATE;
        quality = DEFAULT_QUALITY;
    }

    /* 设置成实时上传录制视频的参数*/
    public void setToUploadParam(){
        video_interval = DEFAULT_UPLOAD_VIDEO_INTERVAL;
        videoBitRate = DEFAULT_UPLOAD_VIDEOBITRATE;
        width = DEFAULT_UPLOAD_WIDTH;
        height = DEFAULT_UPLOAD_HEIGHT;
        rate = DEFAULT_UPLOAD_RATE;
        quality = DEFAULT_UPLOAD_QUALITY;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public int getVideoBitRate() {
        return videoBitRate;
    }

    public void setVideoBitRate(int videoBitRate) {
        this.videoBitRate = videoBitRate;
    }

    public int getVideo_interval() {
        return video_interval;
    }

    public void setVideo_interval(int video_interval) {
        this.video_interval = video_interval;
    }
}
