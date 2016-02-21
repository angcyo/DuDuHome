package com.dudu.video;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.dudu.android.hideapi.SystemPropertiesProxy;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;

import com.dudu.android.launcher.db.DbHelper;
import com.dudu.android.launcher.model.VideoEntity;
import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.event.DeviceEvent;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;


/**
 * Created by 赵圣琪 on 2015/12/9.
 */
public class VideoManager implements SurfaceHolder.Callback, MediaRecorder.OnErrorListener {

    /* 录像视频参数*/
    private VideoConfigParam videoConfigParam;

    private static final int VIDEO_CACHE_FULL = 0;
    private static final int VIDEO_START_RETRY = 1;

    private static final int VIDEO_RETRY_INTERVAL = 3000;

    public static VideoManager mInstance;

    private Camera mCamera;

    private MediaRecorder mMediaRecorder;

    private Timer mTimer;

    private TimerTask mTimerTask;

    private boolean mRecording = false;

    private String mVideoStoragePath;

    private float mVideoCacheMaxSize;

    private String mVideoName;

    private DbHelper mDbHelper;

    private VideoHandler mHandler;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss",
            Locale.getDefault());

    private Context mContext;

    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mLayoutParams;

    private View mVideoView;

    private SurfaceHolder mHolder;

    private VideoTransfer mVideoTransfer;

    private Logger log;

    /* 当前录制视频的文件名*/
    private String curVideoName;

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        log.info("MediaRecorder 错误：what：{}，extra：{} ", what, extra);
    }

    private class VideoHandler extends Handler {
        public VideoHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VIDEO_CACHE_FULL:
                    //存储空间已满，请手动释放存储空间!

                    break;
                case VIDEO_START_RETRY:
                    setUpCamera();

                    startRecord();
                    break;
            }
        }
    }

    /*public static VideoManager getInstance() {
        if (mInstance == null) {
            mInstance = new VideoManager();
        }

        return mInstance;
    }*/

    private VideoManager() {
        log = LoggerFactory.getLogger("video.VideoManager");

        videoConfigParam = new VideoConfigParam();

        mContext = LauncherApplication.getContext();

        mDbHelper = DbHelper.getDbHelper();

        mVideoStoragePath = FileUtils.getVideoStorageDir().getAbsolutePath();
        log.debug("录像存储的路径: " + mVideoStoragePath);

        if (FileUtils.isTFlashCardExists()) {
            mVideoCacheMaxSize = Float.parseFloat(FileUtils.fileByte2Mb(
                    FileUtils.getTFlashCardSpace()));
            log.debug("录像存储最大可用空间: " + mVideoCacheMaxSize);
        }

        mHandler = new VideoHandler();

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        init();
    }

    public VideoConfigParam getVideoConfigParam() {
        return videoConfigParam;
    }

    private void setUpCamera() {
        log.debug("开始初始化camera...");
        shutdownCamera();

        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (Exception e) {
            log.error("获取相机失败",e);
            return;
        }

        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewFormat(PixelFormat.YCbCr_420_SP);
        params.setPreviewSize(854, 480);
        params.setPictureSize(videoConfigParam.getWidth(), videoConfigParam.getHeight());

        mCamera.setParameters(params);
        mCamera.setErrorCallback(new Camera.ErrorCallback() {
            @Override
            public void onError(int error, Camera camera) {
                log.error("相机出错了： " + error);
                releaseAll();
            }
        });
    }

    public void init() {
        mVideoView = LayoutInflater.from(mContext).
                inflate(R.layout.video_recoder, null, false);

        SurfaceView surfaceView = (SurfaceView) mVideoView.findViewById(R.id.surfaceView);

        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(this);

        mLayoutParams = new WindowManager.LayoutParams(1, 1, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        mWindowManager.addView(mVideoView, mLayoutParams);

        setUpCamera();

        mVideoTransfer = new VideoTransfer();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        startRecord();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void updatePreviewSize(int width, int height) {
        mLayoutParams.width = width;
        mLayoutParams.height = height;
        mWindowManager.updateViewLayout(mVideoView, mLayoutParams);
    }

    public View getVideoView() {
        return mVideoView;
    }

    public void startPreview() {
        try {
            mCamera.stopPreview();

        } catch (Exception e) {
            log.error("结束预览出错", e);
        }
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            log.error("开始相机预览错误: " + e.getMessage());
        }
    }

    public void startRecord() {
        if (mRecording) {
            return;
        }

        log.debug("调用startRecord方法，开始启动录像...  sd卡状态：{}", FileUtils.isTFlashCardExists());

        if (FileUtils.isTFlashCardExists()) {
            mRecording = true;

            startMediaRecorder();

            startRecordTimer();
        }
    }

    public void stopRecord() {
        log.debug("停止录像");
        if (!mRecording) {
            return;
        }

        mRecording = false;

        log.debug("调用stopRecord方法，停止录像...");

        stopRecordTimer();

        createVideoFragment();
    }

    public void releaseAll() {
        mRecording = false;

        releaseMediaRecorder();

        shutdownCamera();

        stopRecordTimer();
    }

    public void onTFlashCardInserted() {
        log.debug("TFlashCard插入...");
        //已插入SD卡

        mVideoStoragePath = FileUtils.getVideoStorageDirWhenInsertSdcard();

        log.info("TFlashCard插入... 获取视频存储路径：{}", mVideoStoragePath);
        if (mVideoStoragePath != null) {
            mVideoCacheMaxSize = Float.parseFloat(FileUtils.fileByte2Mb(FileUtils.getTFlashCardSpaceWhenInsertSdcard()));
            startRecord();
        }
    }

    public void onTFlashCardRemoved() {
        log.info("TFlashCard拔出...");

        //没有插入SD卡，不能存储行车录像!

        setUpCamera();

        startPreview();
    }

    private void startMediaRecorder() {
        log.debug("调用startMediaRecorder方法...");
        if (prepareMediaRecorder()) {
            try {
                mMediaRecorder.start();
                EventBus.getDefault().post(new DeviceEvent.Video(DeviceEvent.ON));
                log.debug("开启录像成功...");
                return;
            } catch (Exception e) {
                log.error("录像开启出错: " + e.getMessage());
                EventBus.getDefault().post(new DeviceEvent.Video(DeviceEvent.OFF));
                SystemPropertiesProxy.getInstance().set(mContext, "persist.sys.boot", "reboot");
            }
        } else {
            mRecording = false;

            releaseMediaRecorder();

            mHandler.sendMessageDelayed(mHandler.obtainMessage(VIDEO_START_RETRY),
                    VIDEO_RETRY_INTERVAL);
        }
    }

    private void startRecordTimer() {
        log.debug("开启录像定时器...");
        mTimer = new Timer();
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                log.debug("TimerTask节点，开始录像...");

                createVideoFragment();

                startMediaRecorder();
            }
        };

        mTimer.schedule(mTimerTask, videoConfigParam.getVideo_interval(), videoConfigParam.getVideo_interval());
    }

    private boolean prepareMediaRecorder() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            } catch (Exception e) {
                log.error("重试获取相机失败");
                return false;
            }
        }
        mCamera.unlock();

        mMediaRecorder = new MediaRecorder();

        mMediaRecorder.setOnErrorListener(this);

        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());

        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);

        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        CamcorderProfile profile = CamcorderProfile.get(videoConfigParam.getQuality());
        if (profile.videoBitRate > videoConfigParam.getVideoBitRate()) {
            mMediaRecorder.setVideoEncodingBitRate(videoConfigParam.getVideoBitRate());
        } else {
            mMediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
        }

        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);

        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        mVideoName = mDateFormat.format(new Date()) + ".mp4";

        curVideoName = mVideoStoragePath + File.separator + mVideoName;

        mMediaRecorder.setOutputFile(curVideoName);

        mMediaRecorder.setVideoFrameRate(videoConfigParam.getRate());
        mMediaRecorder.setVideoSize(videoConfigParam.getWidth(), videoConfigParam.getHeight());
        try {
            mMediaRecorder.prepare();
        } catch (Exception e) {
            log.error("准备录像出错: ", e);
            return false;
        }

        return true;
    }

    private void createVideoFragment() {
        log.debug("调用createVideoFragment方法，生成录像片段...");
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
                EventBus.getDefault().post(new DeviceEvent.Video(DeviceEvent.OFF));
            } catch (Exception e) {
                log.error("录像关闭异常: " + e.toString());
            }
        }

        //当前的录像上传
        mVideoTransfer.addVideoFileName(curVideoName);

        releaseMediaRecorder();

        if (mCamera != null) {
            mCamera.lock();
        }

        insertVideo(mVideoName);
    }

    private void shutdownCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            log.debug("释放录像资源...");
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
            EventBus.getDefault().post(new DeviceEvent.Video(DeviceEvent.OFF));
        }
    }

    private void stopRecordTimer() {
        log.debug("停止录像定时器...");
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private void insertVideo(final String videoName) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    log.info("保存视频：{}", videoName);
                    if (!FileUtils.isTFlashCardExists()) {
                        return;
                    }

                    checkTFlashCardSpace();

                    File file = new File(mVideoStoragePath, videoName);
                    String length = FileUtils.fileByte2Kb(file.length());

                    log.debug("insertVideo 视频文件长度：{}M", FileUtils.fileByte2Mb(file.length()));
                    float size = Float.parseFloat(length);
                    //100Kb以下的文件不保存
                    if (file.exists() && size > 250) {
                        insertVideo(file);
                    } else if (file.exists()) {
                        log.error("250Kb以下的文件不保存");
                        boolean success = file.delete();
                        log.error("删除结果:" + success);
                    }
                } catch (Exception e) {
                    log.error("插入数据库出错了...", e);
                }
            }
        }).start();
    }

    private void insertVideo(final File file) throws Exception {
        String length = FileUtils.fileByte2Mb(file.length());

        float size = Float.parseFloat(length);
        log.debug("insertVideo 视频大小: {}M", size);

        while (mVideoCacheMaxSize <= mDbHelper.getTotalSize() + size) {
            log.debug("录像存储空间不够，准备释放空间...");
            if (mDbHelper.isAllVideoLocked()) {
                mHandler.sendMessage(mHandler.obtainMessage(VIDEO_CACHE_FULL));

                file.delete();
                return;
            }

            log.debug("删除时间最久的视频...");
            mDbHelper.deleteOldestVideo();
        }

        log.debug("将视频信息插入数据库...");
        VideoEntity video = new VideoEntity();
        video.setName(file.getName());
        video.setFile(file);
        video.setPath(mVideoStoragePath);
        video.setSize(length);
        mDbHelper.insertVideo(video);
    }

    private void checkTFlashCardSpace() {
        double totalSpace = FileUtils.getTFlashCardSpace();
        double freeSpace = FileUtils.getTFlashCardFreeSpace();
        if (freeSpace < totalSpace * 0.2) {
            log.debug("剩余存储空间小于TFlashCard空间20%，开始清理空间...");
            FileUtils.clearLostDirFolder();
        }
    }

}
