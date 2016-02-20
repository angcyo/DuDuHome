package com.dudu.aios.ui.activity;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dudu.aios.ui.base.BaseActivity;
import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.view.VideoView;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.drivevideo.model.VideoEntity;
import com.dudu.drivevideo.storage.VideoFileManage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

public class VideoPlayActivity extends BaseActivity  implements View.OnClickListener  {
    private final static int MESSAGE_PROGRESS_CHANGED = 0;
    private static final long DISAPPEAR_INTERVAL = 3000;

    private Logger log;

    private ImageButton btnBack, btnLast, btnPlay, btnNext, btnUpload, btnCancelUpload;
    private TextView tvDuration, tvNowDuration;
    private SeekBar seekBar;
    private LinearLayout uploadingContainer, uploadSuccessContainer;
    private ImageView uploadingIcon;

    private AnimationDrawable animationDrawable;

    private VideoView videoView;
    private boolean mPaused = false;
    private int position;

    private LinkedList<VideoEntity> mPlayList = new LinkedList<>();

    public VideoPlayActivity() {
        log = LoggerFactory.getLogger("video.videoui");
    }

    @Override
    protected View getChildView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_video_play,null);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_PROGRESS_CHANGED) {
                int position = videoView.getCurrentPosition();

                seekBar.setProgress(position);

                position /= 1000;
                int minutes = position / 60;
                int hours = minutes / 60;
                int seconds = position % 60;
                minutes %= 60;
                tvNowDuration.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

                sendEmptyMessageDelayed(MESSAGE_PROGRESS_CHANGED, 100);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initClickListener();
        initData();
    }



    private void initClickListener() {
        btnBack.setOnClickListener(this);
        btnLast.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        btnCancelUpload.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
//                showController();

                int duration = videoView.getDuration();

                seekBar.setMax(duration);

                duration /= 1000;

                int minutes = duration / 60;
                int hours = minutes / 60;
                int seconds = duration % 60;

                minutes %= 60;
                tvDuration.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

                videoView.start();

                mHandler.sendEmptyMessage(MESSAGE_PROGRESS_CHANGED);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.seekTo(0);
                videoView.pause();
//                mPauseButton.setVisibility(View.VISIBLE);
                mPaused = true;
            }
        });
    }


    private void initView() {
        btnBack = (ImageButton) findViewById(R.id.button_back);
        btnLast = (ImageButton) findViewById(R.id.button_last);
        btnPlay = (ImageButton) findViewById(R.id.button_play);
        btnNext = (ImageButton) findViewById(R.id.button_next);
        btnUpload = (ImageButton) findViewById(R.id.button_upload);
        btnCancelUpload = (ImageButton) findViewById(R.id.button_cancel_upload);
        tvDuration = (TextView) findViewById(R.id.tv_video_duration);
        tvNowDuration = (TextView) findViewById(R.id.tv_now_duration);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        uploadingContainer = (LinearLayout) findViewById(R.id.uploading_container);
        uploadSuccessContainer = (LinearLayout) findViewById(R.id.upload_successful_container);
        uploadingIcon = (ImageView) findViewById(R.id.image_uploading);

        videoView = (VideoView) findViewById(R.id.video_view);
        videoView.setVisibility(View.VISIBLE);
    }

    private void initData(){
        Uri uri = getIntent().getData();
        log.debug("文件地址：{}", uri.toString());
        if (uri != null){
            videoView.stopPlayback();
            videoView.setVideoURI(uri);
        }
        position = getIntent().getIntExtra(Constants.EXTRA_VIDEO_POSITION, 0);
        mPlayList.addAll(VideoFileManage.getInstance().getVideoList());
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                onButtonBack();
                break;
            case R.id.button_last:
                actionLast();
                break;
            case R.id.button_play:
                actionPlay();
                break;
            case R.id.button_next:
                actionNext();
                break;
            case R.id.button_upload:
                actionUpload();
                break;
            case R.id.button_cancel_upload:
                actionCancel();
                break;
        }
    }

    private void onButtonBack(){
        finish();
    }

    private void actionCancel() {
        uploadingContainer.setVisibility(View.GONE);
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
    }

    private void actionUpload() {
        uploadingContainer.setVisibility(View.VISIBLE);
        uploadingIcon.setImageResource(R.drawable.uplaod_video_arrows);
        animationDrawable = (AnimationDrawable) uploadingIcon.getDrawable();
        animationDrawable.start();
    }

    private void actionNext() {
        if (position < mPlayList.size() - 1) {
            position++;
//            mPauseButton.setVisibility(View.GONE);
            mPaused = false;
            videoView.setVideoURI(Uri.fromFile(mPlayList.get(position).getFile()));
        } else {
//            ToastUtils.showToast(R.string.video_end_alert);
        }
    }

    private void actionPlay() {
        if (mPaused) {
            videoView.start();
            mPaused = false;
//            btnPlay.setVisibility(View.GONE);
        }else {
            videoView.pause();
            mPaused = true;
        }
    }

    private void actionLast() {
        if (position > 0) {
//            btnPlay.setVisibility(View.GONE);
            mPaused = false;
            position--;
            videoView.setVideoURI(Uri.fromFile(mPlayList.get(position).getFile()));
        } else {
//            ToastUtils.showToast(R.string.video_start_alert);
        }
    }
}
