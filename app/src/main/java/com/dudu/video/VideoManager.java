package com.dudu.video;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.dudu.android.hideapi.SystemPropertiesProxy;
import com.dudu.android.launcher.LauncherApplication;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.bean.VideoEntity;
import com.dudu.android.launcher.db.DbHelper;
import com.dudu.android.launcher.service.video.VideoConfigParam;
import com.dudu.android.launcher.service.video.VideoTransfer;
import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.voice.semantic.VoiceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by 赵圣琪 on 2015/12/9.
 */
public class VideoManager implements SurfaceHolder.Callback {

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

    private Logger logger;

    /* 当前录制视频的文件名*/
    private String curVideoName;

    private class VideoHandler extends Handler {
        public VideoHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VIDEO_CACHE_FULL:
                    ToastUtils.showToast(R.string.video_cache_space_full_alert);
                    break;
                case VIDEO_START_RETRY:
                    setUpCamera();

                    startRecord();
                    break;
            }
        }
    }

    public static VideoManager getInstance() {
        if (mInstance == null) {
            mInstance = new VideoManager();
        }

        return mInstance;
    }

    private VideoManager() {
        logger = LoggerFactory.getLogger("video.VideoManager");

        videoConfigParam = new VideoConfigParam();

        mContext = LauncherApplication.getContext();

        mDbHelper = DbHelper.getDbHelper();

        mVideoStoragePath = FileUtils.getVideoStorageDir().getAbsolutePath();
        logger.debug("录像存储的路径: " + mVideoStoragePath);

        if (FileUtils.isTFlashCardExists()) {
            mVideoCacheMaxSize = Float.parseFloat(FileUtils.fileByte2Mb(
                    FileUtils.getTFlashCardSpace()));
            logger.debug("录像存储最大可用空间: " + mVideoCacheMaxSize);
        }

        mHandler = new VideoHandler();

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

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

    public VideoConfigParam getVideoConfigParam() {
        return videoConfigParam;
    }

    private void setUpCamera() {
        logger.debug("开始初始化camera...");
        shutdownCamera();

        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (Exception e) {
            logger.error("获取相机失败");
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
                logger.error("相机出错了： " + error);
                releaseAll();
            }
        });
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
        mCamera.stopPreview();

        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            logger.error("开始相机预览错误: " + e.getMessage());
        }
    }

    public void startRecord() {
        if (mRecording) {
            return;
        }

        logger.debug("调用startRecord方法，开始启动录像...");

        if (FileUtils.isTFlashCardExists()) {
            mRecording = true;

            startMediaRecorder();

            startRecordTimer();
        }
    }

    public void stopRecord() {
        if (!mRecording) {
            return;
        }

        mRecording = false;

        logger.debug("调用stopRecord方法，停止录像...");

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
        logger.debug("TFlashCard插入...");

        ToastUtils.showToast(R.string.video_sdcard_inserted_alert);

        mVideoStoragePath = FileUtils.getVideoStorageDir().getAbsolutePath();

        mVideoCacheMaxSize = Float.parseFloat(FileUtils.fileByte2Mb(FileUtils.getTFlashCardSpace()));

        startRecord();
    }

    public void onTFlashCardRemoved() {
        logger.debug("TFlashCard拔出...");

        ToastUtils.showToast(R.string.video_sdcard_removed_alert);

        setUpCamera();

        startPreview();
    }

    private void startMediaRecorder() {
        logger.debug("调用startMediaRecorder方法...");
        if (prepareMediaRecorder()) {
            try {
                mMediaRecorder.start();
                logger.debug("开启录像成功...");
                return;
            } catch (Exception e) {
                logger.error("录像开启出错: " + e.getMessage());
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
        logger.debug("开启录像定时器...");
        mTimer = new Timer();
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                logger.debug("TimerTask节点，开始录像...");

                createVideoFragment();

                startMediaRecorder();
            }
        };

        mTimer.schedule(mTimerTask, videoConfigParam.getVideo_interval(), videoConfigParam.getVideo_interval());
    }

    private boolean prepareMediaRecorder() {
        mCamera.unlock();

        mMediaRecorder = new MediaRecorder();

        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());

        mMediaRecorder.setCamera(mCamera);

        if (!VoiceManager.isUnderstandingOrSpeaking()) {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        }

        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        CamcorderProfile profile = CamcorderProfile.get(videoConfigParam.getQuality());
        if (profile.videoBitRate > videoConfigParam.getVideoBitRate()) {
            mMediaRecorder.setVideoEncodingBitRate(videoConfigParam.getVideoBitRate());
        } else {
            mMediaRecorder.setVideoEncodingBitRate(profile.videoBitRate);
        }

        if (!VoiceManager.isUnderstandingOrSpeaking()) {
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }

        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        mVideoName = mDateFormat.format(new Date()) + ".mp4";

        curVideoName = mVideoStoragePath + File.separator + mVideoName;

        mMediaRecorder.setOutputFile(curVideoName);

        mMediaRecorder.setVideoFrameRate(videoConfigParam.getRate());
        mMediaRecorder.setVideoSize(videoConfigParam.getWidth(), videoConfigParam.getHeight());
        try {
            mMediaRecorder.prepare();
        } catch (Exception e) {
            logger.error("准备录像出错: " + e.toString());
            return false;
        }

        return true;
    }

    private void createVideoFragment() {
        logger.debug("调用createVideoFragment方法，生成录像片段...");
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
            } catch (Exception e) {
                logger.error("录像关闭异常: " + e.toString());
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
            logger.debug("释放录像资源...");
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }

    private void stopRecordTimer() {
        logger.debug("停止录像定时器...");
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
                if (!FileUtils.isTFlashCardExists()) {
                    return;
                }

                checkTFlashCardSpace();

                try {
                    File file = new File(mVideoStoragePath, videoName);
                    if (file.exists() && file.length() > 0) {
                        insertVideo(file);
                    }
                } catch (Exception e) {
                    logger.error("插入数据库出错了...");
                }
            }
        }).start();
    }

    private void checkTFlashCardSpace() {
        double totalSpace = FileUtils.getTFlashCardSpace();
        double freeSpace = FileUtils.getTFlashCardFreeSpace();
        if (freeSpace < totalSpace * 0.2) {
            logger.debug("剩余存储空间小于TFlashCard空间20%，开始清理空间...");
            FileUtils.clearVideoFolder();
        }
    }

    private void insertVideo(final File file) throws Exception {
        String length = FileUtils.fileByte2Mb(file.length());

        float size = Float.parseFloat(length);
        logger.debug("视频大小: {}M", size);

        while (mVideoCacheMaxSize <= mDbHelper.getTotalSize() + size) {
            logger.debug("录像存储空间不够，准备释放空间...");
            if (mDbHelper.isAllVideoLocked()) {
                mHandler.sendMessage(mHandler.obtainMessage(VIDEO_CACHE_FULL));

                file.delete();
                return;
            }

            logger.debug("删除时间最久的视频...");
            mDbHelper.deleteOldestVideo();
        }

        logger.debug("将视频信息插入数据库...");
        VideoEntity video = new VideoEntity();
        video.setName(file.getName());
        video.setFile(file);
        video.setPath(mVideoStoragePath);
        video.setSize(length);
        mDbHelper.insertVideo(video);
    }

}
