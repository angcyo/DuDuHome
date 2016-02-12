package com.dudu.drivevideo.video;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.dudu.drivevideo.config.RearVideoConfigParam;
import com.dudu.drivevideo.utils.FileUtil;
import com.dudu.drivevideo.utils.TimeUtils;
import com.hclydao.webcam.Ffmpeg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.ByteBuffer;


/**
 * Created by dengjun on 2016/1/27.
 * Description :
 */
public class RearCameraDriveVideo {
    private RearVideoConfigParam rearVideoConfigParam;
    private String curVideoFileAbsolutePath;
    private int deviceId = 2;
    private String deviceName;

    private byte[] frameDataBuffer;
    private byte[] bitmapDataBuffer;
    private int numBuffer = 4;
    private int bufferIndex = 0;

    private Bitmap bitmap;
    private ByteBuffer imageBuffer;

    private boolean preViewFlag = false;
    private ImageView imageView = null;

    private Logger log;

    public RearCameraDriveVideo(RearVideoConfigParam rearVideoConfigParam) {
        this.rearVideoConfigParam = rearVideoConfigParam;

        log = LoggerFactory.getLogger("video.drivevideo");
    }

    /* 初始化摄像头*/
    public int initCamera(String deviceName){
//        this.deviceId = deviceId;
        this.deviceName = deviceName;
        frameDataBuffer = new byte[rearVideoConfigParam.getWidth() * rearVideoConfigParam.getHeight() * 2];
        bitmapDataBuffer = new byte[rearVideoConfigParam.getWidth()*2 *rearVideoConfigParam.getHeight()*2 * 2];

        bitmap = Bitmap.createBitmap(rearVideoConfigParam.getWidth() * 2, rearVideoConfigParam.getHeight() * 2, Bitmap.Config.RGB_565);
        imageBuffer = ByteBuffer.wrap(bitmapDataBuffer);

        return initDriveVideo();
    }

    private int initDriveVideo(){
        int initValue = -1;

        initValue = Ffmpeg.open(deviceName);
        log.info("打开摄像头：{}", initValue);
        if (initValue < 0){
            return  initValue;
        }

        initValue = Ffmpeg.init(rearVideoConfigParam.getWidth(), rearVideoConfigParam.getHeight(), numBuffer);
        log.info("初始化摄像头：{}", initValue);
        if (initValue < 0){
            return  initValue;
        }

        initValue = Ffmpeg.streamon();
        log.info("开启录像数据流：{}", initValue);

        return initValue;
    }

    public int startDriveVideo(){
        return startRecord();
    }

    public int readFrameTobuffer(){
        return Ffmpeg.videostart(frameDataBuffer);
    }

    public int startRecord(){
        int startResult = -1;
        curVideoFileAbsolutePath = generateCurVideoName();
        log.info("后置摄像当前录像路径：{}", curVideoFileAbsolutePath);

        startResult = Ffmpeg.videoinit(curVideoFileAbsolutePath.getBytes());
        log.info("正式开始录像：{}", startResult);
        return startResult;
    }

    public int updateImageView(){
        bufferIndex = Ffmpeg.dqbuf(frameDataBuffer);
//        log.debug("刷新UI  bufferIndex ：{}", bufferIndex);
        if (bufferIndex < 0){
            return bufferIndex;
        }

        int returnResult = Ffmpeg.yuvtorgb(frameDataBuffer, bitmapDataBuffer,
                                    rearVideoConfigParam.getWidth()*2, rearVideoConfigParam.getHeight()*2);
//        log.debug("刷新UI  yuvtorgb returnResult：{}", returnResult);
        if (returnResult >= 0){
            if (preViewFlag){
                postInvalidate();
            }
        }
        Ffmpeg.qbuf(bufferIndex);
        return 0;
    }

    private void postInvalidate(){
        if (imageView != null){
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bitmap.copyPixelsFromBuffer(imageBuffer);
                        if (imageView != null){
                            imageView.setImageBitmap(bitmap);
                        }
                        imageBuffer.clear();
                    } catch (Exception e) {
                        log.error("异常", e);
                    }
                }
            });
        }
    }

    public void startPreview(){
        preViewFlag = true;
    }

    public void stopPreview(){
        preViewFlag = false;
    }



    public void releaseCamera(){
        Ffmpeg.release();
    }

    public void stopRecord(){
        Ffmpeg.videoclose();
    }

    public String getCurVideoFileAbsolutePath() {
        return curVideoFileAbsolutePath;
    }

    public void setCurVideoFileAbsolutePath(String curVideoFileAbsolutePath) {
        this.curVideoFileAbsolutePath = curVideoFileAbsolutePath;
    }

    public RearVideoConfigParam getRearVideoConfigParam() {
        return rearVideoConfigParam;
    }

    public void setRearVideoConfigParam(RearVideoConfigParam rearVideoConfigParam) {
        this.rearVideoConfigParam = rearVideoConfigParam;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    private String generateCurVideoName(){
        return FileUtil.getTFlashCardDirFile("/dudu",RearVideoConfigParam.VIDEO_STORAGE_PATH).getAbsolutePath()
                + File.separator+ TimeUtils.format(TimeUtils.format5)+".mpeg";
    }
}
