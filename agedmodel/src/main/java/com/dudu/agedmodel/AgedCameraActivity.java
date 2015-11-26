package com.dudu.agedmodel;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.dudu.utils.Contacts;
import com.dudu.utils.FileUtils;

import java.io.File;

public class AgedCameraActivity extends NoTitleActivity implements SurfaceHolder.Callback{

    private AgedCameraActivity mActivity;

    private SurfaceView surfaceView;

    private Camera camera=null;

    private MediaRecorder mediaRecorder;

    private SurfaceHolder surfaceHolder;
    //录像文件的名字
    public static String videoName = "agedVideoShow"+".mp4";
    //录像保存的路径
    private String agedVideoPath="";

    private Handler handler=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=this;
        setContentView(R.layout.activity_aged_camera);
        initView();
        initData();
    }
    private void initView() {
        surfaceView=(SurfaceView)findViewById(R.id.camera_surface);
    }
    private void initData() {
        File file=new File(FileUtils.getExternalStorageDirectory(),"agedVideoFolder");
        if(!file.exists()){
            file.mkdirs();
        }
        agedVideoPath=file.getPath();
       handler=new MyHandler();
    }
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(mActivity);
        new CameraTask().execute();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.surfaceHolder = holder;

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.surfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    /**
     * 开始录像的异步类
     * */
    private class CameraTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            if (prepareMediaRecorder()) {
                mediaRecorder.start();
                handler.sendEmptyMessageDelayed(0,10000);
            } else {
                releaseMediaRecorder();
            }
            return null;
        }

    }
    private boolean prepareMediaRecorder() {
        /**
         * 设置录像的相关参数
         * */
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        Camera.Parameters p = camera.getParameters();
        p.setPreviewFormat(PixelFormat.YCbCr_420_SP);
        p.setPictureSize(1280, 720);
        camera.setParameters(p);
        //准备录像机
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                try {
                    if (mr != null)
                        mr.reset();
                } catch (Exception e) {
                    Log.e("ji", "stopRecord: " + e.getMessage());
                }
            }
        });
        //录像的相关参数的设置
        camera.unlock();
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        mediaRecorder.setCamera(camera);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        mediaRecorder.setVideoSize(640, 480);
        if (profile.videoBitRate > 2 * 1024 * 1024)
            mediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
        else
            mediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);

        mediaRecorder.setVideoFrameRate(30);

        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //设置录像保存的目录和名字
        mediaRecorder.setOutputFile(agedVideoPath + File.separator + videoName);

        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            Log.e("ji", e.getMessage());
            return false;
        }
        return true;
    }
    /**
     * 当退出程序时：要释放录像和播放器的内存；
     * */
    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                if (camera != null) {
                    camera.lock();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaRecorder();
        releaseCamera();
    }
    /**
     * 释放摄像机的内存
     * */
    private void releaseCamera() {
        if (camera != null) {
            try {
                camera.release();
                camera = null;
            } catch (Exception e) {

            }
        }
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            Intent intent=new Intent(mActivity,AgedModelMainActivity.class);
            intent.putExtra(Contacts.CLASS_TYPE,Contacts.CAMERA_TYPE);
            startActivity(intent);
            finish();
        }
    }

}
