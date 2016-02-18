package com.dudu.drivevideo.service;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.dudu.drivevideo.broadcast.TFlashCardReceiver;
import com.dudu.drivevideo.storage.VideoFileManage;
import com.dudu.drivevideo.utils.FileUtil;
import com.dudu.drivevideo.video.FrontCameraDriveVideo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by dengjun on 2016/2/13.
 * Description :
 */
public class FrontDriveVideoService {
    private static final int INIT_CAMERA = 0;
    private static final int START_RECORD = 1;

    private FrontCameraDriveVideo frontCameraDriveVideo;
    private TFlashCardReceiver tFlashCardReceiver;

    private ScheduledExecutorService driveVideoThreadPool = null;
    private Logger log;

    private DriveVideoHandler driveVideoHandler;


    public FrontDriveVideoService() {
        log = LoggerFactory.getLogger("video.frontdrivevideo");

        frontCameraDriveVideo = new FrontCameraDriveVideo();
        tFlashCardReceiver = new TFlashCardReceiver();

        driveVideoHandler = new DriveVideoHandler(/*handlerThread.getLooper()*/);

        initThreadPool();
    }



    private Thread guardThread = new Thread(){
        @Override
        public void run() {
            try {
                log.debug("运行守护线程");
                if (frontCameraDriveVideo.isOpenedCamera()){
                    restartRecord();
                }else {
                    log.debug("发送初始化摄像头消息");
                    driveVideoHandler.sendEmptyMessage(INIT_CAMERA);
                }

                VideoFileManage.getInstance().guardTFCardSpace();
            } catch (Exception e) {
                log.error("异常：", e);
                frontCameraDriveVideo.stopDrvieVideo();
            }
        }
    };

    private class DriveVideoHandler extends Handler {

        public DriveVideoHandler(/*Looper looper*/) {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_CAMERA:
                    log.info("处理INIT_CAMERA消息");
                    initCamera();
                    break;
                case START_RECORD:
                    log.info("处理START_RECORD消息");
                    frontCameraDriveVideo.startRecord();
                    if (!frontCameraDriveVideo.isRecording()){
                        driveVideoHandler.sendEmptyMessageDelayed(START_RECORD, 1*1000);
                    }
                    break;
            }
        }
    }

    private void initCamera(){
        try {
            frontCameraDriveVideo.initCamera();
            if (frontCameraDriveVideo.isOpenedCamera()){
              driveVideoHandler.sendEmptyMessage(START_RECORD);
            }else {
                frontCameraDriveVideo.releaseCamera();
            }
        } catch (Exception e) {
            log.error("异常：", e);
        }
    }

    private void restartRecord(){
        log.debug("重启前置摄像头录像");
        frontCameraDriveVideo.stopRecord();
        String videoFileAbPath = frontCameraDriveVideo.getCurVideoFileAbsolutePath();
        frontCameraDriveVideo.startRecord();
        VideoFileManage.getInstance().saveVideoInfo(videoFileAbPath);
    }

    public void startDriveVideo(){
        log.debug("开启前置摄像头行车记录");
        tFlashCardReceiver.registReceiver();
        driveVideoThreadPool.scheduleAtFixedRate(guardThread, 0, frontCameraDriveVideo.getFrontVideoConfigParam().getVideoInterval()/1000, TimeUnit.SECONDS);
    }


    public void stopDriveVideo(){
        shutdownThreadPool();
        frontCameraDriveVideo.stopDrvieVideo();
    }



    public void initThreadPool(){
        if (driveVideoThreadPool == null){
            driveVideoThreadPool = Executors.newScheduledThreadPool(1);
        }
    }

    private void shutdownThreadPool(){
        if (driveVideoThreadPool != null && !driveVideoThreadPool.isShutdown()){
            driveVideoThreadPool.shutdownNow();
            driveVideoThreadPool = null;
        }
    }


    public FrontCameraDriveVideo getFrontCameraDriveVideo() {
        return frontCameraDriveVideo;
    }
}
