package com.dudu.drivevideo.video;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dudu.drivevideo.DriveVideo;
import com.dudu.drivevideo.video.FrontCameraDriveVideo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by dengjun on 2016/2/16.
 * Description :
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    private Logger log;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        log = LoggerFactory.getLogger("video.drivevideo");

        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
//        mHolder = getHolder();
//        mHolder.addCallback(this);

        getHolder().addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
//        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        mHolder = holder;
        try {
            log.debug("surfaceCreated创建");
            mCamera.stopPreview();
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

            DriveVideo.getInstance().getFrontCameraDriveVideo().startRecord();
        } catch (IOException e) {
            log.error("异常：", e);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
     /*   log.debug("surfaceChanged改变");
        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }
        log.debug("surfaceChanged改变----");
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            log.error("异常：", e);
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            log.error("异常：", e);
        }*/
    }



    public void startPreview() {
        log.debug("前置开启预览");

        mCamera.startPreview();
    }

    public void setSurfaceHolder(){
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            log.error("异常", e);
        }
    }

    public void stopPreview(){
        log.debug("前置关闭预览");
        mCamera.stopPreview();
    }

    public SurfaceHolder getmHolder() {
        return mHolder;
    }
}
