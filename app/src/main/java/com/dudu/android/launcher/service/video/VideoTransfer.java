package com.dudu.android.launcher.service.video;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dudu.android.launcher.service.RecordBindService;
import com.dudu.android.launcher.utils.DeviceIDUtil;
import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.TimeUtils;
import com.dudu.http.MultipartRequest;
import com.dudu.http.MultipartRequestParams;
import com.dudu.network.event.UploadVideo;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * Created by dengjun on 2015/12/15.
 * Description :
 */
public class VideoTransfer {
    private Context mContext;
    private int video_interval = 2*30*1000;
    private ScheduledExecutorService sendServiceThreadPool = null;

    /* 标记是否上传视频*/
    private boolean uploadThreadRunFlag = false;
    private Logger log;
    private String uploadUrl = "http://192.168.0.50:8080/carVideoUpload";

    private RequestQueue queue;
    /* 用于存放文件路径*/
    private List<String > videoFileNameList;

    private RecordBindService recordBindService;

    public VideoTransfer(Context context, RecordBindService recordBindService) {
        mContext = context;
        this.recordBindService = recordBindService;

        queue = Volley.newRequestQueue(mContext);

        sendServiceThreadPool = Executors.newScheduledThreadPool(1);
        log = LoggerFactory.getLogger("video.service");

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);

        videoFileNameList = Collections.synchronizedList(new ArrayList<String>());

//        test();
    }

    public void test(){
        new Thread(){
            @Override
            public void run() {
                try {
                    log.debug("测试-------------");
                    sleep(10 * 1000);
                    restartRecordVideo(false);
                    sleep(2*1000);
                    uploadThreadRunFlag = true;//需要放到重启记录摄像之后
                    uploadVideo();
                } catch (InterruptedException e) {
                    log.error("异常：{}", e);
                }
            }
        }.start();
    }

    /* 处理视频上传事件*/
    public void onEventBackgroundThread(UploadVideo uploadVideo){
        log.info("收到并处理UploadVideo事件");
        if (uploadVideo.getObeId().equals(DeviceIDUtil.getIMEI(mContext))){
            confirmStartVideo();
            restartRecordVideo(false);
            uploadThreadRunFlag = true;//需要放到重启记录摄像之后
        }
    }

    /* 用新的间隔参数重启摄像*/
    private void restartRecordVideo(boolean optionFlag){
        log.info("用新的间隔参数重启摄像");
        recordBindService.stopRecord();
        if (optionFlag){
            recordBindService.getVideoConfigParam().resetToDefault();
        }else {
            recordBindService.getVideoConfigParam().setToUploadParam();
        }
        try {
            Thread.sleep(3*1000);
        } catch (InterruptedException e) {
            log.error("异常：{}", e);
        }
        recordBindService.startRecord();
    }

    /* 加入录像路径*/
    public void addVideoFileName(String videoFileName){
        if (videoFileName == null)
            return;
        if (uploadThreadRunFlag = true){
            log.debug("新加入文件：{}", videoFileName);
            videoFileNameList.add(videoFileName);
            synchronized (videoFileNameList){
                videoFileNameList.notifyAll();
            }
        }

    }

    /* 确认是否开始上传视频*/
    private void confirmStartVideo(){
        log.info("confirmStartVideo信息");
        MultipartRequestParams multiPartParams = new MultipartRequestParams();
        multiPartParams.put("confirmStartVideo", DeviceIDUtil.getIMEI(mContext));
        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, multiPartParams, uploadUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                log.info("confirmStartVideo响应信息：{}", response);
                proConfirmResInfo(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                log.error("confirmStartVideo错误响应：{}", error);
            }
        });
        queue.add(multipartRequest);
    }

    private void proConfirmResInfo(String confirmResInfo){
        if (confirmResInfo == null)
            return;
        try {
            JSONObject confirmResJsonobject = new JSONObject(confirmResInfo);
            String resultCode = confirmResJsonobject.getString("resultCode");
            String method = confirmResJsonobject.getString("method");
            String msg = confirmResJsonobject.getString("msg");
            if (resultCode.equals("200")){
                log.info("开启上传线程");
                uploadVideo();
            }
        } catch (JSONException e) {
            log.error("异常：{}", e);
        }
    }

    private  Thread uploadThread = new Thread(){
        @Override
        public void run() {
            while (uploadThreadRunFlag) {
                try {
                    String filePath = getNextFilepath();
                    if (filePath != null){
                        log.debug("上传视频文件名：{}", filePath);
                        doUploadVideo(filePath);

                        log.info("视频上传等待响应");
                        synchronized (uploadThread){
                            uploadThread.wait();
                            log.info("视频上传等待----结束");
                        }
                    }

                    /*if (true) {
                        log.debug("上传视频文件名：{}", "/storage/sdcard1/dudu/video/2015-12-16 17.05.35.mp4");
                        doUploadVideo("/storage/sdcard1/dudu/video/test.mp4");

                        log.info("视频上传等待响应");
                        synchronized (uploadThread){
                            uploadThread.wait();
                        }
                    }*/
                } catch (Exception e) {
                    log.error("异常：{}", e);
                }
            }
        }
    };

    private void uploadVideo(){
        sendServiceThreadPool.schedule(uploadThread, 30, TimeUnit.SECONDS);
//        sendServiceThreadPool.scheduleAtFixedRate(uploadThread, 10, 60, TimeUnit.SECONDS);
    }

    private void stopUploadThread(){
        if (sendServiceThreadPool != null && !sendServiceThreadPool.isShutdown()){
            try {
                sendServiceThreadPool.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("异常：{}", e);
            }
        }
    }


    private String getNextFilepath(){
        if (!videoFileNameList.isEmpty()){
            log.debug("获取到下一个视频文件地址");
            return videoFileNameList.remove(0);
        }else {
            synchronized (videoFileNameList){
                try {
                    log.debug("videoFileNameList 没有数据，等待有数据");
                    videoFileNameList.wait();
                    log.debug("videoFileNameList 没有数据，等待结束");
                    if (videoFileNameList.isEmpty())
                        return null;
                    return videoFileNameList.remove(0);
                } catch (InterruptedException e) {
                    log.error("异常：{}", e);
                    return null;
                }
            }
        }
    }


    private void doUploadVideo(String videoFileName){
        File videoFileToUpload = new File(videoFileName);
        log.debug("上传的视频文件大小：{}", FileUtils.fileByte2Mb(videoFileToUpload.length()));

        File file = new File(videoFileName);
        MultipartRequestParams multiPartParams = new MultipartRequestParams();
        multiPartParams.put("upload_video", file, file.getName());
        multiPartParams.put("obeId", DeviceIDUtil.getIMEI(mContext));
        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, multiPartParams, uploadUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        log.info("上传视频文件响应信息：{}", response);
                        log.debug("上传结束时间：{}", TimeUtils.format(TimeUtils.format1));
                        proUploadResponseInfo(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        log.error("上传视频文件错误响应：{}", error);
                        goOnUpload();
                    }
                });
        log.debug("上传开始时间：{}", TimeUtils.format(TimeUtils.format1));
        queue.add(multipartRequest);
    }

    private void proUploadResponseInfo(String response){
        if (response == null)
            return;
        try {
            JSONObject responseJsonObject = new JSONObject(response);
            String resultCode = responseJsonObject.getString("resultCode");
            String method = responseJsonObject.getString("method");
            String isContinueRecordVideo = responseJsonObject.getString("isContinueRecordVideo");
            String msg = responseJsonObject.getString("msg");
            if (resultCode.equals("200")){
                if (isContinueRecordVideo.equals("true")){
                    goOnUpload();
                }else {
                    stopUpload();
                }
            }else {
                log.info("上传响应 400");
            }
        } catch (JSONException e) {
            log.error("异常：{}", e);
        }
    }


    private void goOnUpload(){
        log.info("继续上传视频");
        synchronized (uploadThread) {
            uploadThread.notifyAll();
        }
        /*synchronized (videoFileNameList) {
            videoFileNameList.notifyAll();
        }*/
    }

    private void stopUpload(){
        log.info("结束上传视频");
        uploadThreadRunFlag = false;
        synchronized (uploadThread) {
            uploadThread.notifyAll();
        }
        synchronized (videoFileNameList) {
            videoFileNameList.notifyAll();
        }

        videoFileNameList.clear();

        stopUploadThread();

        restartRecordVideo(true);
    }

    public void release(){
        EventBus.getDefault().unregister(this);
    }
}
