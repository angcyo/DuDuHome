package com.dudu.agedmodel;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dudu.utils.Contacts;
import com.dudu.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class AgedVideoPlayActivity extends NoTitleActivity implements SurfaceHolder.Callback {
    private Handler handler;

    private AgedVideoPlayActivity mActivity;

    private SurfaceView surfaceView;

    private SurfaceHolder holder;

    private MediaPlayer mMediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_aged_video_play);
        initView();
        initData();
    }

    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.videoPlay_surface);
    }

    private void initData() {
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(mActivity);
        handler = new MyHandler();
    }

    private void openVideo() {
        try {
            mMediaPlayer = new MediaPlayer();

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //设置播放的界面
            mMediaPlayer.setDisplay(holder);
            //设置播放文件的路径
            File file = new File(FileUtils.getExternalStorageDirectory() + "/agedVideoFolder", AgedCameraActivity.videoName);
            mMediaPlayer.setDataSource(file.getAbsolutePath());
            //准备播放
            mMediaPlayer.prepareAsync();
            //设置准备播发的监听事件，只有播发器准备完毕后，才开始播放
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //开始播放
                    mp.start();
                    handler.sendEmptyMessageDelayed(0, 10000);
                }
            });
        } catch (IOException ex) {

            return;
        } catch (IllegalArgumentException ex) {

            return;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
        openVideo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.holder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent(mActivity, AgedModelMainActivity.class);
            intent.putExtra(Contacts.CLASS_TYPE, Contacts.VIDEO_PLAY_TYPE);
            startActivity(intent);
            finish();
        }
    }

}
