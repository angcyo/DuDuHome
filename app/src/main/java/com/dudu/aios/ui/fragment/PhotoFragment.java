package com.dudu.aios.ui.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dudu.aios.ui.activity.MainRecordActivity;
import com.dudu.aios.ui.fragment.base.BaseFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.android.launcher.R;

public class PhotoFragment extends BaseFragment implements View.OnClickListener {

    private ImageButton btnBack, btnDelete, btnUpload, btnCancelUpload;

    private LinearLayout uploadingContainer, uploadSuccessContainer;

    private ImageView uploadingIcon;

    private AnimationDrawable animationDrawable;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_photo, null);
        initFragmentView(view);
        initClickListener();
        return view;
    }

    private void initClickListener() {
        btnBack.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnCancelUpload.setOnClickListener(this);
    }

    private void initFragmentView(View view) {
        btnBack = (ImageButton) view.findViewById(R.id.button_back);
        btnDelete = (ImageButton) view.findViewById(R.id.button_delete);
        btnUpload = (ImageButton) view.findViewById(R.id.button_upload);
        btnCancelUpload = (ImageButton) view.findViewById(R.id.button_cancel_upload);
        uploadingContainer = (LinearLayout) view.findViewById(R.id.uploading_container);
        uploadSuccessContainer = (LinearLayout) view.findViewById(R.id.upload_successful_container);
        uploadingIcon = (ImageView) view.findViewById(R.id.image_uploading);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                replaceFragment(FragmentConstants.FRAGMENT_PHOTO_LIST);
                break;
            case R.id.button_delete:
                actionDelete();
                break;
            case R.id.button_upload:
                actionUpload();
                break;
            case R.id.button_cancel_upload:
                actionCancelUpload();
                break;
        }
    }

    public void replaceFragment(String name) {
        MainRecordActivity activity = (MainRecordActivity) getActivity();
        activity.replaceFragment(name);
    }

    private void actionCancelUpload() {
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

    private void actionDelete() {

    }
}
