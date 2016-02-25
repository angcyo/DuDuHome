package com.dudu.aios.ui.fragment.video;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.dudu.aios.ui.activity.MainRecordActivity;
import com.dudu.aios.ui.activity.video.PhotoListActivity;
import com.dudu.aios.ui.base.ObservableFactory;
import com.dudu.aios.ui.fragment.base.BaseFragment;
import com.dudu.aios.ui.utils.contants.FragmentConstants;
import com.dudu.android.launcher.R;

import com.dudu.drivevideo.DriveVideo;
import com.dudu.drivevideo.video.CameraPreview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2016/2/2.
 */
public class DrivingRecordFragment extends BaseFragment implements /*SurfaceHolder.Callback, */View.OnClickListener {

    private ImageButton mCheckVideoButton, mSwitchVideoButton, mTakePhotoButton, mCheckPhotoButton, mBackButton;

    private boolean isFrontCameraPreView = true;

    private Logger log;

    private MainRecordActivity mainRecordActivity;

    public DrivingRecordFragment() {
        log = LoggerFactory.getLogger("video.drivevideo");
    }

    public void setMainRecordActivity(MainRecordActivity mainRecordActivity) {
        this.mainRecordActivity = mainRecordActivity;
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

    private void initFragmentView(View view) {
        mCheckVideoButton = (ImageButton) view.findViewById(R.id.check_video);
        mSwitchVideoButton = (ImageButton) view.findViewById(R.id.switch_video);
        mCheckPhotoButton = (ImageButton) view.findViewById(R.id.check_photo);
        mTakePhotoButton = (ImageButton) view.findViewById(R.id.take_photo);
        mBackButton = (ImageButton) view.findViewById(R.id.button_back);
    }

    private void initClickListener() {
        mSwitchVideoButton.setOnClickListener(this);
        mCheckVideoButton.setOnClickListener(this);
        mCheckPhotoButton.setOnClickListener(this);
        mTakePhotoButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);

        mTakePhotoButton.setOnLongClickListener(v1 -> {
            stopRearCamera();
            return true;
        });
        mSwitchVideoButton.setOnLongClickListener(v -> {
            startRearCamera();
            return true;
        });
    }

    private void startRearCamera(){
        //后门开启后置摄像头
        DriveVideo.getInstance().getRearCameraVideoService().setIsUsbVideoEnabled(true);
        DriveVideo.getInstance().getRearCameraVideoService().startDriveVideo();
    }

    private void stopRearCamera(){
        //后门关闭后置摄像头

        DriveVideo.getInstance().getRearCameraVideoService().setIsUsbVideoEnabled(false);
        DriveVideo.getInstance().getRearCameraVideoService().stopDriveVideo();
    }



    @Override
    public void onResume() {
        super.onResume();
        mCheckPhotoButton.setEnabled(true);
        mBackButton.setEnabled(true);
        mCheckVideoButton.setEnabled(true);


        mainRecordActivity.setNoBlur();
        ObservableFactory.getInstance().getCommonObservable().hasBackground.set(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_video:
                mCheckVideoButton.setEnabled(false);
                stopRearPreview();
                replaceFragment(FragmentConstants.FRAGMENT_VIDEO_LIST);
                break;
            case R.id.take_photo:
//                DriveVideo.getInstance().getFrontCameraDriveVideo().takePicture();
                break;
            case R.id.switch_video:
                log.debug("切换显示");
                changePreview();
                break;
            case R.id.check_photo:
//                replaceFragment(FragmentConstants.FRAGMENT_PHOTO_LIST);
                mCheckPhotoButton.setEnabled(false);
                mainRecordActivity.startActivity(new Intent(mainRecordActivity, PhotoListActivity.class));
                break;
            case R.id.button_back:
                mBackButton.setEnabled(false);
                mainRecordActivity.setBlur();
                if (isFrontCameraPreView == false){
                    stopRearPreview();
                }
                ObservableFactory.getInstance().getCommonObservable().hasBackground.set(true);
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

    private void stopRearPreview(){
        ObservableFactory.getInstance().getCommonObservable().stopRearPreview();
    }

    private void startRearPreview(){
        ObservableFactory.getInstance().getCommonObservable().startRearPreview();
    }

    private void changePreview(){
        if (isFrontCameraPreView){
            isFrontCameraPreView = false;
            startRearPreview();
        }else {
            isFrontCameraPreView = true;
            stopRearPreview();
        }
    }
}
