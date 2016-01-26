package com.dudu.android.launcher.ui.activity.video;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue.IdleHandler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.bean.VideoEntity;
import com.dudu.android.launcher.db.DbHelper;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.android.launcher.ui.view.VideoView;
import com.dudu.android.launcher.utils.Constants;
import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.android.launcher.utils.ViewAnimation;

import java.io.File;
import java.util.LinkedList;

public class VideoPlayActivity extends BaseNoTitlebarAcitivity implements OnClickListener {

    private final static int MESSAGE_PROGRESS_CHANGED = 0;

    private static final long DISAPPEAR_INTERVAL = 3000;

    private LinkedList<VideoEntity> mPlayList = new LinkedList<>();

    private VideoView mVideoView;

    private PopupWindow mControlWindow;

    private SeekBar mSeekBar;

    private TextView mTotalDuration;

    private TextView mNowDuration;

    private Button mPreButton;

    private Button mNextButton;

    private GestureDetector mGestureDetector;

    private View mControlView;

    private int mScreenWidth = 0, mScreenHeight = 0;

    private int mControlHeight = 0;

    private boolean mPaused = false;

    private Button mPauseButton;

    private int position;

    private Button mBackButton;
    //Back键定时消失的handler
    private Handler mAnimationHandler = new Handler();

    private Runnable hide_button_runnable = new Runnable() {
        @Override
        public void run() {
            toggleAnimation();
        }
    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_PROGRESS_CHANGED) {
                int position = mVideoView.getCurrentPosition();

                mSeekBar.setProgress(position);

                position /= 1000;
                int minutes = position / 60;
                int hours = minutes / 60;
                int seconds = position % 60;
                minutes %= 60;
                mNowDuration.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

                sendEmptyMessageDelayed(MESSAGE_PROGRESS_CHANGED, 100);
            }
        }
    };

    @Override
    public int initContentView() {
        return R.layout.video_play_layout;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

        Looper.myQueue().addIdleHandler(new IdleHandler() {

            @Override
            public boolean queueIdle() {
                if (mControlWindow != null && mVideoView.isShown()) {
                    mControlWindow.showAtLocation(mVideoView, Gravity.BOTTOM, 0, 0);
                    mControlWindow.update(0, 0, mScreenWidth, mControlHeight);
                }

                return false;
            }
        });

        mBackButton = (Button) findViewById(R.id.back_button);

        mVideoView = (VideoView) findViewById(R.id.video_view);
        mPauseButton = (Button) findViewById(R.id.pause_button);

        mControlView = LayoutInflater.from(this).inflate(R.layout.video_controller, null);
        mSeekBar = (SeekBar) mControlView.findViewById(R.id.seekbar);
        mPreButton = (Button) mControlView.findViewById(R.id.previous_button);
        mNextButton = (Button) mControlView.findViewById(R.id.next_button);

        mTotalDuration = (TextView) mControlView.findViewById(R.id.total_duration);
        mNowDuration = (TextView) mControlView.findViewById(R.id.now_duration);

        mControlWindow = new PopupWindow(mControlView);
    }

    @Override
    public void initListener() {
        mGestureDetector = new GestureDetector(this, new SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (!mPaused) {
                    mVideoView.pause();
                    mPaused = true;
                    mPauseButton.setVisibility(View.VISIBLE);
                    buttonAutoHide();
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return super.onDoubleTap(e);
            }
        });

        mPauseButton.setOnClickListener(this);
        mPreButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mVideoView.seekTo(progress);
                }
            }
        });

        mVideoView.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                showController();

                int duration = mVideoView.getDuration();

                mSeekBar.setMax(duration);

                duration /= 1000;

                int minutes = duration / 60;
                int hours = minutes / 60;
                int seconds = duration % 60;

                minutes %= 60;
                mTotalDuration.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

                mVideoView.start();

                mHandler.sendEmptyMessage(MESSAGE_PROGRESS_CHANGED);
            }
        });

        mVideoView.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.seekTo(0);
                mVideoView.pause();
                mPauseButton.setVisibility(View.VISIBLE);
                mPaused = true;
            }
        });
    }

    @Override
    public void initDatas() {
        String path = FileUtils.getVideoStorageDir().getAbsolutePath();

        Uri uri = getIntent().getData();
        if (uri != null) {
            mVideoView.stopPlayback();
            mVideoView.setVideoURI(uri);
        }

        position = getIntent().getIntExtra(Constants.EXTRA_VIDEO_POSITION, 0);

        getVideoFile(mPlayList, new File(path));

        getScreenSize();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mGestureDetector.onTouchEvent(event);
        return result;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.previous_button) {
            if (position > 0) {
                mPauseButton.setVisibility(View.GONE);
                mPaused = false;
                position--;
                mVideoView.setVideoURI(Uri.fromFile(mPlayList.get(position).getFile()));
            } else {
                ToastUtils.showToast(R.string.video_start_alert);
            }
        } else if (v.getId() == R.id.next_button) {
            if (position < mPlayList.size() - 1) {
                position++;
                mPauseButton.setVisibility(View.GONE);
                mPaused = false;
                mVideoView.setVideoURI(Uri.fromFile(mPlayList.get(position).getFile()));
            } else {
                ToastUtils.showToast(R.string.video_end_alert);
            }
        } else if (v.getId() == R.id.pause_button) {
            if (mPaused) {
                mVideoView.start();
                mPaused = false;
                mPauseButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        buttonAutoHide();
    }

    private void buttonAutoHide() {
        if (mBackButton.getVisibility() != View.VISIBLE) {
            toggleAnimation();
        }
        mAnimationHandler.removeCallbacks(hide_button_runnable);
        mAnimationHandler.postDelayed(hide_button_runnable, DISAPPEAR_INTERVAL);
    }

    private void toggleAnimation() {
        ViewAnimation.startAnimation(mBackButton, mBackButton.getVisibility() == View.VISIBLE ?
                R.anim.back_key_disappear : R.anim.back_key_appear, this);
    }

    private void getVideoFile(final LinkedList<VideoEntity> list, File file) {
        DbHelper dbHelper = DbHelper.getDbHelper();
        list.addAll(dbHelper.getAllVideos());
    }

    private void getScreenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;

        mControlHeight = mScreenHeight / 4;
    }

    private void showController() {
        mControlWindow.update(0, 0, mScreenWidth, mControlHeight);
    }

    public void onBackPressed(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mControlWindow.isShowing()) {
            mControlWindow.dismiss();
        }
        mAnimationHandler.removeCallbacks(hide_button_runnable);
    }

}
