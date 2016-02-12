package com.dudu.drivevideo.service;

import android.os.Looper;

import com.dudu.drivevideo.broadcast.TFlashCardReceiver;
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
    private FrontCameraDriveVideo frontCameraDriveVideo;
    private TFlashCardReceiver tFlashCardReceiver;

    private ScheduledExecutorService driveVideoThreadPool = null;
    private Logger log;

    private boolean isOpenedCamera = false;
    private boolean isRecording = false;

    public FrontDriveVideoService() {
        log = LoggerFactory.getLogger("video.frontdrivevideo");

        frontCameraDriveVideo = new FrontCameraDriveVideo();
        tFlashCardReceiver = new TFlashCardReceiver();

        initThreadPool();
    }


    private Thread drvieVideoThread = new Thread(){
        @Override
        public void run() {
            try {
                log.debug("运行drvieVideoThread----");
                if (isOpenedCamera && FileUtil.isTFlashCardExists()){
                    restartRecord();
                }
            } catch (Exception e) {
                log.error("异常：", e);
            }
        }
    };

    private Thread guardThread = new Thread(){
        @Override
        public void run() {
            Looper.prepare();
            try {
                if (isOpenedCamera == false){
                    if (frontCameraDriveVideo.initCamera()){
                        isOpenedCamera = true;
//                        startRecord();
                    }else {
                        frontCameraDriveVideo.releaseCamera();
                    }
                }else {
                    if (isRecording == false){
//                       startRecord();
                    }
                }
            } catch (Exception e) {
                log.error("异常：", e);
                frontCameraDriveVideo.stopDrvieVideo();
                isOpenedCamera = false;
            }
            Looper.loop();
        }
    };

    private void startRecord(){
        if(frontCameraDriveVideo.startRecord()){
            isRecording = true;
        }else {
            frontCameraDriveVideo.releaseMediaRecorder();
        }
    }


    private void restartRecord(){
        log.debug("重启前置摄像头录像");
        frontCameraDriveVideo.stopRecord();
//        frontCameraDriveVideo.stopDrvieVideo();

        frontCameraDriveVideo.startRecord();
    }

    public void startDriveVideo(){
        log.debug("开启前置摄像头行车记录");
        tFlashCardReceiver.registReceiver();

        driveVideoThreadPool.scheduleAtFixedRate(guardThread, 0, 10, TimeUnit.SECONDS);
//        scheduleDrvieVideoThread(frontCameraDriveVideo.getFrontVideoConfigParam().getVideoInterval() / 1000);
    }

    private void scheduleDrvieVideoThread(int period){
        driveVideoThreadPool.scheduleAtFixedRate(drvieVideoThread, period, period, TimeUnit.SECONDS);
    }

    private void tryInitCamera(){
        Observable.timer(5, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                if (!isOpenedCamera){
                    log.info("再次尝试初始化摄像头");
                    isOpenedCamera = frontCameraDriveVideo.initCamera();
                }
            }
        });
    }


    public void stopDriveVideo(){
        shutdownThreadPool();
        frontCameraDriveVideo.stopDrvieVideo();
    }



    public void initThreadPool(){
        if (driveVideoThreadPool == null){
            driveVideoThreadPool = Executors.newScheduledThreadPool(2);
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
