package com.dudu.aios.ui.fragment.video;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.dudu.aios.ui.fragment.base.BaseFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.android.launcher.R;
import com.dudu.commonlib.CommonLib;
import com.dudu.drivevideo.DriveVideo;
import com.dudu.drivevideo.video.CameraPreview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2016/2/2.
 */
public class DrivingRecordFragment extends BaseFragment implements /*SurfaceHolder.Callback, */View.OnClickListener {

    private ImageButton mCheckVideoButton, mSwitchVideoButton, mTakePhotoButton, mCheckPhotoButton, mBackButton;
    private ImageView mRearCameraPreviewView;
    private FrameLayout previewFrameLayout;

    private CameraPreview cameraPreview = null;

    private boolean isFrontCameraPreView = true;

    private Logger log;

    public DrivingRecordFragment() {
        log = LoggerFactory.getLogger("video.drivevideo");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public View getView() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_driving_record, null);

        initFragmentView(view);

        initClickListener();

        return view;
    }

    private void initClickListener() {
        mSwitchVideoButton.setOnClickListener(this);
        mCheckVideoButton.setOnClickListener(this);
        mCheckPhotoButton.setOnClickListener(this);
        mTakePhotoButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
    }

    private void initFragmentView(View view) {
        mCheckVideoButton = (ImageButton) view.findViewById(R.id.check_video);
        mSwitchVideoButton = (ImageButton) view.findViewById(R.id.switch_video);
        mCheckPhotoButton = (ImageButton) view.findViewById(R.id.check_photo);
        mTakePhotoButton = (ImageButton) view.findViewById(R.id.take_photo);
        mBackButton = (ImageButton) view.findViewById(R.id.button_back);

//        mRearCameraPreviewView = (ImageView)view.findViewById(R.id.rear_camera_preview);
//        mRearCameraPreviewView.setVisibility(View.INVISIBLE);

        previewFrameLayout = (FrameLayout)view.findViewById(R.id.camera_preview);

//        initFrontPreview();
    }

    @Override
    public void onResume() {
        super.onResume();


//        cameraPreview.setSurfaceHolder();
//        cameraPreview.startPreview();
//        DriveVideo.getInstance().getFrontCameraDriveVideo().startPreview();
//        DriveVideo.getInstance().getRearCameraDriveVideo().setImageView(mRearCameraPreviewView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        DriveVideo.getInstance().getRearCameraDriveVideo().stopPreview();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_video:
                replaceFragment(FragmentConstants.FRAGMENT_VIDEO_LIST);
                break;
            case R.id.take_photo:
                DriveVideo.getInstance().getFrontCameraDriveVideo().takePicture();
                break;
            case R.id.switch_video:
                log.debug("切换显示");
                changePreview();
                break;
            case R.id.check_photo:
                replaceFragment(FragmentConstants.FRAGMENT_PHOTO_LIST);
                break;
            case R.id.button_back:
                replaceFragment(FragmentConstants.FRAGMENT_MAIN_PAGE);
                break;
        }
    }


    private void stopFrontPreview(){
        if (DriveVideo.getInstance().getFrontCameraDriveVideo().getmCamera()  != null){
            log.debug("停止前置预览");
            DriveVideo.getInstance().getFrontCameraDriveVideo().getCameraPreview().stopPreview();
        }
    }

    private void startFrontPreview(){
        if (DriveVideo.getInstance().getFrontCameraDriveVideo().getmCamera() != null) {
            log.debug("开启前置预览");
            DriveVideo.getInstance().getFrontCameraDriveVideo().getCameraPreview().startPreview();
        }
    }

    private void changePreview(){
        if (isFrontCameraPreView){
            isFrontCameraPreView = false;

//            stopFrontPreview();

//            DriveVideo.getInstance().getRearCameraDriveVideo().setImageView(mRearCameraPreviewView);
//            DriveVideo.getInstance().getRearCameraDriveVideo().startPreview();
//            mRearCameraPreviewView.setVisibility(View.VISIBLE);
        }else {
            isFrontCameraPreView = true;
//            mRearCameraPreviewView.setVisibility(View.INVISIBLE);
//            DriveVideo.getInstance().getRearCameraDriveVideo().stopPreview();

//            startFrontPreview();
        }
    }
}
