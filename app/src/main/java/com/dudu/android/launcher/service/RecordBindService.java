package com.dudu.android.launcher.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
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
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.bean.VideoEntity;
import com.dudu.android.launcher.db.DbHelper;
import com.dudu.android.launcher.ui.activity.video.VideoListActivity;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.ToastUtils;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class RecordBindService extends Service implements SurfaceHolder.Callback {

    private static final String TAG = "RecordBindService";

    private static final int VIDEO_INTERVAL = 10 * 60 * 1000;

    private WindowManager windowManager;

    private SurfaceView surfaceView;

    private Camera camera = null;

    private MediaRecorder mediaRecorder = null;

    private LayoutParams layoutParams;

    private volatile boolean isRecording = false;

    private SurfaceHolder surfaceHolder;

    private MyBinder binder = new MyBinder();

    private View videoView;

    private Button backButton;

    private ImageButton localVideo;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss", Locale.getDefault());

    private String videoName = "";

    private String videoPath = "";

    private DbHelper dbHelper = null;

    private Handler handler = new Handler();

    private Timer timer;

    private TimerTask timerTask;

    private float mVideoCacheMaxSize;

    private TFlashCardReceiver mTFlashCardReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        videoPath = FileUtils.getVideoStorageDir().getAbsolutePath();

        if (FileUtils.isTFlashCardExists()) {
            mVideoCacheMaxSize = Float.parseFloat(FileUtils.fileByte2Mb(FileUtils.getTFlashCardSpace()));
        } else {
            ToastUtils.showToast(R.string.video_sdcard_removed_alert);
        }

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        dbHelper = DbHelper.getDbHelper(RecordBindService.this);

        videoView = LayoutInflater.from(this).inflate(R.layout.video_main, null, false);
        surfaceView = (SurfaceView) videoView.findViewById(R.id.surfaceView);
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

        layoutParams = new LayoutParams(1, 1, LayoutParams.TYPE_PHONE,
                LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        windowManager.addView(videoView, layoutParams);

        timer = new Timer();

        mTFlashCardReceiver = new TFlashCardReceiver();

        registerTFlashCardReceiver();
    }

    private void registerTFlashCardReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addDataScheme("file");
        registerReceiver(mTFlashCardReceiver, intentFilter);
    }

    public void updatePreviewSize(int width, int height) {
        layoutParams.width = width;
        layoutParams.height = height;
        windowManager.updateViewLayout(videoView, layoutParams);
    }

    public void startRecord() {
        if (isRecording) {
            return;
        }

        new MediaPrepareTask().execute();
    }

    public void stopRecord() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
            } catch (Exception e) {
                LogUtils.e(TAG, e.toString());
            }
        }

        isRecording = false;

        releaseMediaRecorder();

        if (camera != null) {
            camera.lock();
        }

        releaseCamera();

        insertVideo();
    }

    public void startRecordTimer() {
        timer = new Timer();

        timerTask = new TimerTask() {

            @Override
            public void run() {
                stopRecord();
                startRecord();
            }
        };

        timer.schedule(timerTask, VIDEO_INTERVAL, VIDEO_INTERVAL);
    }

    public void stopRecordTimer() {
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

        stopRecordTimer();

        releaseMediaRecorder();

        releaseCamera();

        windowManager.removeView(videoView);

        unregisterReceiver(mTFlashCardReceiver);
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

    private boolean prepareMediaRecorder() {
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        Camera.Parameters p = camera.getParameters();
        p.setPreviewFormat(PixelFormat.YCbCr_420_SP);
        p.setPictureSize(1280, 720);
        camera.setParameters(p);

//        try {
//            camera.setPreviewDisplay(surfaceHolder);
//            camera.startPreview();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setOnErrorListener(new OnErrorListener() {

            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                try {
                    if (mr != null)
                        mr.reset();
                } catch (Exception e) {
                    LogUtils.e(TAG, "stopRecord: " + e.getMessage());
                }
            }
        });

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

        videoName = dateFormat.format(new Date()) + ".mp4";

        if (FileUtils.isTFlashCardExists()) {
            mediaRecorder.setOutputFile(videoPath + File.separator + videoName);
        } else {
            mediaRecorder.setOutputFile(videoPath + File.separator + "temp.mp4");
        }

        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            LogUtils.e(TAG, e.getMessage());
            return false;
        }

        return true;
    }

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
                LogUtils.e(TAG, e.getMessage());
            }
        }
    }

    private void insertVideo() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                if (!FileUtils.isTFlashCardExists()) {
                    return;
                }

                File file = new File(videoPath, videoName);
                if (file.exists() && file.length() > 0) {
                    String length = FileUtils.fileByte2Mb(file.length());
                    float totalSize = dbHelper.getTotalSize() + Float.parseFloat(length);
                    if (totalSize >= mVideoCacheMaxSize) {
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

                        do {
                            dbHelper.deleteOldestVideo();
                            totalSize = dbHelper.getTotalSize() + Float.parseFloat(length);
                        } while (totalSize >= mVideoCacheMaxSize);
                    }

                    VideoEntity video = new VideoEntity();
                    video.setName(file.getName());
                    video.setFile(file);
                    video.setPath(videoPath);
                    video.setSize(length);
                    dbHelper.insertVideo(video);
                }
            }
        }).start();
    }

    class MediaPrepareTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (prepareMediaRecorder()) {
                mediaRecorder.start();
                isRecording = true;
            } else {
                releaseMediaRecorder();
            }

            return null;
        }

    }

    private class TFlashCardReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.v(TAG, "SD卡插拔广播" + action);
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {

                ToastUtils.showToast(R.string.video_sdcard_inserted_alert);

                videoPath = FileUtils.getVideoStorageDir().getAbsolutePath();

                mVideoCacheMaxSize = Float.parseFloat(FileUtils.fileByte2Mb(FileUtils.getTFlashCardSpace()));
            } else if (action.equals(Intent.ACTION_MEDIA_REMOVED)) {
                videoPath = FileUtils.getVideoStorageDir().getAbsolutePath();
                ToastUtils.showToast(R.string.video_sdcard_removed_alert);
            }
        }
    }

}
