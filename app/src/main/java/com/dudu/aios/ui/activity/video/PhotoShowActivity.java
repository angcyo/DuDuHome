package com.dudu.aios.ui.activity.video;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dudu.aios.ui.base.BaseActivity;
import com.dudu.android.launcher.R;

public class PhotoShowActivity extends BaseActivity implements View.OnClickListener{
    private Context context;

    private ImageButton btnBack, btnDelete, btnUpload, btnCancelUpload;

    private LinearLayout uploadingContainer, uploadSuccessContainer;

    private ImageView uploadingIcon, imageShow;

    private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        observableFactory.getCommonObservable(baseBinding).hasTitle.set(false);
        initView();
        initClickListener();
        showImage();
    }

    @Override
    protected View getChildView() {
        return LayoutInflater.from(this).inflate(R.layout.activity_photo_show,null);
    }

    private void initClickListener() {
        btnBack.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnCancelUpload.setOnClickListener(this);
    }

    private void initView() {
        btnBack = (ImageButton) findViewById(R.id.button_back);
        btnDelete = (ImageButton) findViewById(R.id.button_delete);
        btnUpload = (ImageButton) findViewById(R.id.button_upload);
        btnCancelUpload = (ImageButton) findViewById(R.id.button_cancel_upload);
        uploadingContainer = (LinearLayout) findViewById(R.id.uploading_container);
        uploadSuccessContainer = (LinearLayout) findViewById(R.id.upload_successful_container);
        uploadingIcon = (ImageView) findViewById(R.id.image_uploading);
        imageShow = (ImageView)findViewById(R.id.image_show);
    }

    private void showImage(){
        Uri uri = getIntent().getData();
        if (uri != null){
            imageShow.setImageURI(uri);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_back:
                finish();
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
