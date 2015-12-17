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
    private ScheduledExecutorService sendServiceThreadPool = null;

    /* 标记是否上传视频*/
    private boolean uploadThreadRunFlag = false;
    private Logger log;
    private String uploadUrl = "http://119.29.65.127/carVideoUpload";

    private RequestQueue queue;
    /* 用于存放文件路径*/
    private List<String > videoFileNameList;

    private RecordBindService recordBindService;

    private VideoConfirmRequest videoConfirmRequest;

    public VideoTransfer(Context context, RecordBindService recordBindService) {
        mContext = context;
        this.recordBindService = recordBindService;

        queue = Volley.newRequestQueue(mContext);

        sendServiceThreadPool = Executors.newScheduledThreadPool(1);
        log = LoggerFactory.getLogger("video.service");

        EventBus.getDefault().unregister(this);
        EventBus.getDefault().register(this);

        videoFileNameList = Collections.synchronizedList(new ArrayList<String>());

        videoConfirmRequest = new VideoConfirmRequest( this,mContext);

//        test();
    }

    public void test(){
        new Thread(){
            @Override
            public void run() {
                try {
                    while (true){
                        sleep(15 * 1000);
                        videoConfirmRequest.confirmStartVideo();
                        sleep(15 * 1000);
                    }

//                    log.debug("测试-------------");
//                    sleep(15 * 1000);

//                    EventBus.getDefault().post(new UploadVideo(mContext));

                  /*  restartRecordVideo(false);
                    sleep(2*1000);
                    uploadThreadRunFlag = true;//需要放到重启记录摄像之后
                    uploadVideo();*/
                } catch (InterruptedException e) {
                    log.error("异常：{}", e);
                }
            }
        }.start();
    }

    /* 处理视频上传事件*/
    public void onEventBackgroundThread(UploadVideo uploadVideo){
        log.info("收到并处理UploadVideo事件");
       /* if (uploadThreadRunFlag == true){
            log.info("已经在上传视频了---");
            return;
        }*/

        stopUploadThread();//收到事件如果上传线程在运行先停掉

        videoFileNameList.clear();
        if (uploadVideo.getObeId().equals(DeviceIDUtil.getIMEI(mContext))){
            uploadThreadRunFlag = true;

            videoConfirmRequest.confirmStartVideo();

            restartRecordVideo(false);
//            uploadThreadRunFlag = true;//需要放到重启记录摄像之后

            //测试的时候用
//            uploadVideo();
        }
    }

    /* 用新的间隔参数重启摄像*/
    private void restartRecordVideo(boolean optionFlag){
        log.info("用新的间隔参数重启摄像  optionFlag = {}", optionFlag);
        recordBindService.stopRecord();
        if (optionFlag){
            recordBindService.getVideoConfigParam().resetToDefault();
        }else {
            recordBindService.getVideoConfigParam().setToUploadParam();
        }
        try {
            Thread.sleep(5*1000);
        } catch (InterruptedException e) {
            log.error("异常：{}", e);
        }
        recordBindService.startRecord();
    }

    /* 加入录像路径*/
    public void addVideoFileName(String videoFileName){
       /* if (videoFileNameList.size() > 5){
            log.info("待发送实时视频大于5个，删除第一个");
            videoFileNameList.remove(0);
        }else if (videoFileNameList.size() > 10){
            log.info("待发送视频个数大于10个，恢复正常行车记录");
            videoFileNameList.clear();
            restartRecordVideo(true);
        }*/

        if (videoFileName == null)
            return;
        if (uploadThreadRunFlag == true){
            log.debug("新加入文件：{}", videoFileName);
            videoFileNameList.add(videoFileName);
            synchronized (videoFileNameList){
                videoFileNameList.notifyAll();
            }
        }

    }


    private  Thread uploadThread = new Thread(){
        @Override
        public void run() {
            log.info("视频上传线程开始运行----------");
            while (uploadThreadRunFlag) {
                try {
                    String filePath = getNextFilepath();
                    if (filePath != null){
                        String fileLength = FileUtils.fileByte2Mb(new File(filePath).length());
                        log.debug("上传视频  长度：{} M，文件名：{}",fileLength, filePath);
                        if (Float.valueOf(fileLength) < 0.8 || Float.valueOf(fileLength)  > 4){
                            log.info("文件长度 length < 0.8 || length > 4，过滤掉");
                            continue;
                        }

                        doUploadVideo(filePath);

                        log.info("视频上传等待响应");
                        synchronized (uploadThread){
                            uploadThread.wait();
                            log.info("视频上传等待----结束");
                        }
                    }
                } catch (Exception e) {
                    log.error("异常：{}", e);
                }
            }
        }
    };

    public void uploadVideo(){
        log.info("开启上传线程");

        if (sendServiceThreadPool == null){
            sendServiceThreadPool = Executors.newScheduledThreadPool(1);
        }
        sendServiceThreadPool.schedule(uploadThread, 1, TimeUnit.SECONDS);
//        sendServiceThreadPool.execute(uploadThread);
//        sendServiceThreadPool.scheduleAtFixedRate(uploadThread, 10, 60, TimeUnit.SECONDS);

    }

    private void stopUploadThread(){
        if (sendServiceThreadPool != null && !sendServiceThreadPool.isShutdown()){
            try {
//                sendServiceThreadPool.awaitTermination(30, TimeUnit.SECONDS);
                sendServiceThreadPool.shutdown();
                sendServiceThreadPool = null;
            } catch (Exception e) {
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
        /*String  fileLength = FileUtils.fileByte2Mb(videoFileToUpload.length());
        log.debug("上传的视频文件大小：{}", fileLength);
        float length = Float.valueOf(fileLength);
        if (length < 0.8 || length > 3){
            log.info("文件长度 length < 0.8 || length > 4，过滤掉");
            return;
        }*/

        MultipartRequestParams multiPartParams = new MultipartRequestParams();
        multiPartParams.put("upload_video", videoFileToUpload, videoFileToUpload.getName());
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
                        try {
                            if (error != null){
                                log.error("上传视频文件错误响应：{}", error.toString());
                                goOnUpload();
                            }else{
                                log.error("上传视频文件错误响应  null");
                            }
                        } catch (Exception e) {
                            log.error("异常：{}", e);
                        }
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
        }catch (Exception e){
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
