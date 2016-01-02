package com.dudu.android.ext.media.domain.services;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class VideoHelper {

    /* 录像视频参数*/
    private VideoConfigParam videoConfigParam;

    private static final int VIDEO_INTERVAL = 10 * 60 * 1000;

    private static VideoHelper mInstance;

    private Camera mCamera;

    private MediaRecorder mMediaRecorder;

    private Timer mTimer;

    private TimerTask mTimerTask;

    private boolean mRecording = false;

    private String mVideoStoragePath;

    private String mVideoName;

    private boolean mRecordingAudio = true;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss",
            Locale.getDefault());

    private Logger logger;

    private boolean isPreviewing;
    private String curVideoName;

    private VideoHelper() {
        logger = LoggerFactory.getLogger("video.VideoHelper");
        mVideoStoragePath = VideoUtils.getVideoStorageDir().getAbsolutePath();
    }

    public static VideoHelper getInstance() {
        if (mInstance == null) {
            mInstance = new VideoHelper();
        }

        return mInstance;
    }


    public boolean getRecordingAudio() {
        return mRecordingAudio;
    }

    public void setRecordingAudio(boolean recordingAudio) {
        mRecordingAudio = recordingAudio;
    }

    public void startRecord(SurfaceHolder holder) {
        if (mRecording) {
            return;
        }

        logger.debug("调用startRecord方法，开始启动录像...");

        prepareCamera(holder);

        if (VideoUtils.isTFlashCardExists()) {
            mRecording = true;
            startMediaRecorder(holder);
            startRecordTimer(holder);
        } else {
            doStartPreview(holder);
        }
    }

    /**
     * 开启预览
     */
    public void doStartPreview(SurfaceHolder holder) {
        if (mCamera != null) {
            if (isPreviewing) {
                mCamera.stopPreview();
            }

            try {
                logger.debug("开启预览...");
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (Exception e) {
                logger.error("预览出错：" + e.getMessage());
            }

            isPreviewing = true;
        }
    }

    private void prepareCamera(final SurfaceHolder holder) {
        logger.debug("开始初始化camera: prepareCamera");
        if (mCamera == null) {
            try {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            } catch (Exception e) {
                logger.error("camera.open出错", e);
                if (mCamera == null) {
                    return;
                }
            }
        }

        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewFormat(PixelFormat.YCbCr_420_SP);
        params.setPreviewSize(854, 480);
        params.setPictureSize(videoConfigParam.getWidth(), videoConfigParam.getHeight());

        mCamera.setParameters(params);
        mCamera.setErrorCallback(null);
        mCamera.setErrorCallback(new Camera.ErrorCallback() {
            @Override
            public void onError(int error, Camera camera) {
                logger.error("相机出错了： " + error);
                mRecording = false;
                releaseMediaRecorder();
                releaseCamera();
                startRecord(holder);
            }
        });
    }

    private void startMediaRecorder(SurfaceHolder holder) {
        logger.debug("调用startMediaRecorder方法...");
        if (prepareMediaRecorder(holder)) {
            try {
                mMediaRecorder.start();
                EventBus.getDefault().post(new VideoStatus(VideoStatus.ON));
                logger.debug("开启录像成功...");
            } catch (Exception e) {
                logger.debug("开启录像失败...");
                //准备录像失败，是否重启
            }

        } else {
            //准备录像失败，是否重启录像
        }
    }

    private boolean prepareMediaRecorder(SurfaceHolder holder) {
        if (mCamera == null) {
            logger.debug("准备相机: prepareCamera is called!");
            prepareCamera(holder);
        }
        if (mCamera == null) {
            return false;
        }
        mCamera.unlock();

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setOnErrorListener(null);
        mMediaRecorder.setPreviewDisplay(holder.getSurface());
        mMediaRecorder.setCamera(mCamera);
        if (mRecordingAudio) {
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

        if (mRecordingAudio) {
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }

        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mVideoName = mDateFormat.format(new Date()) + ".mp4";
        curVideoName = mVideoStoragePath + File.separator + mVideoName;
        mMediaRecorder.setOutputFile(mVideoStoragePath + File.separator + mVideoName);
        mMediaRecorder.setVideoFrameRate(videoConfigParam.getRate());
        mMediaRecorder.setVideoSize(videoConfigParam.getWidth(), videoConfigParam.getHeight());
        try {
            mMediaRecorder.prepare();
        } catch (Exception e) {
            logger.error("准备录像出错: " + e.toString());
            handlePrepareException(e);
            return false;
        }

        return true;
    }

    private void handlePrepareException(Exception e) {
        String message = e.getMessage();
        if (!TextUtils.isEmpty(message)) {
            if (message.contains("EROFS")) {
                logger.error("存储空间已满, 准备录像出错");
            }
        }
        logger.error("录像出错了： " + e.getMessage());
    }

    private void createVideoFragment() {
        logger.debug("调用createVideoFragment方法，生成录像片段...");
        EventBus.getDefault().post(new VideoStatus(VideoStatus.OFF));
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
            } catch (Exception e) {
                logger.error("录像关闭异常: " + e.toString());
            }
        }

        releaseMediaRecorder();

        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            try {
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                logger.error("释放相机资源出错：" + e.getMessage());
            }
        }
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            logger.debug("释放录像资源...");
            try {
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;

                if (mCamera != null) {
                    mCamera.lock();
                }
            } catch (Exception e) {
                logger.error("释放录像资源出错: " + e.getMessage());
            }
        }
    }

    private void startRecordTimer(final SurfaceHolder holder) {
        logger.debug("开始录像定时器...");
        mTimer = new Timer();
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                logger.debug("TimerTask节点，开始录像...");
                createVideoFragment();

                startMediaRecorder(holder);
            }
        };

        mTimer.schedule(mTimerTask, VIDEO_INTERVAL, VIDEO_INTERVAL);
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

}
