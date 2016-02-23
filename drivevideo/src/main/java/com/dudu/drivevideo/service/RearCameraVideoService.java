package com.dudu.drivevideo.service;



import com.dudu.drivevideo.config.RearVideoConfigParam;
import com.dudu.drivevideo.config.VideoConfigParam;
import com.dudu.drivevideo.utils.FileUtil;
import com.dudu.drivevideo.utils.UsbControl;
import com.dudu.drivevideo.video.RearCameraDriveVideo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by dengjun on 2016/1/26.
 * Description :
 */
public class RearCameraVideoService {
    private VideoConfigParam videoConfigParam;
    private RearCameraDriveVideo rearCameraDriveVideo;

    private boolean isDriveVideoIng = false;
    private boolean isRecordingVideo = false;
    private boolean isCanStartRecording = true;

    private boolean isUsbVideoEnabled = false;


    private ScheduledExecutorService driveVideoThreadPool = null;
    private Logger log;


    public RearCameraVideoService() {
        videoConfigParam = new VideoConfigParam();
        rearCameraDriveVideo = new RearCameraDriveVideo(videoConfigParam.getRearVideoConfigParam());

        initThreadPool();
        log = LoggerFactory.getLogger("video.reardrivevideo");
    }

    private Thread initDriveVideoThread = new Thread(){
        @Override
        public void run() {
            try {
                log.info("设置usb setToHost：{}", UsbControl.setToHost());
                FileUtil.getTFlashCardDirFile("/dudu",RearVideoConfigParam.VIDEO_STORAGE_PATH);
                log.info("运行initDriveVideoThread");
                if (initDriveVideo()){
                    log.debug("初始化后置摄像头成功");
                    isDriveVideoIng = true;
                    driveVideoThreadPool.execute(videoThread);
                }else {
                    log.debug("初始化后置摄像头失败，10秒尝试重新初始化");
                    driveVideoThreadPool.schedule(initDriveVideoThread, 10, TimeUnit.SECONDS);
                }
            } catch (Exception e) {
                log.error("异常", e);
                releaseAndReStartVideo();
//                log.info("设置usb setToClient：{}", UsbControl.setToClient());
            }
        }
    };

    private boolean initDriveVideo(){
        boolean isInitDriveVideoOk = false;
        List<String> videoFileNameList = FileUtil.getDirFileNameList("/dev", "video");
        if (videoFileNameList.size() > 0){
            for (String fileName: videoFileNameList){
//                log.debug("filename : {}", fileName);
                if (!("".equals(fileName) || (fileName.length() <= 5)|| "video32".equals(fileName)
                        || "video33".equals(fileName) || "video0".equals(fileName) || "video1".equals(fileName))){

                    int deviceId = Integer.parseInt(fileName.trim().substring(5));
                    log.debug("当前设备名：{}, 设备ID：{}", fileName, deviceId);
                    if (rearCameraDriveVideo.initCamera("/dev/"+fileName) >= 0){
                        isInitDriveVideoOk = true;
                        break;
                    }else {
                        log.debug("初始化失败，结束本次录像");
                        rearCameraDriveVideo.releaseCamera();
                    }
                }
            }
        }
        return isInitDriveVideoOk;
    }

    private Thread videoThread = new Thread(){
        @Override
        public void run() {
            try {
                isCanStartRecording = true;
                while (isDriveVideoIng) {
                    if (rearCameraDriveVideo.updateImageView() >= 0){
                        if (isCanStartRecording){
                            isCanStartRecording = false;
//                            driveVideoThreadPool.execute(recordingThread);
                        }
                    }else {
                        log.info("updateImageView失败");
                        isRecordingVideo = false;
                        isDriveVideoIng = false;
                    }
                }
                log.debug("后置摄像头结束本次录像");
                rearCameraDriveVideo.releaseCamera();
                notifyStartNextRecord();
            } catch (Exception e) {
                log.error("异常", e);
                releaseAndReStartVideo();
            }
        }
    };


    private Thread recordingThread = new Thread() {
        @Override
        public void run() {
            try {
                log.info("运行driveVideoThread");

                if (rearCameraDriveVideo.startRecord() >= 0){
                    isRecordingVideo = true;
                    log.debug("后置摄像头开始录制");
                    while (isRecordingVideo){
//                        log.debug("readFrameTobuffer");
                        rearCameraDriveVideo.readFrameTobuffer();
                    }
                    log.debug("后置摄像头结束视频录制");
                    rearCameraDriveVideo.stopRecord();

                    notifyStartNextRecord();
                }else {
                    log.info("后置摄像头开启视频录制失败");
                    isRecordingVideo = false;
                    isDriveVideoIng = false;//开启录像失败时，必须释放摄像头
                    rearCameraDriveVideo.releaseCamera();
                }
            } catch (Exception e) {
                log.error("异常", e);
                releaseAndReStartVideo();
            }
        }
    };

    private void notifyStartNextRecord(){
        synchronized (monitorDriveVideoThread){
            log.debug("通知可以开启下一个录像");
            monitorDriveVideoThread.notify();
        }
    }

    private void waitStartNextRecordnotify(){
        try {
            synchronized (monitorDriveVideoThread){
                log.debug("等待前面录像停止");
                monitorDriveVideoThread.wait(5*1000);
            }
        } catch (InterruptedException e) {
            log.error("异常：", e);
        }
    }


    private Thread monitorDriveVideoThread = new Thread(){
        @Override
        public void run() {
            try {
                log.info("运行monitorDriveVideoThread");

                if (isDriveVideoIng){
                    isRecordingVideo = false;
                    waitStartNextRecordnotify();
//                    driveVideoThreadPool.execute(recordingThread);
                }else {
                    log.debug("后置摄像头重新初始化");
                    rearCameraDriveVideo.releaseCamera();

                    waitStartNextRecordnotify();
                    driveVideoThreadPool.execute(initDriveVideoThread);
                }
            } catch (Exception e) {
                log.error("异常：", e);
                releaseAndReStartVideo();
            }
        }
    };

    private void sheduleMonitorThread(int period){
        driveVideoThreadPool.scheduleAtFixedRate(monitorDriveVideoThread, period, period, TimeUnit.SECONDS);
    }

    private void releaseAndReStartVideo(){
        rearCameraDriveVideo.releaseCamera();
        driveVideoThreadPool.schedule(initDriveVideoThread, 5, TimeUnit.SECONDS);
    }

    public void startDriveVideo(){
        if (!isUsbVideoEnabled){
            log.info("USB摄像头未使能");
            return;
        }
        log.info("开启startDriveVideo UsbControl.setToHost() {}", UsbControl.setToHost());
        if (!isDriveVideoIng){
            startRecordingVideo();
        }
    }

    private void startRecordingVideo(){
        initThreadPool();
        driveVideoThreadPool.execute(initDriveVideoThread);
        sheduleMonitorThread(15);
    }

    public void stopDriveVideo(){
        log.info("停止stopDriveVideo UsbControl.setToClient() {}", UsbControl.setToClient());
        stopRecordingVideo();
    }

    private void stopRecordingVideo(){
        isDriveVideoIng = false;
        isRecordingVideo = false;
        shutdownThreadPool();
        rearCameraDriveVideo.releaseCamera();
    }

    public void initThreadPool(){
        if (driveVideoThreadPool == null){
            driveVideoThreadPool = Executors.newScheduledThreadPool(3);
        }
    }

    private void shutdownThreadPool(){
        if (driveVideoThreadPool != null && !driveVideoThreadPool.isShutdown()){
            driveVideoThreadPool.shutdownNow();
            driveVideoThreadPool = null;
        }
    }


    public boolean isUsbVideoEnabled() {
        return isUsbVideoEnabled;
    }

    public void setIsUsbVideoEnabled(boolean isUsbVideoEnabled) {
        this.isUsbVideoEnabled = isUsbVideoEnabled;
    }

    public RearCameraDriveVideo getRearCameraDriveVideo() {
        return rearCameraDriveVideo;
    }

}
