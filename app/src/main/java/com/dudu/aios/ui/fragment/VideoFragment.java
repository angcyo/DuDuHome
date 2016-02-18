package com.dudu.aios.ui.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dudu.aios.ui.fragment.base.BaseFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.android.launcher.R;

public class VideoFragment extends BaseFragment implements View.OnClickListener {

    private ImageButton btnBack, btnLast, btnPlay, btnNext, btnUpload, btnCancelUpload;

    private TextView tvDuration, tvNowDuration;

    private SeekBar seekBar;

    private LinearLayout uploadingContainer, uploadSuccessContainer;

    private ImageView uploadingIcon;

    private AnimationDrawable animationDrawable;

    @Override
    public View getView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_video, null);
        initFragmentView(view);
        initClickListener();
        return view;
    }

    private void initClickListener() {
        btnBack.setOnClickListener(this);
        btnLast.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        btnCancelUpload.setOnClickListener(this);
    }

    private void initFragmentView(View view) {
        btnBack = (ImageButton) view.findViewById(R.id.button_back);
        btnLast = (ImageButton) view.findViewById(R.id.button_last);
        btnPlay = (ImageButton) view.findViewById(R.id.button_play);
        btnNext = (ImageButton) view.findViewById(R.id.button_next);
        btnUpload = (ImageButton) view.findViewById(R.id.button_upload);
        btnCancelUpload = (ImageButton) view.findViewById(R.id.button_cancel_upload);
        tvDuration = (TextView) view.findViewById(R.id.tv_video_duration);
        tvNowDuration = (TextView) view.findViewById(R.id.tv_now_duration);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        uploadingContainer = (LinearLayout) view.findViewById(R.id.uploading_container);
        uploadSuccessContainer = (LinearLayout) view.findViewById(R.id.upload_successful_container);
        uploadingIcon = (ImageView) view.findViewById(R.id.image_uploading);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                replaceFragment(FragmentConstants.FRAGMENT_VIDEO_LIST);
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

    }

    private void actionPlay() {

    }

    private void actionLast() {

    }
}
