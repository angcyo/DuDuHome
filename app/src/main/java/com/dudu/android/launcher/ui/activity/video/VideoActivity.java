package com.dudu.android.launcher.ui.activity.video;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.dudu.android.launcher.R;
import com.dudu.android.launcher.ui.activity.base.BaseNoTitlebarAcitivity;
import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.ToastUtils;
import com.dudu.android.launcher.utils.ViewAnimation;
import com.dudu.video.VideoManager;

public class VideoActivity extends BaseNoTitlebarAcitivity {

    private static final int DISAPPEAR_INTERVAL = 3000;

    private VideoManager mVideoManager;

    private View mVideoView;

    private Button mBackButton;

    private ImageButton mDetailButton;

    //Back键定时消失的handler
    private Handler mAnimationHandler = new Handler();

    private Runnable button_hideRunable = new Runnable() {
        @Override
        public void run() {
            toggleAnimation();
        }
    };

    private void toggleAnimation() {
        ViewAnimation.startAnimation(mBackButton, mBackButton.getVisibility() == View.VISIBLE ?
                R.anim.back_key_disappear : R.anim.back_key_appear, this);
        ViewAnimation.startAnimation(mDetailButton, mDetailButton.getVisibility() == View.VISIBLE ?
                R.anim.camera_image_disappear : R.anim.camera_image_apear, this);
    }

    private void buttonAutoHide() {
        if (mBackButton.getVisibility() != View.VISIBLE
                && mDetailButton.getVisibility() != View.VISIBLE) {
            toggleAnimation();
        }
        mAnimationHandler.removeCallbacks(button_hideRunable);
        mAnimationHandler.postDelayed(button_hideRunable, DISAPPEAR_INTERVAL);
    }

    @Override
    public int initContentView() {
        return R.layout.video_activity;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
//        mVideoManager = VideoManager.getInstance();
        mVideoView = mVideoManager.getVideoView();

        mBackButton = (Button) mVideoView.findViewById(R.id.back_button);
        mDetailButton = (ImageButton) mVideoView.findViewById(R.id.detail_button);
    }

    @Override
    public void initListener() {
        mVideoView.findViewById(R.id.surfaceView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonAutoHide();
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoActivity.this, VideoListActivity.class);
                startActivity(intent);
            }
        });
        buttonAutoHide();
    }

    public void onBackPressed(View v) {
        finish();
    }

    @Override
    public void initDatas() {
        if (!FileUtils.isTFlashCardExists()) {
            ToastUtils.showToast(R.string.video_sdcard_removed_alert);
//            mVideoManager.startPreview();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mVideoManager.updatePreviewSize(854, 480);
        buttonAutoHide();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mVideoManager.updatePreviewSize(1, 1);
        mBackButton.setVisibility(View.GONE);
        mDetailButton.setVisibility(View.GONE);
        mAnimationHandler.removeCallbacks(button_hideRunable);
    }

}
