package com.dudu.android.launcher.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.bean.VideoEntity;
import com.dudu.android.launcher.db.DbHelper;
import com.dudu.android.launcher.ui.activity.video.VideoListActivity;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.DeviceIDUtil;
import com.dudu.android.launcher.utils.FileNameUtil;
import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.android.launcher.utils.Utils;
import com.dudu.android.launcher.utils.ViewAnimation;
import com.dudu.conn.ConnectionEvent;
import com.dudu.event.DeviceEvent;
import com.dudu.http.MultipartRequest;
import com.dudu.http.MultipartRequestParams;
import com.dudu.monitor.event.CarStatus;
import com.dudu.obd.BleOBD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class RecordBindService extends Service implements SurfaceHolder.Callback {

    private static final String TAG = "RecordBindService";

    private static final int VIDEO_INTERVAL = 10 * 60 * 1000;

    private static final int DISAPPEAR_INTERVAL = 3000;

    private WindowManager windowManager;

    private SurfaceView surfaceView;

    private Camera camera = null;

    private MediaRecorder mediaRecorder = null;

    private LayoutParams layoutParams;

    private SurfaceHolder surfaceHolder;

    private MyBinder binder = new MyBinder();

    private View videoView;

    private Button backButton;

    private ImageButton localVideo;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss",
            Locale.getDefault());

    private String videoName = "";

    private String videoPath = "";

    private DbHelper dbHelper = null;

    private Handler handler = new Handler();

    private Timer timer;

    private TimerTask timerTask;

    private float mVideoCacheMaxSize;

    private TFlashCardReceiver mTFlashCardReceiver;

    private static String HTTP_URL_IMG = "http://192.168.124.177:8081/weixin/picUpload";

    private RequestQueue queue;

    private boolean isDrivingCar = false;

    private Logger logger;

    private volatile boolean isPreviewingOrRecording = false;

    private volatile boolean isRecording = false;

    private boolean isPreviewing = false;

    private Camera.Parameters mCameraParams;

    //Back键定时消失的handler
    private Handler mBackDisappearHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            toggleAnimation();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        logger = LoggerFactory.getLogger("video.service");

        EventBus.getDefault().register(this);

        videoPath = FileUtils.getVideoStorageDir().getAbsolutePath();
        logger.debug("录像存储的路径: " + videoPath);

        if (FileUtils.isTFlashCardExists()) {
            mVideoCacheMaxSize = Float.parseFloat(FileUtils.fileByte2Mb(FileUtils.getTFlashCardSpace()));
            logger.debug("录像存储最大可用空间: " + mVideoCacheMaxSize);
        }

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        dbHelper = DbHelper.getDbHelper(RecordBindService.this);

        videoView = LayoutInflater.from(this).inflate(R.layout.video_main, null, false);
        surfaceView = (SurfaceView) videoView.findViewById(R.id.surfaceView);
        surfaceView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (backButton.getVisibility() == View.VISIBLE && localVideo.getVisibility() == View.VISIBLE) {
                    return;
                }
                toggleAnimation();
                stopBackDisappearHandler();
                startBackDisappearHandler();
            }
        });

        backButton = (Button) videoView.findViewById(R.id.back_button);
        backButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent(Constants.VIDEO_PREVIEW_BROADCAST));
            }
        });

        localVideo = (ImageButton) videoView.findViewById(R.id.local_video);
        localVideo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordBindService.this, VideoListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        SurfaceHolder holder = surfaceView.getHolder();

        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        layoutParams = new LayoutParams(1, 1, LayoutParams.TYPE_PHONE,
                LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        windowManager.addView(videoView, layoutParams);

        timer = new Timer();

        mTFlashCardReceiver = new TFlashCardReceiver();

        registerTFlashCardReceiver();

        queue = Volley.newRequestQueue(this);

        if (Utils.isDemoVersion(this)) {
            isDrivingCar = true;
        }
    }

    private void registerTFlashCardReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addDataScheme("file");
        registerReceiver(mTFlashCardReceiver, intentFilter);
    }

    private void startBackDisappearHandler() {
        mBackDisappearHandler.sendEmptyMessageDelayed(0, DISAPPEAR_INTERVAL);
    }

    private void stopBackDisappearHandler() {
        mBackDisappearHandler.removeMessages(0);
    }

    public void updatePreviewSize(int width, int height) {
        if (width == 854 && height == 480) {
            startBackDisappearHandler();
        } else if (width == 1 && height == 1) {
            stopBackDisappearHandler();
        }

        layoutParams.width = width;
        layoutParams.height = height;
        windowManager.updateViewLayout(videoView, layoutParams);
    }

    /**
     * 开始预览或者录像
     */
    public void startRecord() {
        if (isPreviewingOrRecording) {
            return;
        }

        logger.debug("调用startRecord方法，开始启动录像...");

        prepareCamera();

        if (Utils.isDemoVersion(this)) {
            isDrivingCar = true;
        }

        if (FileUtils.isTFlashCardExists() && isDrivingCar) {
            isPreviewingOrRecording = true;
            startMediaRecorder();

            startRecordTimer();
        } else {
            doStartPreview();
        }
    }

    private void startMediaRecorder() {
        if (isRecording) {
            return;
        }

        isRecording = true;

        logger.debug("调用startMediaRecorder方法...");

        new MediaPrepareTask().execute();
    }

    public void stopRecord() {
        if (!isPreviewingOrRecording) {
            return;
        }

        isPreviewingOrRecording = false;
        logger.debug("调用stopRecord方法，停止录像...");

        stopRecordTimer();

        createVideoFragment();
    }

    /**
     * 生成10分钟的视频片段
     */
    private void createVideoFragment() {
        isRecording = false;
        logger.debug("调用createVideoFragment方法，生成录像片段...");

        EventBus.getDefault().post(new DeviceEvent.Video(DeviceEvent.OFF));

        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
            } catch (Exception e) {
                logger.error("录像关闭出错了: " + e.getMessage());
            }
        }

        releaseMediaRecorder();

        if (camera != null) {
            camera.lock();
        }

        releaseCamera();

        insertVideo(videoName);
    }

    public void startRecordTimer() {
        timer = new Timer();

        timerTask = new TimerTask() {

            @Override
            public void run() {
                createVideoFragment();

                startMediaRecorder();
            }
        };

        timer.schedule(timerTask, VIDEO_INTERVAL, VIDEO_INTERVAL);
    }

    /**
     * 停止录像的计时器
     */
    private void stopRecordTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopRecord();

        windowManager.removeView(videoView);

        unregisterReceiver(mTFlashCardReceiver);

        EventBus.getDefault().unregister(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MyBinder extends Binder {
        public RecordBindService getService() {
            return RecordBindService.this;
        }
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

    public void prepareCamera() {
        logger.debug("开始初始化camera: prepareCamera");
        if (camera == null) {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }

        mCameraParams = camera.getParameters();
        mCameraParams.setPreviewFormat(PixelFormat.YCbCr_420_SP);
        mCameraParams.setPreviewSize(854, 480);
        mCameraParams.setPictureSize(1280, 720);
        camera.setParameters(mCameraParams);
        camera.setErrorCallback(new Camera.ErrorCallback() {
            @Override
            public void onError(int error, Camera camera) {
                logger.error("相机出错了： " + error);
                isRecording = false;

                isPreviewingOrRecording = false;

                releaseCamera();

                releaseMediaRecorder();
            }
        });
    }

    private boolean prepareMediaRecorder() {
        if (camera == null) {
            prepareCamera();
        }

        camera.unlock();

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                try {
                    if (mr != null)
                        mr.reset();
                } catch (Exception e) {
                    logger.error("mediaRecorder 出错了：" + e.getMessage());
                }
            }
        });

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

        videoName = dateFormat.format(new Date()) + ".mp4";

        logger.debug("用户正在开车并且TFlashCard存在，录像正常...");
        mediaRecorder.setOutputFile(videoPath + File.separator + videoName);

        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            logger.error("准备录像出错: " + e.getMessage());
            handlePrepareException(e);
            return false;
        }

        return true;
    }

    private void handlePrepareException(Exception e) {
        logger.error("录像出错了： " + e.getMessage());
        String message = e.getMessage();
        if (!TextUtils.isEmpty(message)) {
            if (message.contains("EROFS")) {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        ToastUtils.showToast(R.string.video_sdcard_broken);
                    }
                });
            }
        }
    }


    /**
     * 开启预览
     */
    public void doStartPreview() {
        if (camera != null) {
            if (isPreviewing) {
                camera.stopPreview();
            }

            try {
                logger.debug("开启预览...");
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            } catch (Exception e) {
                logger.error("预览出错：" + e.getMessage());
            }

            isPreviewing = true;
        }
    }

    /**
     * 释放录像资源
     */
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

    private void releaseCamera() {
        if (camera != null) {
            try {
                camera.release();
                camera = null;
            } catch (Exception e) {
                logger.error("相机释放出错了：" + e.getMessage());
            }
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
                    File file = new File(videoPath, videoName);
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

        while (mVideoCacheMaxSize <= dbHelper.getTotalSize() + size) {
            logger.debug("录像存储空间不够，准备释放空间...");
            if (dbHelper.isAllVideoLocked()) {
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        ToastUtils.showToast(R.string.video_cache_space_full_alert);
                    }
                });

                file.delete();
                return;
            }

            logger.debug("删除时间最久的视频...");
            dbHelper.deleteOldestVideo();
        }

        logger.debug("将视频信息插入数据库...");
        VideoEntity video = new VideoEntity();
        video.setName(file.getName());
        video.setFile(file);
        video.setPath(videoPath);
        video.setSize(length);
        dbHelper.insertVideo(video);
    }

    class MediaPrepareTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            logger.debug("开启录像初始化线程: MediaPrepareTask");
            if (prepareMediaRecorder()) {
                mediaRecorder.start();

                EventBus.getDefault().post(new DeviceEvent.Video(DeviceEvent.ON));

                isRecording = true;
            } else {
                stopRecord();

                prepareCamera();

                doStartPreview();
            }

            return null;
        }

    }

    private class TFlashCardReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                logger.debug("TFlashCard插入...");

                ToastUtils.showToast(R.string.video_sdcard_inserted_alert);

                videoPath = FileUtils.getVideoStorageDir().getAbsolutePath();

                mVideoCacheMaxSize = Float.parseFloat(FileUtils.fileByte2Mb(FileUtils.getTFlashCardSpace()));

                startRecord();
            } else if (action.equals(Intent.ACTION_MEDIA_REMOVED)) {
                logger.debug("TFlashCard拔出...");

                ToastUtils.showToast(R.string.video_sdcard_removed_alert);

                stopRecord();

                prepareCamera();

                doStartPreview();
            }
        }
    }

    public void onEventBackgroundThread(final ConnectionEvent.TakePhoto takePhoto) {
        if (camera != null) {
            camera.autoFocus(null);
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    if (data != null) {
                        InputStream inputStream = new ByteArrayInputStream(data);
                        MultipartRequestParams multiPartParams = new MultipartRequestParams();
                        multiPartParams.put("upload_img", inputStream, FileNameUtil.randomString(7) + ".jpg");
                        multiPartParams.put("obeId", DeviceIDUtil.getIMEI(getApplicationContext()));
                        multiPartParams.put("openid", takePhoto.getOpenId());
                        MultipartRequest multipartRequest = new MultipartRequest
                                (Request.Method.POST, multiPartParams, HTTP_URL_IMG, new Response.Listener<String>() {

                                    @Override
                                    public void onResponse(String response) {

                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                });
                        queue.add(multipartRequest);
                    }
                }

            });
        }
    }

    public void onEventBackgroundThread(CarStatus event) {
        if (event.getCarStatus() == CarStatus.CAR_OFFLINE) {
            logger.debug("接收到熄火的消息...");

            isDrivingCar = false;

            stopRecord();

            prepareCamera();

            doStartPreview();
        } else if (event.getCarStatus() == CarStatus.CAR_ONLINE) {
            logger.debug("接收到点火的消息...");

            isDrivingCar = true;

            startRecord();
        }
    }

    private void toggleAnimation() {
        ViewAnimation.startAnimation(backButton, backButton.getVisibility() == View.VISIBLE ?
                R.anim.back_key_disappear : R.anim.back_key_appear, this);
        ViewAnimation.startAnimation(localVideo, localVideo.getVisibility() == View.VISIBLE ?
                R.anim.camera_image_disappear : R.anim.camera_image_apear, this);
    }

}
